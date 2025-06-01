package com.myarea.myarea.controller;

import com.myarea.myarea.dto.PostRequestDto;
import com.myarea.myarea.dto.PostResponseDto;
import com.myarea.myarea.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시물 생성
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @RequestParam Long userId,
            @RequestBody PostRequestDto requestDto) {
        return ResponseEntity.ok(postService.createPost(userId, requestDto));
    }

    // 전체 게시물 조회(페이지네이션 지원)
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    // 특정 사용자 게시물 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponseDto>> getPostsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByUser(userId, pageable));
    }

    // 특정 위치의 게시물 조회(location_id로 하는거라 변경해야 할 예정)
    @GetMapping("/location/{locationId}")
    public ResponseEntity<Page<PostResponseDto>> getPostsByLocation(
            @PathVariable Long locationId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByLocation(locationId, pageable));
    }

    // 장소 이름으로 게시물 검색
    @GetMapping("/search")
    public ResponseEntity<Page<PostResponseDto>> searchPostsByLocationName(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(postService.searchPostsByLocationName(keyword, pageable));
    }

    // 게시물 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestBody PostRequestDto requestDto) {
        return ResponseEntity.ok(postService.updatePost(postId, userId, requestDto));
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        postService.deletePost(postId, userId);
        return ResponseEntity.ok().build();
    }
} 