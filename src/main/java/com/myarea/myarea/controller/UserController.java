package com.myarea.myarea.controller;

import com.myarea.myarea.component.JwtTokenProvider;
import com.myarea.myarea.dto.LoginRequestDto;
import com.myarea.myarea.dto.SignupRequestDto;
import com.myarea.myarea.dto.UserDto;
import com.myarea.myarea.entity.User;
import com.myarea.myarea.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto requestDto) {
        UserDto userDto = userService.signup(requestDto);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) {
        User user = userService.authenticate(requestDto.getEmail(), requestDto.getPassword());
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
        String jwtToken = jwtTokenProvider.createToken(user.getId());
        return ResponseEntity.ok().body(jwtToken);
    }
}