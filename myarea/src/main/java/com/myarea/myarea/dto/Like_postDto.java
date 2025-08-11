package com.myarea.myarea.dto;

import com.myarea.myarea.entity.Like_post;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Like_postDto {
    private Long id;
    private Long user_id;
    private Long post_id;
    private LocalDateTime liked_at;

    public static Like_postDto toDto(Like_post like_post) {
        return new Like_postDto(
                like_post.getId(),
                like_post.getUser_id().getId(),
                like_post.getPost_id().getPostId(),
                like_post.getLiked_at()
        );
    }
}
