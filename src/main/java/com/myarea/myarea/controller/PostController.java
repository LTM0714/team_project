package com.myarea.myarea.controller;

import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.service.PostService;
import com.myarea.myarea.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    @Autowired
    private PostService postService;
    private final UserService userService;

    // 전체 게시물 조회
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // post_id를 이용한 게시물 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDtoById(postId));
    }

    // 게시물 생성
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getLoginUserById(userId);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        return ResponseEntity.ok(postService.createPost(postDto, user));
    }

    // 게시물 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long postId, 
                                            @RequestBody PostDto postDto,
                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getLoginUserById(userId);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        
        Post post = postService.getPostById(postId);
        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to update this post");
        }
        
        return ResponseEntity.ok(postService.updatePost(postId, postDto));
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getLoginUserById(userId);
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }
        
        Post post = postService.getPostById(postId);
        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }
        
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

} 