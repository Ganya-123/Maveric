package com.maveric.controller;

import com.maveric.dto.ForgotPassword;
import com.maveric.dto.LoginDto;
import com.maveric.dto.RegisterRequestDto;
import com.maveric.dto.RegisterResponseDto;
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
  @Autowired private RegisterService service;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponseDto> register(
      @Valid @RequestBody RegisterRequestDto request) {
    var registerResponse = service.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
  }

  @PostMapping("/login")
  public ResponseEntity<String> userLogin(@Valid @RequestBody LoginDto loginDto) {
    var loginResponse = service.loginUser(loginDto);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(loginResponse);
  }

  @PostMapping("/forgotPassword")
  public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPassword forgotPassword) {
    var loginResponse = service.forgotPassword(forgotPassword);
    return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> userLogout(@Valid @RequestBody LoginDto loginDto) {
    var loginResponse = service.logoutUser(loginDto);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(loginResponse);
  }
}
