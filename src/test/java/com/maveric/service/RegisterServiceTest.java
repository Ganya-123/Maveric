package com.maveric.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.maveric.repo.RegisterRepo;
import com.maveric.utils.EncryptDecrypt;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegisterServiceTest {
  @Mock private RegisterRepo mockRepo;
  @Mock private ModelMapper mockMapper;
  @Mock private EncryptDecrypt mockEncryptDecrypt;
  @InjectMocks private RegisterService registerService;

  private RegisterRequestDto requestDto;
  private User user;
  private RegisterResponseDto responseDto;

  private LoginDto loginDto;

  private List<User> users;
  private User logoutUser;
  private User user1;
  private User user3;
  private ForgotPassword forgotPassword;

  @BeforeEach
  void setUp() {

    user1 = new User();
    user1.setFullName("ABHI");
    user1.setMobileNumber("1234567890");
    user1.setEmailId("a@example.com");
    user1.setPassword("password".toCharArray());
    user1.setPasswordStatus(Constants.INACTIVE);
    user1.setSession(Constants.INACTIVE);

    User user2 = new User();
    user2.setFullName("BHARAT");
    user2.setMobileNumber("9876543210");
    user2.setEmailId("b@example.com");
    user2.setPassword("securepwd".toCharArray());
    user2.setPasswordStatus(Constants.INACTIVE);
    user2.setSession(Constants.INACTIVE);

    user3 = new User();
    user3.setFullName("CAT");
    user3.setMobileNumber("9876543210");
    user3.setEmailId("c@example.com");
    user3.setPassword("oldPassword3".toCharArray());
    user3.setPasswordStatus(Constants.ACTIVE);
    user3.setSession(Constants.ACTIVE);

    users = new ArrayList<>();
    users.add(user1);
    users.add(user2);
    users.add(user3);

    requestDto = new RegisterRequestDto();
    requestDto.setMobileNumber("9876543210");
    requestDto.setFullName("Ganya HT");
    requestDto.setEmailId("g@gmail.com");
    requestDto.setPassword("password".toCharArray());

    user = new User();
    user.setUserId(1L);
    user.setMobileNumber("9876543210");
    user.setFullName("Ganya HT");
    user.setEmailId("g@gmail.com");
    user.setPassword("password".toCharArray());
    user.setPasswordStatus(Constants.ACTIVE);

    logoutUser = new User();
    logoutUser.setUserId(1L);
    logoutUser.setMobileNumber("9876543210");
    logoutUser.setFullName("Ganya HT");
    logoutUser.setEmailId("g@gmail.com");
    logoutUser.setPassword("password".toCharArray());
    logoutUser.setPasswordStatus(Constants.ACTIVE);
    logoutUser.setSession(Constants.ACTIVE);

    responseDto = new RegisterResponseDto();
    responseDto.setEmailId(user.getEmailId());
    responseDto.setFullName(user.getFullName());
    responseDto.setMobileNumber(user.getMobileNumber());

    loginDto = new LoginDto("ganya@gmail.com", "password".toCharArray());

    forgotPassword = new ForgotPassword();
    forgotPassword.setEmailId("test@example.com");
    forgotPassword.setNewPassword("oldPassword3".toCharArray());
    forgotPassword.setConfirmPassword("oldPassword3".toCharArray());
  }

  @Test
  void testregisterUser_success() {
    when(mockMapper.map(requestDto, User.class)).thenReturn(user);
    when(mockRepo.findByEmailIdAndStatusActive(requestDto.getEmailId()))
        .thenReturn(Optional.empty());
    when(mockRepo.save(user)).thenReturn(user);
    when(mockMapper.map(user, RegisterResponseDto.class)).thenReturn(responseDto);
    when(mockEncryptDecrypt.encode(requestDto.getPassword())).thenReturn("password");

    RegisterResponseDto result = registerService.registerUser(requestDto);
    assertNotNull(result);
    assertEquals(user.getEmailId(), result.getEmailId());
    assertEquals(user.getFullName(), result.getFullName());
  }

  @Test
  void testRegisterUserfailure_EmailAlreadyExists() {

    when(mockRepo.findByEmailIdAndStatusActive(requestDto.getEmailId()))
        .thenReturn(Optional.of(user));

    EmailAlreadyExistsException exception =
        assertThrows(
            EmailAlreadyExistsException.class,
            () -> {
              registerService.registerUser(requestDto);
            });

    assertEquals("Email already exists", exception.getMessage());
  }

  @Test
  void testRegisterUser_MappingFailure() {

    when(mockRepo.findByEmailIdAndStatusActive(requestDto.getEmailId()))
        .thenReturn(Optional.empty());
    when(mockEncryptDecrypt.encode(requestDto.getPassword())).thenReturn("encodedPassword");
    when(mockMapper.map(requestDto, User.class)).thenThrow(new RuntimeException("Mapping error"));

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              registerService.registerUser(requestDto);
            });

    assertEquals("Mapping error", exception.getMessage());
  }

  @Test
  void testLoginUser_Successful() {

    when(mockRepo.findByEmailIdAndStatusActive(loginDto.getEmailId()))
        .thenReturn(Optional.of(user));
    when(mockEncryptDecrypt.decode(new String(user.getPassword())))
        .thenReturn("password".toCharArray());

    String result = registerService.loginUser(loginDto);

    assertEquals(Constants.SUCCESSFUL, result);
  }

  @Test
  void testLoginUser_EmailNotFound() {

    when(mockRepo.findByEmailIdAndStatusActive(loginDto.getEmailId())).thenReturn(Optional.empty());
    when(mockEncryptDecrypt.decode(new String(user.getPassword())))
        .thenReturn("password".toCharArray());

    assertThrows(
        EmailNotFoundException.class,
        () -> {
          registerService.loginUser(loginDto);
        });
  }

  @Test
  void testLoginUser_InvalidPassword() {

    when(mockRepo.findByEmailIdAndStatusActive(loginDto.getEmailId()))
        .thenReturn(Optional.of(user));
    when(mockEncryptDecrypt.decode(new String(user.getPassword())))
        .thenReturn("wrongPassword".toCharArray());

    String response = registerService.loginUser(loginDto);

    assertEquals(Constants.UNSUCCESSFUL, response);
  }

  @Test
  void testForgotPassword_Success() {
    ForgotPassword forgotPassword = new ForgotPassword();
    forgotPassword.setEmailId("test@example.com");
    forgotPassword.setNewPassword("newPassword".toCharArray());
    forgotPassword.setConfirmPassword("newPassword".toCharArray());

    User existingUser = new User();
    existingUser.setEmailId(forgotPassword.getEmailId());
    existingUser.setPassword("encodedOldPassword".toCharArray());
    existingUser.setFullName("Test User");
    existingUser.setMobileNumber("1234567890");
    existingUser.setPasswordStatus("ACTIVE");

    when(mockRepo.findByEmailIdAndStatusActive(forgotPassword.getEmailId()))
        .thenReturn(Optional.of(existingUser));
    when(mockRepo.getUsersByEmailId(forgotPassword.getEmailId())).thenReturn(users);
    when(mockEncryptDecrypt.encode(forgotPassword.getNewPassword()))
        .thenReturn("encodedNewPassword");
    when(mockEncryptDecrypt.decode("encodedOldPassword1")).thenReturn("oldPassword1".toCharArray());
    when(mockEncryptDecrypt.decode("encodedOldPassword2")).thenReturn("oldPassword2".toCharArray());
    when(mockEncryptDecrypt.decode("encodedOldPassword3")).thenReturn("oldPassword3".toCharArray());

    User savedUser = new User();
    savedUser.setEmailId(forgotPassword.getEmailId());
    savedUser.setPassword("encodedNewPassword".toCharArray());
    savedUser.setFullName("Test User");
    savedUser.setMobileNumber("1234567890");
    savedUser.setPasswordStatus("ACTIVE");

    when(mockRepo.save(any(User.class))).thenReturn(savedUser);

    String result = registerService.forgotPassword(forgotPassword);

    assertEquals("Password change successful", result);
    assertEquals(Constants.INACTIVE, existingUser.getPasswordStatus());
    assertEquals(savedUser.getEmailId(), forgotPassword.getEmailId());
    assertEquals("encodedNewPassword", new String(savedUser.getPassword()));
    assertEquals(Constants.ACTIVE, savedUser.getPasswordStatus());
    assertEquals("Test User", savedUser.getFullName());
    assertEquals("1234567890", savedUser.getMobileNumber());
  }

  @Test
  void testForgotPassword_EmailNotFound() {

    when(mockRepo.findByEmailIdAndStatusActive(forgotPassword.getEmailId()))
        .thenReturn(Optional.empty());

    assertThrows(
        EmailNotFoundException.class,
        () -> {
          registerService.forgotPassword(forgotPassword);
        });
  }

  @Test
  void testForgotPassword_PasswordsDoNotMatch() {
    ForgotPassword forgotPassword = new ForgotPassword();
    forgotPassword.setEmailId("test@example.com");
    forgotPassword.setNewPassword("newPassword1".toCharArray());
    forgotPassword.setConfirmPassword("newPassword2".toCharArray());

    when(mockRepo.findByEmailIdAndStatusActive(forgotPassword.getEmailId()))
        .thenReturn(Optional.of(user));

    assertThrows(
        PasswordsNotMatchingException.class,
        () -> {
          registerService.forgotPassword(forgotPassword);
        });
  }

  @Test
  void testForgotPassword_NewPasswordMatchesOldPasswords() {

    when(mockRepo.findByEmailIdAndStatusActive(forgotPassword.getEmailId()))
        .thenReturn(Optional.of(user));
    when(mockRepo.getUsersByEmailId(forgotPassword.getEmailId())).thenReturn(users);

    when(mockEncryptDecrypt.decode(anyString())).thenReturn("oldPassword1".toCharArray());
    when(mockEncryptDecrypt.decode(anyString())).thenReturn("oldPassword2".toCharArray());
    when(mockEncryptDecrypt.decode(anyString())).thenReturn("oldPassword3".toCharArray());
    when(mockEncryptDecrypt.encode(forgotPassword.getNewPassword()))
        .thenReturn("encodedNewPassword");

    assertThrows(
        PasswordRepetitionException.class,
        () -> {
          registerService.forgotPassword(forgotPassword);
        });
  }

  @Test
  void logout_success() {
    when(mockEncryptDecrypt.decode(anyString())).thenReturn(logoutUser.getPassword());
    when(mockRepo.findByEmailIdAndStatusActive(loginDto.getEmailId()))
        .thenReturn(Optional.of(logoutUser));
    when(mockRepo.save(user1)).thenReturn(user1);
    assertEquals(Constants.LOGGED_OUT, registerService.logoutUser(loginDto.getEmailId()));
  }

  @Test
  void logout_EmailNotFoundException() {
    when(mockRepo.findByEmailIdAndStatusActive(loginDto.getEmailId())).thenReturn(Optional.empty());
    assertThrows(
        EmailNotFoundException.class,
        () -> {
          registerService.logoutUser(loginDto.getEmailId());
        });
  }

  @Test
  void already_user_logged_out() {

    when(mockEncryptDecrypt.decode(anyString())).thenReturn(user1.getPassword());

    when(mockRepo.findByEmailIdAndStatusActive(loginDto.getEmailId()))
        .thenReturn(Optional.of(user1));
    when(mockRepo.save(user1)).thenReturn(user1);
    assertEquals(
        Constants.USER_ALREADY_LOGGED_OUT, registerService.logoutUser(loginDto.getEmailId()));
  }
}
