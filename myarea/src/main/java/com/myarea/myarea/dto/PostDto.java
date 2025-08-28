package com.myarea.myarea.dto;

import com.myarea.myarea.entity.Location;
import com.myarea.myarea.entity.Post;
import com.myarea.myarea.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Builder
public class PostDto {
    private Long postId;
    private Long userId;
    private List<ImageResponse> images;
    private List<String> existingImageKeys;
    private String body;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long subsubId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter @Setter @Builder
    @AllArgsConstructor @NoArgsConstructor
    public static class ImageResponse {
        private Long id;
        private String s3Key;
        private Integer order;
        private String url;
    }

    // 전송 받은 이미지와 내용을 필드에 저장하는 생성자 추가 --> @AllArgsConstructor

    // 데이터를 잘 받았는지 확인할 toString() 메서드 추가 --> @ToString

    //폼 데이터를 받은 DTO객체를 엔티티로 반환
    public Post toEntity(User user, Location location) {

        return new Post(postId, user, null, body, location, createdAt, updatedAt);
    }

    public static PostDto fromEntity(Post post, java.util.function.Function<String, String> keyToUrl) {
        return PostDto.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getId())
                .images(
                        post.getImages().stream()
                                .map(img -> ImageResponse.builder()
                                        .id(img.getId())
                                        .s3Key(img.getS3_key())
                                        .order(img.getDisplayOrder())
                                        .url(keyToUrl == null ? null : keyToUrl.apply(img.getS3_key()))
                                        .build())
                                .toList()
                )
                .body(post.getBody())
                .latitude(post.getLocation().getLatitude())
                .longitude(post.getLocation().getLongitude())
                .address(post.getLocation().getAddress())
                .subsubId(null)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
