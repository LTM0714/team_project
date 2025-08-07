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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private SubSubLocationService subSubLocationService;


    public List<Post> index() { return postRepository.findAll(); }

    public Post show(Long post_id) { return postRepository.findById(post_id).orElse(null); }

    public Post create(PostDto dto, User user) {
        Location location = null;

        boolean hasManualLocation = dto.getLatitude() != null && dto.getLongitude() != null && dto.getAddress() != null;
        boolean hasSelectedLocation = dto.getSubsubId() != null;

        // GPS + subsubId 동시 입력한 경우: 예외 처리
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

        Post post = dto.toEntity(user, location);

        return postRepository.save(post);
    }
/*
    public PostDto update(Long post_id, PostDto dto, User user) {
        Post target = postRepository.findById(post_id).orElse(null);

        if (target == null) return null;

        // 작성자 확인
        if (!target.getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            return null; // 권한 없음
        }

        // patch
        target.patch(dto.toEntity(user));
        Post updated = postRepository.save(target);
        return PostDto.fromEntity(updated);
    }
*/
    public boolean delete(Long post_id, User user) {
        Post target = postRepository.findById(post_id).orElse(null);

        if (target == null) return false;

        // 작성자 확인
        if (!target.getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            return false; // 권한 없음
        }

        postRepository.delete(target);
        return true;
    }

} 