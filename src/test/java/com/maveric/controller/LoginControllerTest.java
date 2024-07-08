package com.maveric.controller;

import com.maveric.dto.ForgotPassword;
import com.maveric.dto.LoginDto;
import com.maveric.dto.RegisterRequestDto;
import com.maveric.dto.RegisterResponse;
import com.maveric.entity.User;
import com.maveric.exceptions.EmailAlreadyExistsException;
import com.maveric.exceptions.EmailNotFoundException;
import com.maveric.service.RegisterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginControllerTest {
    @Mock
    private RegisterService service;


    @InjectMocks
    private LoginController controller;

    @Test
    void register() {
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


        when(service.registerUser(requestDto)).thenReturn(responseDto);

        ResponseEntity<RegisterResponse> response = controller.register(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }
    @Test
    void register_EmailAlreadyExists() {
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setMobileNumber("9876543210");
        requestDto.setFullName("Ganya HT");
        requestDto.setEmail("g@gmail.com");
        requestDto.setPassword("password".toCharArray());

        when(service.registerUser(requestDto)).thenThrow(new EmailAlreadyExistsException("Email already exists"));

        assertThrows(EmailAlreadyExistsException.class, () -> {
            controller.register(requestDto);
        });
    }

    @Test
    void userLogin() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("gtg@gmail.com");
        loginDto.setPassword("password".toCharArray());

        when(service.loginUser(loginDto)).thenReturn("Login successful");
        ResponseEntity<String> response = controller.userLogin(loginDto);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Login successful", response.getBody());

    }
    @Test
    void userLogin_EmailNotFound() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("gtg@gmail.com");
        loginDto.setPassword("password".toCharArray());

        when(service.loginUser(loginDto)).thenThrow(new EmailNotFoundException("Email Id not found"));

        assertThrows(EmailNotFoundException.class, () -> {
            controller.userLogin(loginDto);
        });
    }
    @Test
    void userLogin_IncorrectPassword() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("gtg@gmail.com");
        loginDto.setPassword("wrongPassword".toCharArray());

        when(service.loginUser(loginDto)).thenReturn("Login unsuccessful");

        ResponseEntity<String> response = controller.userLogin(loginDto);

        assertEquals("Login unsuccessful", response.getBody());
    }

    @Test
    void forgotPassword() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("newPassword".toCharArray());
        forgotPassword.setNewpasswordtwo("newPassword".toCharArray());

        when(service.forgotPassword(forgotPassword)).thenReturn("Password change successful");
        ResponseEntity<String> response = controller.forgotPassword(forgotPassword);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Password change successful", response.getBody());
    }
    @Test
    void forgotPassword_EmailNotFound() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("newPassword".toCharArray());
        forgotPassword.setNewpasswordtwo("newPassword".toCharArray());

        when(service.forgotPassword(forgotPassword)).thenThrow(new EmailNotFoundException("Email Id not found"));

        assertThrows(EmailNotFoundException.class, () -> {
            controller.forgotPassword(forgotPassword);
        });
    }
    @Test
    void forgotPassword_PasswordsDoNotMatch() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("newPassword1".toCharArray());
        forgotPassword.setNewpasswordtwo("newPassword2".toCharArray());

        when(service.forgotPassword(forgotPassword)).thenThrow(new IllegalArgumentException("Passwords are not matching"));

        assertThrows(IllegalArgumentException.class, () -> {
            controller.forgotPassword(forgotPassword);
        });
    }
    @Test
    void forgotPassword_NewPasswordMatchesOldPasswords() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("oldPassword1".toCharArray());
        forgotPassword.setNewpasswordtwo("oldPassword1".toCharArray());

        when(service.forgotPassword(forgotPassword)).thenThrow(new IllegalArgumentException("New password matches with one of the old passwords"));

        assertThrows(IllegalArgumentException.class, () -> {
            controller.forgotPassword(forgotPassword);
        });
    }
    @Test
    void forgotPassword_NewPasswordMatchesLastThreeOldPasswords() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("test@example.com");
        forgotPassword.setNewpasswordone("oldPassword3".toCharArray());
        forgotPassword.setNewpasswordtwo("oldPassword3".toCharArray());

        when(service.forgotPassword(forgotPassword)).thenThrow(new IllegalArgumentException("New password matches with one of the last three old passwords"));

        assertThrows(IllegalArgumentException.class, () -> {
            controller.forgotPassword(forgotPassword);
        });
    }

}