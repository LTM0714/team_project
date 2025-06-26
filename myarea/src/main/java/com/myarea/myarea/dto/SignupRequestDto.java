package com.myarea.myarea.dto;

import com.myarea.myarea.entity.User;
import com.myarea.myarea.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 회원가입 시 데이터 받는 폼
@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {
    @NotBlank(message = "email을 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    private String profileImage;

    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .profileImage(this.profileImage)
                .role(UserRole.USER)
                .build();
    }
}