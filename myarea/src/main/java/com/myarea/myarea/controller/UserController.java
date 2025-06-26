package com.myarea.myarea.controller;

import com.myarea.myarea.jwt.JwtUtil;
import com.myarea.myarea.dto.LoginRequestDto;
import com.myarea.myarea.dto.SignupRequestDto;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signupRequestDto) {
        if (userService.checkEmailDuplicate(signupRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
        }

        userService.signup(signupRequestDto);
        return ResponseEntity.ok().body("회원가입 완료");
    }

    // 로그인 시 액세스&리프레시 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
        User user = userService.login(dto);
        if (user == null) return ResponseEntity.status(401).body("Login failed");

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        // refreshToken 저장 → DB
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        ));
    }

    // 리프레시 토큰으로 Access 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(403).body("Invalid refresh token");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        // DB에서 유저 찾아서 실제 권한(UserRole)을 조회
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        String newAccessToken = jwtUtil.generateAccessToken(email, user.getRole());

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }

    // 로그인한 사용자 정보 반환(예: 프로필)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        User user = userService.getLoginUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(user);
    }
}