package com.maveric.entity;

import com.maveric.utils.PasswordConverter;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  private String fullName;
  private String mobileNumber;
  private String emailId;

  @Convert(converter = PasswordConverter.class)
  @Column(nullable = false)
  private char[] password;

  private String passwordStatus;
  private String session;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Transaction> transactions;
}
