package com.maveric.service;

import com.maveric.constants.Constants;
import com.maveric.dto.ForgotPassword;
import com.maveric.dto.LoginDto;
import com.maveric.dto.RegisterRequestDto;
import com.maveric.dto.RegisterResponseDto;
import com.maveric.entity.User;
import com.maveric.exceptions.*;
import com.maveric.repo.RegisterRepo;
import com.maveric.utils.EncryptDecrypt;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

  private final RegisterRepo repo;
  private final ModelMapper mapper;
  private final EncryptDecrypt encryptDecrypt;

  public RegisterResponseDto registerUser(RegisterRequestDto request) {
    repo.findByEmailIdAndStatusActive(request.getEmailId())
        .ifPresent(
            user -> {
              throw new EmailAlreadyExistsException("Email already exists");
            });
    var user = mapper.map(request, User.class);
    user.setPasswordStatus(Constants.ACTIVE);
    user.setSession(Constants.INACTIVE);
    user.setPassword(encryptDecrypt.encode(request.getPassword()).toCharArray());
    var userResponse = repo.save(user);
    return mapper.map(userResponse, RegisterResponseDto.class);
  }

  public String loginUser(LoginDto loginDto) {
    var user =
        repo.findByEmailIdAndStatusActive(loginDto.getEmailId())
            .orElseThrow(() -> new EmailNotFoundException(Constants.EMAIL_ID_NOT_FOUND));

    char[] decodedPassword = encryptDecrypt.decode(new String(user.getPassword()));

    if (Arrays.equals(loginDto.getPassword(), decodedPassword)) {
      user.setSession(Constants.ACTIVE);
      repo.save(user);
      return Constants.SUCCESSFUL;
    } else {
      return Constants.UNSUCCESSFUL;
    }
  }

  public String forgotPassword(ForgotPassword forgotPassword) {
    var user =
        repo.findByEmailIdAndStatusActive(forgotPassword.getEmailId())
            .orElseThrow(() -> new EmailNotFoundException(Constants.EMAIL_ID_NOT_FOUND));

    if (!Arrays.equals(forgotPassword.getNewPassword(), forgotPassword.getConfirmPassword())) {
      throw new PasswordsNotMatchingException(Constants.PASSWORDS_NOT_MATCHING);
    }

    List<User> users = repo.getUsersByEmailId(forgotPassword.getEmailId());
    if (users.isEmpty()) {
      throw new PasswordNotFoundException(Constants.PASSWORDS_NOT_FOUND);
    }

    checkAgainstPasswords(forgotPassword.getNewPassword(), users);
    user.setPasswordStatus(Constants.INACTIVE);
    repo.save(user);

    var newUser = new User();
    newUser.setEmailId(user.getEmailId());
    newUser.setPassword(encryptDecrypt.encode(forgotPassword.getNewPassword()).toCharArray());
    newUser.setPasswordStatus(Constants.ACTIVE);
    newUser.setFullName(user.getFullName());
    newUser.setSession(Constants.INACTIVE);
    newUser.setPassword(user.getPassword());
    repo.save(newUser);

    return Constants.PASSWORD_CHANGE_SUCCESSFUL;
  }

  public String logoutUser(String emailId) {
    var user =
        repo.findByEmailIdAndStatusActive(emailId)
            .orElseThrow(() -> new EmailNotFoundException(Constants.EMAIL_ID_NOT_FOUND));

    if (user.getSession().equals(Constants.ACTIVE)) {
      user.setSession(Constants.INACTIVE);
      repo.save(user);
      return Constants.LOGGED_OUT;
    } else {
      return Constants.USER_ALREADY_LOGGED_OUT;
    }
  }

  private void checkAgainstPasswords(char[] newPassword, List<User> users) {
    for (User user : users) {
      char[] encryptedPassword = user.getPassword();
      char[] decodedPassword = encryptDecrypt.decode(new String(encryptedPassword));

      if (Arrays.equals(newPassword, decodedPassword)) {
        throw new PasswordRepetitionException(Constants.PASSWORD_ALREADY_PRESENT);
      }
    }
  }
}
