package com.myarea.myarea.controller;

import com.myarea.myarea.dto.CommentDto;
import com.myarea.myarea.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    @Autowired
    private CommentService commentService;

    //1. 댓글 조회
    @GetMapping("/api/posts/{post_id}/comments")
    public ResponseEntity<List<CommentDto>> comments(@PathVariable Long post_id) {
        //서비스에 위임
        List<CommentDto> dtos = commentService.comments(post_id);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    // 댓글 생성
    @PostMapping("/api/posts/{post_Id}/comments")
    public ResponseEntity<CommentDto> create(@PathVariable Long post_Id, @RequestBody CommentDto dto,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        dto.setUserId(userId);

        //서비스에 위임
        CommentDto createdDto = commentService.create(post_Id, dto, userId);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }


    //3. 댓글 수정
    @PatchMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable Long id, @RequestBody CommentDto dto,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        dto.setUserId(userId);

        //서비스에 위임
        CommentDto updatedDto = commentService.update(id, dto, userId);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    //4. 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<CommentDto> delete(@PathVariable Long id, @RequestBody CommentDto dto,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        dto.setUserId(userId);

        //서비스에 위임
        CommentDto deletedDto = commentService.delete(id, userId);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(deletedDto);
    }
}