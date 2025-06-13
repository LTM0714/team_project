package com.myarea.myarea.dto;

import com.myarea.myarea.entity.User;
import com.myarea.myarea.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {
    private String email;
    private String password;
    private String name;
    private String profileImage;

    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .profileImage(this.profileImage)
                .userRole(UserRole.USER)
                .build();

    }
}