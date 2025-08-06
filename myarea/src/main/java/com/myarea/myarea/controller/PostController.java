package com.myarea.myarea.controller;

import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.jwt.JwtUtil;
import com.myarea.myarea.repository.UserRepository;
import com.myarea.myarea.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    // 전체 게시물 조회
    @GetMapping
    public List<Post> index(){
        return postService.index();
    }

    // postId를 이용한 게시물 조회
    @GetMapping("{id}")
    public Post show(@PathVariable Long id){
        return postService.show(id);
    }

    // 게시물 생성
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PostDto dto,
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
            
            // 6. 게시물 생성
            Post created = postService.create(dto, user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating post: " + e.getMessage());
        }
    }
/*
    // 게시물 수정
    @PatchMapping("{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PostDto dto,
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
            User user = userRepository.findByEmail(email);
            if(user == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // 5. 게시물 수정
            PostDto updated = postService.update(id, dto, user);

            // 6. 작성자 불일치 또는 실패 시 권한 없음 응답
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the author of this post.");
            }

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating post: " + e.getMessage());
        }
    }    
*/
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