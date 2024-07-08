package com.maveric.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ForgotPassword {
    @NotBlank(message = "Email is required")
    private String email;
  //  @NotBlank(message = "Password is required")
    private char[] newpasswordone;
   // @NotBlank(message = "Password is required")
    private char[] newpasswordtwo;
}
