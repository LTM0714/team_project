package com.myarea.myarea.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponseDto {
    // 클라이언트에게 반환되는 게시물 데이터
    private Long postId;
    private Long userId;
    private String imageUrl;
    private String body;
    private LocationDto location;
    private LocalDateTime createdAt;

    @Getter
    @Setter
    public static class LocationDto {
        private Long id;
        private Double latitude;
        private Double longitude;
        private String locationName;
    }
} 