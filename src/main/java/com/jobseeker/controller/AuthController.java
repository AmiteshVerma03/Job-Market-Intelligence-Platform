package com.jobseeker.controller;

import com.jobseeker.dto.RegisterRequest;
import com.jobseeker.entity.User;
import com.jobseeker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );
    }
}