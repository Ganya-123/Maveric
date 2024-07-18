package com.maveric.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.constants.Constants;
import com.maveric.dto.ForgotPassword;
import com.maveric.dto.LoginDto;
import com.maveric.dto.RegisterRequestDto;
import com.maveric.dto.RegisterResponseDto;
import com.maveric.exceptions.EmailAlreadyExistsException;
import com.maveric.exceptions.EmailNotFoundException;
import com.maveric.exceptions.PasswordRepetitionException;
import com.maveric.exceptions.PasswordsNotMatchingException;
import com.maveric.service.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LoginController.class)
class LoginControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private RegisterService registerService;

  @Autowired private ObjectMapper objectMapper;
  private RegisterRequestDto registerRequestDto;
  private LoginDto loginDto;
  private ForgotPassword forgotPassword;

  @BeforeEach
  void setUp() {
    registerRequestDto = new RegisterRequestDto();
    registerRequestDto.setFullName("John Doe");
    registerRequestDto.setMobileNumber("1234567890");
    registerRequestDto.setEmailId("test@example.com");
    registerRequestDto.setPassword("password123".toCharArray());

    loginDto = new LoginDto();
    loginDto.setEmailId("test@example.com");
    loginDto.setPassword("password123".toCharArray());

    forgotPassword = new ForgotPassword();
    forgotPassword.setEmailId("test@example.com");
    forgotPassword.setNewPassword("newpassword123".toCharArray());
    forgotPassword.setConfirmPassword("newpassword123".toCharArray());
  }

  @Test
  void registerUser() throws Exception {
    var responseDto = new RegisterResponseDto();
    responseDto.setUserId(1L);
    responseDto.setFullName("John Doe");
    responseDto.setMobileNumber("1234567890");
    responseDto.setEmailId("test@example.com");

    when(registerService.registerUser(any(RegisterRequestDto.class))).thenReturn(responseDto);

    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(1L))
        .andExpect(jsonPath("$.fullName").value("John Doe"))
        .andExpect(jsonPath("$.mobileNumber").value("1234567890"))
        .andExpect(jsonPath("$.emailId").value("test@example.com"));
  }

  @Test
  void registerUser_validation_failure() throws Exception {
    var emptyRequest = new RegisterRequestDto();
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"));
  }

  @Test
  void registerUserWithExistingEmail() throws Exception {

    when(registerService.registerUser(any(RegisterRequestDto.class)))
        .thenThrow(new EmailAlreadyExistsException("Email already exists"));

    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDto)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Email already exists"));
  }

  @Test
  void registerUser_MappingError() throws Exception {

    when(registerService.registerUser(registerRequestDto))
        .thenThrow(new RuntimeException("Mapping error"));
    mockMvc
        .perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDto)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void loginUser() throws Exception {

    when(registerService.loginUser(any(LoginDto.class))).thenReturn("Login successful");

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$").value("Login successful"));
  }

  @Test
  void loginUserWithInvalidEmail() throws Exception {

    when(registerService.loginUser(any(LoginDto.class)))
        .thenThrow(new EmailNotFoundException("Email Id not found"));

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Email Id not found"));
  }

  @Test
  void loginUserWithIncorrectPassword() throws Exception {

    when(registerService.loginUser(any(LoginDto.class))).thenReturn("Login unsuccessful");

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
        .andExpect(jsonPath("$").value("Login unsuccessful"));
  }

  @Test
  void forgotPassword() throws Exception {

    when(registerService.forgotPassword(any(ForgotPassword.class)))
        .thenReturn("Password change successful");

    mockMvc
        .perform(
            post("/forgotPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPassword)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").value("Password change successful"));
  }

  @Test
  void forgotPasswordWithEmailNotFound() throws Exception {
    ;

    when(registerService.forgotPassword(any(ForgotPassword.class)))
        .thenThrow(new EmailNotFoundException("Email Id not found"));

    mockMvc
        .perform(
            post("/forgotPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPassword)))
        .andExpect(jsonPath("$.message").value("Email Id not found"));
  }

  @Test
  void forgotPasswordWithPasswordRepitition() throws Exception {

    when(registerService.forgotPassword(any(ForgotPassword.class)))
        .thenThrow(
            new PasswordRepetitionException("New password matches with one of the old passwords"));

    mockMvc
        .perform(
            post("/forgotPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPassword)))
        .andExpect(
            jsonPath("$.message").value("New password matches with one of the old passwords"));
  }

  @Test
  void forgotPasswordWithPasswordRepitition2() throws Exception {

    when(registerService.forgotPassword(any(ForgotPassword.class)))
        .thenThrow(
            new PasswordRepetitionException(
                "New password matches with one of the last three old passwords"));

    mockMvc
        .perform(
            post("/forgotPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPassword)))
        .andExpect(
            jsonPath("$.message")
                .value("New password matches with one of the last three old passwords"));
  }

  @Test
  void forgotPassword_Not_Matching() throws Exception {

    when(registerService.forgotPassword(any(ForgotPassword.class)))
        .thenThrow(new PasswordsNotMatchingException(Constants.PASSWORDS_NOT_MATCHING));

    mockMvc
        .perform(
            post("/forgotPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPassword)))
        .andExpect(jsonPath("$.message").value(Constants.PASSWORDS_NOT_MATCHING));
  }

  @Test
  void logoutUser() throws Exception {

    when(registerService.logoutUser(loginDto.getEmailId())).thenReturn(Constants.LOGGED_OUT);

    mockMvc
        .perform(
            post("/logout/{emailId}", loginDto.getEmailId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(Constants.LOGGED_OUT))
        .andExpect(status().isOk());
  }

  @Test
  void logoutUserFailure1() throws Exception {

    when(registerService.logoutUser(loginDto.getEmailId()))
        .thenReturn(Constants.USER_ALREADY_LOGGED_OUT);

    mockMvc
        .perform(
            post("/logout/{emailId}", loginDto.getEmailId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(Constants.USER_ALREADY_LOGGED_OUT));
  }
}
