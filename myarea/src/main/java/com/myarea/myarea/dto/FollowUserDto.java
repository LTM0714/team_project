package com.myarea.myarea.dto;

import com.myarea.myarea.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FollowUserDto {
    private Long id;
    private String email;
    private String name;
    private String profileImage;
    private LocalDateTime lastLoginAt;

    public static FollowUserDto toDto(User user) {
        return new FollowUserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileImage(),
                user.getLastLoginAt()
        );
    }
}
