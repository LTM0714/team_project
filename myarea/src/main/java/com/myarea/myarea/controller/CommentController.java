package com.myarea.myarea.controller;

import com.myarea.myarea.dto.CommentDto;
import com.myarea.myarea.jwt.JwtUtil;
import com.myarea.myarea.service.CommentService;
import com.myarea.myarea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    //1. 댓글 조회
    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> comments(@PathVariable Long postId) {
        //서비스에 위임
        List<CommentDto> dtos = commentService.comments(postId);
        //결과 응답
        return ResponseEntity.ok(dtos);
    }

    //2. 댓글 생성
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable Long postId, @RequestBody CommentDto dto,
                                             @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        //서비스에 위임
        CommentDto createdDto = commentService.create(postId, dto, userId);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }


    //3. 댓글 수정
    @PatchMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable Long id, @RequestBody CommentDto dto,
                                             @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        //서비스에 위임
        CommentDto updatedDto = commentService.update(id, dto, userId);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    //4. 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> delete(@PathVariable Long id,
                                             @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        //서비스에 위임
        CommentDto deletedDto = commentService.delete(id, userId);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(deletedDto);
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
