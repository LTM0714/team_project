package com.myarea.myarea.controller;

import com.myarea.myarea.dto.Like_postDto;
import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.jwt.JwtUtil;
import com.myarea.myarea.service.Like_postService;
import com.myarea.myarea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/like_post")
public class Like_postController {
    @Autowired
    private Like_postService like_postService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @GetMapping
    public List<PostDto> getLike_post(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        return like_postService.getLike_post(userId);
    }

    @PostMapping("/{post_id}")
    public ResponseEntity<Like_postDto> addLike_post(@PathVariable Long post_id,
                                                     @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        Like_postDto dto = like_postService.addLike_post(userId, post_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<Like_postDto> deleteLike_post(@PathVariable Long post_id,
                                                        @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        Like_postDto dto = like_postService.deleteLike_post(userId, post_id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // JWT 토큰에서 userId 추출
    private Long extractUserId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 없습니다.");
        }

        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }

        String email = jwtUtil.getEmailFromToken(token);
        return userService.findByEmail(email).getId();
    }
}
