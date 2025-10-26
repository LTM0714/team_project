package com.myarea.myarea.service;

import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Location;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.SubSubLocation;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.repository.LocationRepository;
import com.myarea.myarea.repository.PostRepository;
import com.myarea.myarea.repository.SubSubLocationRepository;
import com.myarea.myarea.repository.UserRepository;
import org.springframework.data.domain.*;
import com.myarea.myarea.response.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private static final int MAX_IMAGES = 5;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private SubSubLocationService subSubLocationService;
    @Autowired
    private S3UrlService s3UrlService;
    @Autowired
    private AWS_S3Service awsS3Service;


    public List<PostDto> index() {
        return postRepository.findAll().stream()
                .map(p -> PostDto.fromEntity(p, s3UrlService::cloudfront))
                .collect(Collectors.toList()); }

    public PostDto show(Long post_id) {
        return postRepository.findById(post_id)
                .map(p -> PostDto.fromEntity(p, s3UrlService::cloudfront))
                .orElseThrow(()->new EntityNotFoundException("Post not found")); }

    public PageResponse<PostDto> findAllWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findAll(pageable);

        Function<Post, PostDto> mapper = p -> PostDto.fromEntity(p, s3UrlService::cloudfront);
        Page<PostDto> dtoPage = postPage.map(mapper);

        return new PageResponse<>(dtoPage);
    }

    public PostDto create(PostDto dto, User user, List<MultipartFile> files) {
        Location location = null;

        boolean hasManualLocation = dto.getLatitude() != null && dto.getLongitude() != null && dto.getAddress() != null;
        boolean hasSelectedLocation = dto.getSubsubId() != null;

        // GPS + subsubId 동시 입력한 경우 → 예외 처리
        if (hasManualLocation && hasSelectedLocation) {
            throw new IllegalArgumentException("사진 업로드 시 사진 메타데이터가 있을 경우와 지역 선택 중 하나만 선택해 주세요.");
        }

        // 1. GPS 정보(위도/경도/주소)가 모두 있는 경우
        if (hasManualLocation) {
            location = new Location();
            location.setLatitude(dto.getLatitude());
            location.setLongitude(dto.getLongitude());
            location.setAddress(dto.getAddress());

            location = locationRepository.save(location); // Location 저장
        }

        // 2. 사용자가 선택한 subsubId로 자동 설정
        else if (hasSelectedLocation) {
            SubSubLocation subSubLocation = subSubLocationService.findById(dto.getSubsubId());

            location = new Location();
            location.setLatitude(subSubLocation.getLatitude());
            location.setLongitude(subSubLocation.getLongitude());
            location.setAddress(subSubLocation.getAddress());

            location = locationRepository.save(location);
        }

        Post saved = postRepository.save(dto.toEntity(user, location));

        List<String> keys = new ArrayList<>();
        try {
            if (files != null && !files.isEmpty()) {
                keys = awsS3Service.uploadFile(saved.getPostId(), files); // 네트워크 작업
                saved.setImageKeyList(keys);
                saved = postRepository.save(saved); // 이미지 반영된 후 저장
            }
            return PostDto.fromEntity(saved, s3UrlService::cloudfront);
        } catch (Exception e) {
            if (!keys.isEmpty()) {
                awsS3Service.deleteFiles(keys); // 실패 시 업로드 파일 삭제
            }
            throw new RuntimeException("게시물 생성 중 오류 발생", e);
        }
    }

    public Post update(Long post_id, PostDto dto, User user, List<MultipartFile> files) {
        Post target = postRepository.findById(post_id).orElse(null);

        if (target == null) return null;

        // 작성자 확인
        if (!target.getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            return null; // 권한 없음
        }

        Location location = target.getLocation();

        // GPS + subsubId 동시 입력한 경우 → 예외 처리
        if ((dto.getLatitude() != null || dto.getLongitude() != null || dto.getAddress() != null)
                && dto.getSubsubId() != null) {
            throw new IllegalArgumentException("GPS 메타데이터와 직접 선택한 위치는 동시에 입력할 수 없습니다.");
        }

        // 1. GPS 정보로 수정
        if (dto.getLatitude() != null && dto.getLongitude() != null && dto.getAddress() != null) {
            if (location == null) {
                location = new Location();
            }
            location.setLatitude(dto.getLatitude());
            location.setLongitude(dto.getLongitude());
            location.setAddress(dto.getAddress());

            location = locationRepository.save(location);
        }

        // 2. subsubId로 수정
        else if (dto.getSubsubId() != null) {
            SubSubLocation subSubLocation = subSubLocationService.findById(dto.getSubsubId());

            if (location == null) {
                location = new Location();
            }
            location.setLatitude(subSubLocation.getLatitude());
            location.setLongitude(subSubLocation.getLongitude());
            location.setAddress(subSubLocation.getAddress());

            location = locationRepository.save(location);
        }

        // patch
        target.patch(dto.toEntity(user, location));

        // 이미지 업데이트
        List<String> existingKeys = Optional.ofNullable(dto.getExistingImageKeys())
                .orElseGet(ArrayList::new);

        List<String> newlyUploadedKeys = new ArrayList<>();
        if(files != null && !files.isEmpty()) {
            if(existingKeys.size() + files.size() > MAX_IMAGES) {
                throw new IllegalArgumentException("이미지는 최대 " + MAX_IMAGES + "개까지 업로드할 수 있습니다.");
            }
            newlyUploadedKeys = awsS3Service.uploadFile(post_id, files);
        }

        // S3에서 삭제 대상 찾기
        List<String> currentKeys = target.getImageKeyList();
        List<String> toDelete = currentKeys.stream()
                .filter(k -> !existingKeys.contains(k))
                .collect(Collectors.toList());

        awsS3Service.deleteFiles(toDelete);

        // 최종 이미지 키 세팅 → DB 반영
        List<String> finalKeys = new ArrayList<>(existingKeys);
        finalKeys.addAll(newlyUploadedKeys);
        target.setImageKeyList(finalKeys);

        return postRepository.save(target);
    }

    public boolean delete(Long post_id, User user) {
        Post target = postRepository.findById(post_id).orElse(null);

        if (target == null) return false;

        // 작성자 확인
        if (!target.getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            return false; // 권한 없음
        }

        List<String> keysToDelete = new ArrayList<>(target.getImageKeyList());

        postRepository.delete(target);

        // 3) S3 삭제 (예외는 삼켜서 DB 롤백 안 되게)
        try {
            if (!keysToDelete.isEmpty()) {
                awsS3Service.deleteFiles(keysToDelete);
            }
        } catch (Exception e) {
            log.warn("S3 삭제 실패 postId={}, keys={}", post_id, keysToDelete, e);
        }
        return true;
    }

} 