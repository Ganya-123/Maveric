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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final RegisterRepo repo;
    private final ModelMapper mapper;
    private final EncryptDecrypt encryptDecrypt;

    public RegisterResponse registerUser(RegisterRequestDto request) {
        repo.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException("Email already exists");
                });
//        repo.findByEmail(request.getEmail())
//                .ifPresentOrElse(
//                        user -> {
//                            throw new EmailAlreadyExistsException("Email already exists");
//                        },
//                        () -> {
//                            User user = mapper.map(request, User.class);
//                            user.setStatus("ACTIVE");
//                            user.setPassword(encryptDecrypt.encode(request.getPassword()).toCharArray());
//                            User savedUser = repo.save(user);
//                        }
//                );

        User user = mapper.map(request, User.class);
        user.setStatus("ACTIVE");
        user.setPassword(encryptDecrypt.encode(request.getPassword()).toCharArray());
        User userResponse = repo.save(user);
        return mapper.map(userResponse, RegisterResponse.class);
    }

    public String loginUser(LoginDto loginDto) {
        User response = repo.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("Email Id not found"));

        char[] decodedPassword = encryptDecrypt.decode(new String(response.getPassword()));
        if (new String(loginDto.getPassword()).equals(new String(decodedPassword))) {
            return "Login successful";
        } else {
            return "Login unsuccessful";
        }
    }

    public String forgotPassword(ForgotPassword forgotPassword) {
        User existingUser = repo.findByEmail(forgotPassword.getEmail())
                .orElseThrow(() -> new EmailNotFoundException("Email Id not found"));

        if (!new String(forgotPassword.getNewpasswordone()).equals(new String(forgotPassword.getNewpasswordtwo()))) {
            throw new IllegalArgumentException("Passwords are not matching");
        }

        List<String> encryptedPasswordList = repo.getPasswordsByEmail(forgotPassword.getEmail())
                .orElseThrow(() -> new RuntimeException("Passwords not found"));

        String encryptedNewPassword = encryptDecrypt.encode(forgotPassword.getNewpasswordone());

        for (String encryptedPassword : encryptedPasswordList) {
            char[] decryptedPassword = encryptDecrypt.decode(encryptedPassword);
            if (new String(forgotPassword.getNewpasswordone()).equals(new String(decryptedPassword))) {
                throw new IllegalArgumentException("New password matches with one of the old passwords");
            }
        }

        int passwordListSize = encryptedPasswordList.size();
        int startIndex = Math.max(0, passwordListSize - 3);
        List<String> lastThreePasswords = encryptedPasswordList.subList(startIndex, passwordListSize);

        for (String lastThreeEncryptedPassword : lastThreePasswords) {
            char[] decryptedLastThreePassword = encryptDecrypt.decode(lastThreeEncryptedPassword);
            if (new String(forgotPassword.getNewpasswordone()).equals(new String(decryptedLastThreePassword))) {
                throw new IllegalArgumentException("New password matches with one of the last three old passwords");
            }
        }

        existingUser.setStatus("INACTIVE");
        repo.save(existingUser);

        User newUser = new User();
        newUser.setEmail(existingUser.getEmail());
        newUser.setPassword(encryptDecrypt.encode(forgotPassword.getNewpasswordone()).toCharArray());
        newUser.setStatus("ACTIVE");
        newUser.setFullName(existingUser.getFullName());
        repo.save(newUser);

        return "Password change successful";
    }
}
