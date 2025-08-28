package com.myarea.myarea.service;

import com.myarea.myarea.dto.Like_postDto;
import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Like_post;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.repository.Like_postRepository;
import com.myarea.myarea.repository.PostRepository;
import com.myarea.myarea.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class Like_postService {
    @Autowired
    private Like_postRepository like_postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private S3UrlService s3UrlService;

    public List<PostDto> getLike_post(Long user_id) {
        List<Long> post_ids = like_postRepository.findByUser_id(user_id)
                .stream()
                .map(like -> like.getPost_id().getPostId())
                .collect(Collectors.toList());

        return postRepository.findAllById(post_ids).stream()
                .map(p -> PostDto.fromEntity(p, s3UrlService::toViewUrl))
                .collect(Collectors.toList());
    }

    public Like_postDto addLike_post(Long user_id, Long post_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new IllegalArgumentException("로그인 해야 합니다"));
        Post post = postRepository.findById(post_id).orElseThrow(() -> new IllegalArgumentException("대상 게시글이 없습니다."));

        Like_postDto dto = new Like_postDto();
        Like_post like_post = Like_post.addLike_post(dto, user, post);
        Like_post addLike_post = like_postRepository.save(like_post);
        return Like_postDto.toDto(addLike_post);
    }

    public Like_postDto deleteLike_post(Long user_id, Long post_id) {
        Like_post target = like_postRepository.findById(user_id, post_id);

        like_postRepository.delete(target);
        return Like_postDto.toDto(target);
    }
}
