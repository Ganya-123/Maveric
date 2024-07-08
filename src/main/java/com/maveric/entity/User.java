package com.maveric.entity;

import com.maveric.config.PasswordConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "`user`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    private String fullName;
    private String mobileNumber;
    private String email;

    @Convert(converter = PasswordConverter.class)
    private char[] password;

    private String status;
}
