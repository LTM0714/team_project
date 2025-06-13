package com.myarea.myarea.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    private Long postId;
    private Long userId;
    private String imageUrl;
    private String body;
    private Long locId;
    private LocalDateTime createdAt;
}
