package com.maveric.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.dto.ForgotPassword;
import com.maveric.dto.LoginDto;
import com.maveric.dto.RegisterRequestDto;
import com.maveric.dto.RegisterResponse;
import com.maveric.exceptions.EmailAlreadyExistsException;
import com.maveric.exceptions.EmailNotFoundException;
import com.maveric.service.RegisterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterService registerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser() throws Exception {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setFullName("John Doe");
        requestDto.setMobileNumber("1234567890");
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123".toCharArray());

        RegisterResponse responseDto = new RegisterResponse();
        responseDto.setUserId(1L);
        responseDto.setFullName("John Doe");
        responseDto.setMobileNumber("1234567890");
        responseDto.setEmail("test@example.com");

        when(registerService.registerUser(any(RegisterRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.mobileNumber").value("1234567890"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void registerUserWithExistingEmail() throws Exception {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setFullName("John Doe");
        requestDto.setMobileNumber("1234567890");
        requestDto.setEmail("existing@example.com");
        requestDto.setPassword("password123".toCharArray());

        when(registerService.registerUser(any(RegisterRequestDto.class))).thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void loginUser() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password123".toCharArray());

        when(registerService.loginUser(any(LoginDto.class))).thenReturn("Login successful");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$").value("Login successful"));
    }


    @Test
    void loginUserWithInvalidEmail() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("invalid@example.com");
        loginDto.setPassword("password123".toCharArray());

        when(registerService.loginUser(any(LoginDto.class))).thenThrow(new EmailNotFoundException("Email Id not found"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Email Id not found"));
    }

    @Test
    void loginUserWithIncorrectPassword() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("wrongpassword".toCharArray());

        when(registerService.loginUser(any(LoginDto.class))).thenReturn("Login unsuccessful");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(jsonPath("$").value("Login unsuccessful"));
    }

    @Test
    void forgotPassword() throws Exception {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("newpassword123".toCharArray());
        forgotPassword.setNewpasswordtwo("newpassword123".toCharArray());

        when(registerService.forgotPassword(any(ForgotPassword.class))).thenReturn("Password change successful");

        mockMvc.perform(post("/forgotPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotPassword)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("Password change successful"));
    }


    @Test
    void forgotPasswordWithEmailNotFound() throws Exception {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("notfound@example.com");
        forgotPassword.setNewpasswordone("newpassword123".toCharArray());
        forgotPassword.setNewpasswordtwo("newpassword123".toCharArray());

        when(registerService.forgotPassword(any(ForgotPassword.class))).thenThrow(new EmailNotFoundException("Email Id not found"));

        mockMvc.perform(post("/forgotPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotPassword)))
                .andExpect(jsonPath("$.message").value("Email Id not found"));
    }

}