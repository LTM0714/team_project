package com.myarea.myarea.service;

import com.myarea.myarea.dto.LoginRequestDto;
import com.myarea.myarea.dto.SignupRequestDto;
import com.myarea.myarea.dto.UserDto;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User authenticate(String email, String rawPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return null; // 이메일이 없으면 실패
        }

        User user = optionalUser.get();
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user; // 비밀번호 일치 → 인증 성공
        }
        return null; // 비밀번호 불일치 → 인증 실패
    }

    @Transactional
    public UserDto signup(SignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setName(requestDto.getName());
        user.setProfileImage(requestDto.getProfileImage());
        user.setLastLoginAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Transactional
    public UserDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        user.setLastLoginAt(LocalDateTime.now());
        return convertToDto(user);
    }

    public User getLoginUserById(Long userId) {
        if(userId == null) return null;
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) return null;
        return optionalUser.get();
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setUserRole(String.valueOf(user.getUserRole()));
        dto.setProfileImage(user.getProfileImage());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setEditedAt(user.getEditedAt());
        dto.setLastLoginAt(user.getLastLoginAt());
        return dto;
    }
}