package com.maveric.service;

import com.maveric.config.EncryptDecrypt;
import com.maveric.dto.ForgotPassword;
import com.maveric.dto.LoginDto;
import com.maveric.dto.RegisterRequestDto;
import com.maveric.dto.RegisterResponse;
import com.maveric.entity.User;
import com.maveric.exceptions.EmailAlreadyExistsException;
import com.maveric.exceptions.EmailNotFoundException;
import com.maveric.repo.RegisterRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegisterServiceTest {
    @Mock
    private RegisterRepo mockRepo;
    @Mock
    private ModelMapper mockMapper;
    @Mock
    private EncryptDecrypt mockEncryptDecrypt;
    @InjectMocks
    private RegisterService registerService;

    @Test
    void testregisterUser_success() {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setMobileNumber("9876543210");
        requestDto.setFullName("Ganya HT");
        requestDto.setEmail("g@gmail.com");
        requestDto.setPassword("password".toCharArray());

        User user = new User();
        user.setUserId(1L);
        user.setMobileNumber("9876543210");
        user.setFullName("Ganya HT");
        user.setEmail("g@gmail.com");
        user.setPassword("password".toCharArray());
        user.setStatus("ACTIVE");

        RegisterResponse responseDto = new RegisterResponse();
        responseDto.setEmail(user.getEmail());
        responseDto.setFullName(user.getFullName());
        responseDto.setMobileNumber(user.getMobileNumber());

        when(mockMapper.map(requestDto, User.class)).thenReturn(user);
        when(mockRepo.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(mockRepo.save(user)).thenReturn(user);
        when(mockMapper.map(user, RegisterResponse.class)).thenReturn(responseDto);
        when(mockEncryptDecrypt.encode(requestDto.getPassword())).thenReturn("password");

        RegisterResponse result = registerService.registerUser(requestDto);
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFullName(), result.getFullName());
    }

    @Test
    public void testRegisterUserfailure_EmailAlreadyExists() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("password".toCharArray());
        registerRequestDto.setFullName("Test User");
        registerRequestDto.setMobileNumber("1234567890");

        User user = new User();
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(registerRequestDto.getPassword());
        user.setFullName(registerRequestDto.getFullName());
        user.setMobileNumber(registerRequestDto.getMobileNumber());
        user.setStatus("ACTIVE");
        when(mockRepo.findByEmail(registerRequestDto.getEmail())).thenReturn(Optional.of(user));

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            registerService.registerUser(registerRequestDto);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    public void testRegisterUser_MappingFailure() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setPassword("password".toCharArray());
        registerRequestDto.setFullName("Test User");
        registerRequestDto.setMobileNumber("1234567890");

        User user = new User();
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(registerRequestDto.getPassword());
        user.setFullName(registerRequestDto.getFullName());
        user.setMobileNumber(registerRequestDto.getMobileNumber());
        user.setStatus("ACTIVE");
        when(mockRepo.findByEmail(registerRequestDto.getEmail())).thenReturn(Optional.empty());
        when(mockEncryptDecrypt.encode(registerRequestDto.getPassword())).thenReturn("encodedPassword");
        when(mockMapper.map(registerRequestDto, User.class)).thenThrow(new RuntimeException("Mapping error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registerService.registerUser(registerRequestDto);
        });

        assertEquals("Mapping error", exception.getMessage());
    }

    @Test
    public void testLoginUser_success() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password".toCharArray());

        User user = new User();
        user.setEmail(loginDto.getEmail());
        user.setPassword("encodedPassword".toCharArray());

        when(mockRepo.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(mockEncryptDecrypt.decode(new String(user.getPassword()))).thenReturn("password".toCharArray());

        String response = registerService.loginUser(loginDto);

        assertEquals("Login successful", response);
    }

    @Test
    public void testLoginUser_EmailNotFound() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password".toCharArray());

        User user = new User();
        user.setEmail(loginDto.getEmail());
        user.setPassword("encodedPassword".toCharArray());

        when(mockRepo.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());
        when(mockEncryptDecrypt.decode(new String(user.getPassword()))).thenReturn("password".toCharArray());

        assertThrows(EmailNotFoundException.class, () -> {
            registerService.loginUser(loginDto);
        });
    }

    @Test
    public void testLoginUser_InvalidPassword() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password".toCharArray());

        User user = new User();
        user.setEmail(loginDto.getEmail());
        user.setPassword("encodedPassword".toCharArray());

        when(mockRepo.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(mockEncryptDecrypt.decode(new String(user.getPassword()))).thenReturn("wrongPassword".toCharArray());

        String response = registerService.loginUser(loginDto);

        assertEquals("Login unsuccessful", response);
    }

    @Test
    public void testForgotPassword_Success() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("newPassword".toCharArray());
        forgotPassword.setNewpasswordtwo("newPassword".toCharArray());

        User existingUser = new User();
        existingUser.setEmail(forgotPassword.getEmail());
        existingUser.setPassword("encodedOldPassword".toCharArray());
        existingUser.setFullName("Test User");
        existingUser.setMobileNumber("1234567890");
        existingUser.setStatus("ACTIVE");

        when(mockRepo.findByEmail(forgotPassword.getEmail())).thenReturn(Optional.of(existingUser));
        when(mockRepo.getPasswordsByEmail(forgotPassword.getEmail())).thenReturn(Optional.of(Arrays.asList("encodedOldPassword1", "encodedOldPassword2", "encodedOldPassword3")));
        when(mockEncryptDecrypt.encode(forgotPassword.getNewpasswordone())).thenReturn("encodedNewPassword");
        when(mockEncryptDecrypt.decode("encodedOldPassword1")).thenReturn("oldPassword1".toCharArray());
        when(mockEncryptDecrypt.decode("encodedOldPassword2")).thenReturn("oldPassword2".toCharArray());
        when(mockEncryptDecrypt.decode("encodedOldPassword3")).thenReturn("oldPassword3".toCharArray());

        User savedUser = new User();
        savedUser.setEmail(forgotPassword.getEmail());
        savedUser.setPassword("encodedNewPassword".toCharArray());
        savedUser.setFullName("Test User");
        savedUser.setMobileNumber("1234567890");
        savedUser.setStatus("ACTIVE");

        when(mockRepo.save(any(User.class))).thenReturn(savedUser);

        String result = registerService.forgotPassword(forgotPassword);

        assertEquals("Password change successful", result);
        assertEquals("INACTIVE", existingUser.getStatus());
        assertEquals(savedUser.getEmail(), forgotPassword.getEmail());
        assertEquals(new String(savedUser.getPassword()), "encodedNewPassword");
        assertEquals(savedUser.getStatus(), "ACTIVE");
        assertEquals(savedUser.getFullName(), "Test User");
        assertEquals(savedUser.getMobileNumber(), "1234567890");
    }


    @Test
    public void testForgotPassword_EmailNotFound() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("newPassword".toCharArray());
        forgotPassword.setNewpasswordtwo("newPassword".toCharArray());

        when(mockRepo.findByEmail(forgotPassword.getEmail())).thenReturn(Optional.empty());

        assertThrows(EmailNotFoundException.class, () -> {
            registerService.forgotPassword(forgotPassword);
        });
    }


    @Test
    public void testForgotPassword_PasswordsDoNotMatch() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("newPassword1".toCharArray());
        forgotPassword.setNewpasswordtwo("newPassword2".toCharArray());

        User user = new User();
        user.setEmail(forgotPassword.getEmail());
        user.setPassword("oldEncryptedPassword".toCharArray());
        user.setFullName("Test User");
        user.setMobileNumber("1234567890");

        when(mockRepo.findByEmail(forgotPassword.getEmail())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> {
            registerService.forgotPassword(forgotPassword);
        });
    }


    @Test
    public void testForgotPassword_NewPasswordMatchesLastThreeOldPasswords() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("oldPassword3".toCharArray());
        forgotPassword.setNewpasswordtwo("oldPassword3".toCharArray());

        User user = new User();
        user.setEmail(forgotPassword.getEmail());
        user.setPassword("oldEncryptedPassword".toCharArray());
        user.setFullName("Test User");
        user.setMobileNumber("1234567890");

        when(mockRepo.findByEmail(forgotPassword.getEmail())).thenReturn(Optional.of(user));
        when(mockRepo.getPasswordsByEmail(forgotPassword.getEmail())).thenReturn(Optional.of(List.of("encodedOldPassword1", "encodedOldPassword2", "encodedOldPassword3")));
        when(mockEncryptDecrypt.decode("encodedOldPassword1")).thenReturn("oldPassword1".toCharArray());
        when(mockEncryptDecrypt.decode("encodedOldPassword2")).thenReturn("oldPassword2".toCharArray());
        when(mockEncryptDecrypt.decode("encodedOldPassword3")).thenReturn("oldPassword3".toCharArray());
        when(mockEncryptDecrypt.encode(forgotPassword.getNewpasswordone())).thenReturn("encodedNewPassword");

        assertThrows(IllegalArgumentException.class, () -> {
            registerService.forgotPassword(forgotPassword);
        });
    }
}