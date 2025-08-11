package com.myarea.myarea.controller;

import com.myarea.myarea.dto.FollowDto;
import com.myarea.myarea.dto.FollowUserDto;
import com.myarea.myarea.entity.Follow;
import com.myarea.myarea.jwt.JwtUtil;
import com.myarea.myarea.service.FollowService;
import com.myarea.myarea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @GetMapping
    public List<FollowUserDto> getFollow(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        return followService.getFollow(userId);
    }

    @PostMapping("/{followed_user_id}")
    public ResponseEntity<FollowDto> addFollow(@PathVariable Long followed_user_id,
                                               @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        FollowDto dto = followService.addFollow(userId, followed_user_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{followed_user_id}")
    public ResponseEntity<FollowDto> deleteFollow(@PathVariable Long followed_user_id,
                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        FollowDto dto = followService.deleteFollow(userId, followed_user_id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // JWT 토큰에서 userId 추출
    private Long extractUserId(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 없습니다.");
        }

        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }

        String email = jwtUtil.getEmailFromToken(token);
        return userService.findByEmail(email).getId();
    }
}
