package com.myarea.myarea.dto;

import com.myarea.myarea.entity.User;
import com.myarea.myarea.entity.UserRole;
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
    private UserRole userRole;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private LocalDateTime lastLoginAt;

    public User toEntity() { return new User(id, email, password, name, userRole, profileImage, createdAt, editedAt, lastLoginAt); }
}