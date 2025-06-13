package com.myarea.myarea.service;

import com.myarea.myarea.dto.PostDto;
import com.myarea.myarea.entity.Location;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.repository.LocationRepository;
import com.myarea.myarea.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDto getPostDtoById(Long postId) {
        Post post = getPostById(postId);
        return convertToDto(post);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
    }

    @Transactional
    public PostDto createPost(PostDto postDto, User user) {
        Post post = new Post();
        post.setImageUrl(postDto.getImageUrl());
        post.setBody(postDto.getBody());
        post.setUser(user);
        
        if (postDto.getLocId() != null) {
            Location location = locationRepository.findById(postDto.getLocId())
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + postDto.getLocId()));
            post.setLocation(location);
        }
        
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }

    @Transactional
    public PostDto updatePost(Long postId, PostDto postDto) {
        Post post = getPostById(postId);
        
        if (postDto.getImageUrl() != null) {
            post.setImageUrl(postDto.getImageUrl());
        }
        if (postDto.getBody() != null) {
            post.setBody(postDto.getBody());
        }
        if (postDto.getLocId() != null) {
            Location location = locationRepository.findById(postDto.getLocId())
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + postDto.getLocId()));
            post.setLocation(location);
        }
        
        Post updatedPost = postRepository.save(post);
        return convertToDto(updatedPost);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getPostById(postId);
        postRepository.delete(post);
    }

    private PostDto convertToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUser().getId());
        dto.setImageUrl(post.getImageUrl());
        dto.setBody(post.getBody());
        if (post.getLocation() != null) {
            dto.setLocId(post.getLocation().getLocId());
        }
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
} 