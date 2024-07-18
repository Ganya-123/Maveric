package com.maveric.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.maveric.constants.Constants;
import com.maveric.dto.ForgotPassword;
import com.maveric.dto.LoginDto;
import com.maveric.dto.RegisterRequestDto;
import com.maveric.dto.RegisterResponseDto;
import com.maveric.entity.User;
import com.maveric.exceptions.EmailAlreadyExistsException;
import com.maveric.exceptions.EmailNotFoundException;
import com.maveric.exceptions.PasswordRepetitionException;
import com.maveric.exceptions.PasswordsNotMatchingException;
import com.maveric.service.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginControllerTest {

  @Mock private RegisterService service;

  @InjectMocks private LoginController controller;

  private RegisterRequestDto requestDto;
  private LoginDto loginDto;
  private ForgotPassword forgotPassword;

  @BeforeEach
  void setUp() {
    requestDto = new RegisterRequestDto();
    requestDto.setMobileNumber("9876543210");
    requestDto.setFullName("Ganya HT");
    requestDto.setEmailId("g@gmail.com");
    requestDto.setPassword("password".toCharArray());

    loginDto = new LoginDto();
    loginDto.setEmailId("gtg@gmail.com");
    loginDto.setPassword("password".toCharArray());

    forgotPassword = new ForgotPassword();
    forgotPassword.setEmailId("test@example.com");
    forgotPassword.setNewPassword("newPassword".toCharArray());
    forgotPassword.setConfirmPassword("newPassword".toCharArray());
  }

  @Test
  void register() {
    var user = new User();
    user.setUserId(1L);
    user.setMobileNumber("9876543210");
    user.setFullName("Ganya HT");
    user.setEmailId("g@gmail.com");
    user.setPassword("password".toCharArray());
    user.setPasswordStatus("ACTIVE");

    var responseDto = new RegisterResponseDto();
    responseDto.setEmailId(user.getEmailId());
    responseDto.setFullName(user.getFullName());
    responseDto.setMobileNumber(user.getMobileNumber());

    when(service.registerUser(requestDto)).thenReturn(responseDto);

    ResponseEntity<RegisterResponseDto> response = controller.register(requestDto);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  void register_EmailAlreadyExists() {
    when(service.registerUser(requestDto))
        .thenThrow(new EmailAlreadyExistsException("Email already exists"));

    assertThrows(
        EmailAlreadyExistsException.class,
        () -> {
          controller.register(requestDto);
        });
  }

  @Test
  void register_EmailMappingError() {
    when(service.registerUser(requestDto)).thenThrow(new RuntimeException("Mapping error"));

    assertThrows(
        RuntimeException.class,
        () -> {
          controller.register(requestDto);
        });
  }

  @Test
  void userLogin() {
    when(service.loginUser(loginDto)).thenReturn("Login successful");

    ResponseEntity<String> response = controller.userLogin(loginDto);
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals(Constants.SUCCESSFUL, response.getBody());
  }

  @Test
  void userLogin_EmailNotFound() {
    when(service.loginUser(loginDto)).thenThrow(new EmailNotFoundException("Email Id not found"));

    assertThrows(
        EmailNotFoundException.class,
        () -> {
          controller.userLogin(loginDto);
        });
  }

  @Test
  void userLogin_IncorrectPassword() {
    loginDto.setPassword("wrongPassword".toCharArray());
    when(service.loginUser(loginDto)).thenReturn(Constants.UNSUCCESSFUL);

    ResponseEntity<String> response = controller.userLogin(loginDto);
    assertEquals(Constants.UNSUCCESSFUL, response.getBody());
  }

  @Test
  void forgotPassword() {
    when(service.forgotPassword(forgotPassword)).thenReturn(Constants.PASSWORD_CHANGE_SUCCESSFUL);

    ResponseEntity<String> response = controller.forgotPassword(forgotPassword);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(Constants.PASSWORD_CHANGE_SUCCESSFUL, response.getBody());
  }

  @Test
  void forgotPassword_EmailNotFound() {
    when(service.forgotPassword(forgotPassword))
        .thenThrow(new EmailNotFoundException("Email Id not found"));

    assertThrows(
        EmailNotFoundException.class,
        () -> {
          controller.forgotPassword(forgotPassword);
        });
  }

  @Test
  void forgotPassword_PasswordsDoNotMatch() {
    forgotPassword.setNewPassword("newPassword1".toCharArray());
    forgotPassword.setConfirmPassword("newPassword2".toCharArray());

    when(service.forgotPassword(forgotPassword))
        .thenThrow(new PasswordsNotMatchingException("Passwords are not matching"));

    assertThrows(
        PasswordsNotMatchingException.class,
        () -> {
          controller.forgotPassword(forgotPassword);
        });
  }

  @Test
  void forgotPassword_NewPasswordMatchesOldPasswords() {
    forgotPassword.setNewPassword("oldPassword1".toCharArray());
    forgotPassword.setConfirmPassword("oldPassword1".toCharArray());

    when(service.forgotPassword(forgotPassword))
        .thenThrow(
            new PasswordRepetitionException("New password matches with one of the old passwords"));

    assertThrows(
        PasswordRepetitionException.class,
        () -> {
          controller.forgotPassword(forgotPassword);
        });
  }

  @Test
  void forgotPassword_NewPasswordMatchesLastThreeOldPasswords() {
    forgotPassword.setNewPassword("oldPassword3".toCharArray());
    forgotPassword.setConfirmPassword("oldPassword3".toCharArray());

    when(service.forgotPassword(forgotPassword))
        .thenThrow(
            new PasswordRepetitionException(
                "New password matches with one of the last three old passwords"));

    assertThrows(
        PasswordRepetitionException.class,
        () -> {
          controller.forgotPassword(forgotPassword);
        });
  }

  @Test
  void logout_user() {

    when(service.logoutUser(loginDto.getEmailId())).thenReturn(Constants.LOGGED_OUT);
    assertEquals(Constants.LOGGED_OUT, controller.userLogout(loginDto.getEmailId()).getBody());
  }

  @Test
  void logout_user_failure1() {

    when(service.logoutUser(loginDto.getEmailId())).thenReturn(Constants.USER_ALREADY_LOGGED_OUT);
    assertEquals(
        Constants.USER_ALREADY_LOGGED_OUT, controller.userLogout(loginDto.getEmailId()).getBody());
  }
}
