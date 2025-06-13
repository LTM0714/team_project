package com.myarea.myarea.controller;

import com.myarea.myarea.dto.LoginRequestDto;
import com.myarea.myarea.dto.SignupRequestDto;
import com.myarea.myarea.dto.UserDto;
import com.myarea.myarea.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto requestDto) {
        UserDto userDto = userService.signup(requestDto);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto requestDto) {
        UserDto userDto = userService.login(requestDto);
        return ResponseEntity.ok(userDto);
    }
}