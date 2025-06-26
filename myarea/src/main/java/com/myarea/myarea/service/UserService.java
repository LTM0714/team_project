package com.myarea.myarea.service;

import com.myarea.myarea.dto.LoginRequestDto;
import com.myarea.myarea.dto.SignupRequestDto;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.entity.UserRole;
import com.myarea.myarea.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 로그인 email 중복 검사 메서드
    public boolean checkEmailDuplicate(String email){
        return userRepository.existsByEmail(email);
    }

    // 회원가입 메서드
    public void signup(SignupRequestDto signupRequestDto){
        User user = new User();
        user.setEmail(signupRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        user.setName(signupRequestDto.getName());
        user.setProfileImage(signupRequestDto.getProfileImage());
        user.setRole(UserRole.USER);
        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // 로그인 메서드
    public User login(LoginRequestDto loginRequestDto){
        User findUser = userRepository.findByEmail(loginRequestDto.getEmail());

        if (findUser == null) {
            return null;
        }

        // 비밀번호 암호화 비교
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), findUser.getPassword())) {
            return null;
        }

        return findUser;
    }

    // 이메일로 찾기
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 로그인한 User 반환 메서드
    public User getLoginUserById(Long id){
        if (id == null) return null;

        Optional<User> findUser = userRepository.findById(id);
        return findUser.orElse(null);
    }
}