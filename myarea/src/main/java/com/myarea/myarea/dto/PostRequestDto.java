package com.myarea.myarea.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    // 이미지 생성/수정 시 필요한 데이터
    private String imageUrl;
    private String body;
    private Long locationId;
    private Double latitude;
    private Double longitude;
    private String locationName;
} 