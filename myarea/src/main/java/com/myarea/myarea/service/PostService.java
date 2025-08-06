package com.myarea.myarea.service;

import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.repository.LocationRepository;
import com.myarea.myarea.repository.PostRepository;
import com.myarea.myarea.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;


    public List<Post> index() { return postRepository.findAll(); }

    public Post show(Long post_id) { return postRepository.findById(post_id).orElse(null); }

    public Post create(PostDto dto, User user) {
        Post post = dto.toEntity(user);

        if(post.getPostId() != null){
            return null;
        }
        return postRepository.save(post);
    }

    public PostDto update(Long post_id, PostDto dto, User user) {
        Post target = postRepository.findById(post_id).orElse(null);

        if (target == null) return null;

        // 작성자 확인
        if (!target.getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            return null; // 권한 없음
        }

        // patch
        target.patch(dto.toEntity(user));
        Post updated = postRepository.save(target);
        return PostDto.fromEntity(updated);
    }

    public boolean delete(Long post_id, User user) {
        Post target = postRepository.findById(post_id).orElse(null);

        if (target == null) return false;

        // 작성자 확인
        if (!target.getUser().getId().equals(user.getId()) &&
                !user.getRole().name().equals("ADMIN")) {
            return false; // 권한 없음
        }

        postRepository.delete(target);
        return true;
    }

} 