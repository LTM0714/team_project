package com.myarea.myarea.service;

import com.myarea.myarea.dto.PostRequestDto;
import com.myarea.myarea.dto.PostResponseDto;
import com.myarea.myarea.entity.Location;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.repository.LocationRepository;
import com.myarea.myarea.repository.PostRepository;
import com.myarea.myarea.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public PostResponseDto createPost(Long userId, PostRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Location location;
        if (requestDto.getLocationId() != null) {
            location = locationRepository.findById(requestDto.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found"));
        } else {
            location = new Location();
            location.setLatitude(requestDto.getLatitude());
            location.setLongitude(requestDto.getLongitude());
            location.setLocationName(requestDto.getLocationName());
            location = locationRepository.save(location);
        }

        Post post = new Post();
        post.setUser(user);
        post.setImageUrl(requestDto.getImageUrl());
        post.setBody(requestDto.getBody());
        post.setLocation(location);

        Post savedPost = postRepository.save(post);
        return convertToResponseDto(savedPost);
    }

    public Page<PostResponseDto> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }

    public Page<PostResponseDto> getPostsByUser(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable)
                .map(this::convertToResponseDto);
    }

    public Page<PostResponseDto> getPostsByLocation(Long locationId, Pageable pageable) {
        return postRepository.findByLocationId(locationId, pageable)
                .map(this::convertToResponseDto);
    }

    public Page<PostResponseDto> searchPostsByLocationName(String keyword, Pageable pageable) {
        return postRepository.findByLocationNameContaining(keyword, pageable)
                .map(this::convertToResponseDto);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, Long userId, PostRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Only the post author can update the post");
        }

        post.setImageUrl(requestDto.getImageUrl());
        post.setBody(requestDto.getBody());

        if (requestDto.getLocationId() != null) {
            Location location = locationRepository.findById(requestDto.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found"));
            post.setLocation(location);
        }

        return convertToResponseDto(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Only the post author can delete the post");
        }

        postRepository.delete(post);
    }

    private PostResponseDto convertToResponseDto(Post post) {
        PostResponseDto responseDto = new PostResponseDto();
        responseDto.setPostId(post.getPostId());
        responseDto.setUserId(post.getUser().getId());
        responseDto.setImageUrl(post.getImageUrl());
        responseDto.setBody(post.getBody());
        responseDto.setCreatedAt(post.getCreatedAt());

        PostResponseDto.LocationDto locationDto = new PostResponseDto.LocationDto();
        locationDto.setId(post.getLocation().getId());
        locationDto.setLatitude(post.getLocation().getLatitude());
        locationDto.setLongitude(post.getLocation().getLongitude());
        locationDto.setLocationName(post.getLocation().getLocationName());
        responseDto.setLocation(locationDto);

        return responseDto;
    }
} 