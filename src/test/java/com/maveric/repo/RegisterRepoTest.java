package com.maveric.repo;

import static org.junit.jupiter.api.Assertions.*;

import com.maveric.constants.Constants;
import com.maveric.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class RegisterRepoTest {

  @Autowired private RegisterRepo registerRepo;

  private User activeUser;
  private User inactiveUser;

  @BeforeEach
  void setUp() {
    activeUser = new User();
    activeUser.setFullName("Active User");
    activeUser.setMobileNumber("1234567890");
    activeUser.setEmailId("activeuser@example.com");
    activeUser.setPassword("password".toCharArray());
    activeUser.setPasswordStatus(Constants.ACTIVE);
    activeUser.setSession(Constants.ACTIVE);

    inactiveUser = new User();
    inactiveUser.setFullName("Inactive User");
    inactiveUser.setMobileNumber("0987654321");
    inactiveUser.setEmailId("inactiveuser@example.com");
    inactiveUser.setPassword("password".toCharArray());
    inactiveUser.setPasswordStatus(Constants.INACTIVE);
    inactiveUser.setSession(Constants.INACTIVE);

    registerRepo.save(activeUser);
    registerRepo.save(inactiveUser);
  }

  @Test
  void testFindByEmailIdAndStatusActive_Success() {
    Optional<User> foundUser = registerRepo.findByEmailIdAndStatusActive("activeuser@example.com");
    assertEquals(true, foundUser.isPresent());
    assertEquals("activeuser@example.com", foundUser.get().getEmailId());
    assertEquals(Constants.ACTIVE, foundUser.get().getPasswordStatus());
  }

  @Test
  void testFindByEmailIdAndStatusActive_Failure() {
    Optional<User> foundUser =
        registerRepo.findByEmailIdAndStatusActive("inactiveuser@example.com");
    assertEquals(false, foundUser.isPresent());
  }

  @Test
  void testGetUsersByEmailId_Success() {
    List<User> foundUsers = registerRepo.getUsersByEmailId("activeuser@example.com");
    assertEquals(1, foundUsers.size());
    assertEquals("activeuser@example.com", foundUsers.get(0).getEmailId());
  }

  @Test
  void testGetUsersByEmailId_Failure() {
    List<User> foundUsers = registerRepo.getUsersByEmailId("nonexistentuser@example.com");
    assertEquals(0, foundUsers.size());
  }
}
