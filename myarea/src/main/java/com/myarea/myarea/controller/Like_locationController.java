package com.myarea.myarea.controller;

import com.myarea.myarea.dto.Like_locationDto;
import com.myarea.myarea.dto.SublocationDto;
import com.myarea.myarea.jwt.JwtUtil;
import com.myarea.myarea.service.Like_locationService;
import com.myarea.myarea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/like_location")
public class Like_locationController {
    @Autowired
    private Like_locationService like_locationService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @GetMapping
    public List<SublocationDto> getLike_location(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        return like_locationService.getLike_location(userId);
    }

    @PostMapping("/{sublocation_id}")
    public ResponseEntity<Like_locationDto> addLike_location(@PathVariable Long sublocation_id,
                                                             @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        Like_locationDto dto = like_locationService.addLike_location(userId, sublocation_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{sublocation_id}")
    public ResponseEntity<Like_locationDto> deleteLike_location(@PathVariable Long sublocation_id,
                                                                @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = extractUserId(token);
        Like_locationDto dto = like_locationService.deleteLike_location(userId, sublocation_id);
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
