package com.myarea.myarea.service;

import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Slf4j
@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public List<Post> index() {
        return postRepository.findAll();
    }

    public Post show(Long id) {
        return postRepository.findById(id).orElse(null);
    }


    public Post create(PostDto dto) {
        Post post = dto.toEntity();
        if(post.getPostId() != null){
            return null;
        }
        return postRepository.save(post);
    }

    public Post update(Long id, PostDto dto) {
        // 1. DTO -> 엔티티 변환하기
        Post post=dto.toEntity();
        log.info("id: {}, article: {}", id, post.toString());
        // 2. 타깃 조회하기
        Post target=postRepository.findById(id).orElse(null);
        // 3. 잘못된 요청 처리하기
        if(target == null || id != post.getPostId()){
            log.info("잘못된 요청! id: {}, article: {}", id, post.toString());
            return null;
        }
        // 4. 업데이트하기
        target.patch(post);
        Post updated=postRepository.save(target);
        return updated;
    }

    public Post delete(Long id) {
        // 1. 대상 찾기
        Post target = postRepository.findById(id).orElse(null);
        // 2. 잘못된 요청 처리하기
        if(target == null){
            return null;
        }
        // 3. 대상 삭제하기
        postRepository.delete(target);
        return target;
    }
} 