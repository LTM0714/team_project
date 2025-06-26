package com.myarea.myarea.controller;

import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Post;
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
    public ResponseEntity<Post> create(@RequestBody PostDto dto){
        Post created = postService.create(dto);
        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 게시물 수정
    @PatchMapping("{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody PostDto dto){
        Post updated = postService.update(id, dto);
        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 게시물 삭제
    @DeleteMapping("{id}")
    public ResponseEntity<Post> delete(@PathVariable Long id){
        Post deleted = postService.delete(id);
        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
} 