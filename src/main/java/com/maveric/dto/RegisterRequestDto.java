package com.maveric.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.maveric.utils.CharArrayToStringSerializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

  @NotBlank(message = "Full name is required")
  private String fullName;

  @NotBlank(message = "Mobile number is required")
  @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
  private String mobileNumber;

  @NotBlank(message = "Email is required")
  @Email(
      regexp =
          "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
      message = "Email format is invalid")
  private String emailId;

  @JsonSerialize(using = CharArrayToStringSerializer.class)
  @NotNull(message = "Password is required")
  private char[] password;
}
