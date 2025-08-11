package com.myarea.myarea.dto;

import com.myarea.myarea.entity.Like_location;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Like_locationDto {
    private Long id;
    private Long user_id;
    private Long subloc_id;
    private LocalDateTime liked_at;

    public static Like_locationDto toDto(Like_location like_location) {
        return new Like_locationDto(
                like_location.getId(),
                like_location.getUser_id().getId(),
                like_location.getSubloc_id().getSubloc_id(),
                like_location.getLiked_at()
        );
    }
}
