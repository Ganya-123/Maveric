package com.maveric.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private long userId;
    private String fullName;
    private String mobileNumber;
    private String email;

}
