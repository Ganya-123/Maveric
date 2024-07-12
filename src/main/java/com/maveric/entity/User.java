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

  private String status;
  private String session;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Transaction> transactions;

  public User(
      String fullName,
      String mobileNumber,
      String emailId,
      char[] password,
      String status,
      String session) {
    this.fullName = fullName;
    this.mobileNumber = mobileNumber;
    this.emailId = emailId;
    this.password = password;
    this.status = status;
    this.session = session;
  }
}
