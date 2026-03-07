package com.jobseeker.controller;

import com.jobseeker.dto.LoginRequest;
import com.jobseeker.dto.RegisterRequest;
import com.jobseeker.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword());

        return ResponseEntity.ok("User Registered");
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword());
    }
}