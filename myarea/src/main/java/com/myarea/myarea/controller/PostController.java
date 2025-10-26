package com.myarea.myarea.controller;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.jwt.JwtUtil;
import com.myarea.myarea.repository.UserRepository;
import com.myarea.myarea.response.PageResponse;
import com.myarea.myarea.service.AWS_S3Service;
import com.myarea.myarea.service.KakaoMapService;
import com.myarea.myarea.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private KakaoMapService kakaoMapService;

    // 전체 게시물 조회
    @GetMapping
    public List<PostDto> index(){ return postService.index(); }

    // postId를 이용한 게시물 조회
    @GetMapping("{id}")
    public PostDto show(@PathVariable Long id){
        return postService.show(id);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "18") int size) {

        return ResponseEntity.ok(postService.findAllWithPaging(page, size));
    }

    // 게시물 생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@ModelAttribute PostDto dto,
                                    @RequestParam(value = "multipartFile", required = false) List<MultipartFile> files,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader){
        try{
            // 1. Authorization 헤더가 없거나 Bearer 형식이 아닌 경우
            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token missing or invalid format");
            }

            // 2. 토큰 추출
            String token = authHeader.substring("Bearer ".length());

            // 3. 토큰 유효성 검사
            if(!jwtUtil.validateToken(token)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }
            
            // 4. 사용자 정보 추출
            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getUserRoleFromToken(token); // 사용하지 않지만 추후 등급에 따른 범위 제한
            
            // 5. 사용자 조회
            User user = userRepository.findByEmail(email);
            if(user == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            if (files != null && !files.isEmpty()) {
                MultipartFile firstFile = files.get(0);

                try (InputStream in = firstFile.getInputStream()) {
                    Metadata metadata = ImageMetadataReader.readMetadata(in);

                    GpsDirectory gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);
                    if (gpsDir != null) {
                        GeoLocation geoLocation = gpsDir.getGeoLocation();
                        if (geoLocation != null) {
                            double latitude = geoLocation.getLatitude();
                            double longitude = geoLocation.getLongitude();
                            String address = kakaoMapService.getAddressFromCoords(longitude, latitude);

                            dto.setLatitude(latitude);
                            dto.setLongitude(longitude);
                            dto.setAddress(address);

                            System.out.println("Extracted GPS -> lat: " + latitude + ", lon: " + longitude + ", address: " + address);
                        }
                    }
                }
            }

            // 6. 게시물 생성
            PostDto created = postService.create(dto, user, files);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating post: " + e.getMessage());
        }
    }

    // 게시물 수정
    @PatchMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @ModelAttribute PostDto dto,
                                    @RequestParam(value = "multipartFile", required = false) List<MultipartFile> files,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 1. 토큰 존재 및 형식 확인
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token missing or invalid format");
            }

            // 2. 토큰 추출
            String token = authHeader.substring("Bearer ".length());

            // 3. 토큰 유효성 검사
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }

            // 4. 사용자 정보 추출
            String email = jwtUtil.getEmailFromToken(token);

            // 5. 사용자 조회
            User user = userRepository.findByEmail(email);
            if(user == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // 6. 게시물 수정
            Post updated = postService.update(id, dto, user, files);

            // 7. 작성자 불일치 또는 실패 시 권한 없음 응답
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the author of this post.");
            }

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating post: " + e.getMessage());
        }
    }    

    // 게시물 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader){
        try {
            // 1. 토큰 존재 및 형식 확인
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token missing or invalid format");
            }

            // 2. 토큰 추출
            String token = authHeader.substring("Bearer ".length());

            // 3. 토큰 유효성 검사
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }

            // 4. 사용자 정보 추출
            String email = jwtUtil.getEmailFromToken(token);
            User user = userRepository.findByEmail(email);

            // 5. 게시물 삭제
            boolean deleted = postService.delete(id, user);

            // 6. 작성자 불일치 또는 실패 시 권한 없음 응답
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the author of this post.");
            }

            return ResponseEntity.ok("Post deleted successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting post: " + e.getMessage());
        }
    }

} 