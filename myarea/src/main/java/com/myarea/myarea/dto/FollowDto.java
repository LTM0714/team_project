package com.myarea.myarea.dto;

import com.myarea.myarea.entity.Follow;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FollowDto {
    private Long id;
    private Long user_id;
    private Long followed_user_id;
    private LocalDateTime created_at;

    public static FollowDto toDto(Follow follow) {
        return new FollowDto(
                follow.getId(),
                follow.getUser_id().getId(),
                follow.getFollowed_user_id().getId(),
                follow.getCreated_at()
        );
    }
}
