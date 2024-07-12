package com.maveric.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.maveric.utils.CharArrayToStringSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPassword {
  @NotBlank(message = "Email is required")
  private String emailId;

  @NotNull(message = "Password is required")
  @JsonSerialize(using = CharArrayToStringSerializer.class)
  private char[] newPassword;

  @NotNull(message = "Password is required")
  @JsonSerialize(using = CharArrayToStringSerializer.class)
  private char[] confirmPassword;
}
