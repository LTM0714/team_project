package com.myarea.myarea.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String userRole;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private LocalDateTime lastLoginAt;
}