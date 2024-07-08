package com.maveric.controller;

import com.maveric.dto.ForgotPassword;
import com.maveric.dto.LoginDto;
import com.maveric.dto.RegisterRequestDto;
import com.maveric.dto.RegisterResponse;
import com.maveric.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class LoginController {
    @Autowired
    private RegisterService service;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequestDto request) {
        var registerResponse = service.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@Valid @RequestBody LoginDto loginDto) {
        String loginResponse = service.loginUser(loginDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(loginResponse);

    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPassword forgotPassword) {
        String loginResponse = service.forgotPassword(forgotPassword);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);

    }
}
