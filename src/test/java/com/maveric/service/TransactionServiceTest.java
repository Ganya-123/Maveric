package com.maveric.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.maveric.constants.Constants;
import com.maveric.dto.TransactionDto;
import com.maveric.entity.Transaction;
import com.maveric.entity.User;
import com.maveric.exceptions.UserIdNotFoundException;
import com.maveric.exceptions.UserNotLoggedInException;
import com.maveric.repo.RegisterRepo;
import com.maveric.repo.TransactionRepo;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceTest {
  @Mock private RegisterRepo registerRepo;
  @Mock private TransactionRepo transactionRepo;
  @Mock private ModelMapper mapper;
  @InjectMocks private TransactionService service;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUserId(1L);
    user.setMobileNumber("9876543210");
    user.setFullName("Ganya HT");
    user.setEmailId("g@gmail.com");
    user.setPassword("password".toCharArray());
    user.setPasswordStatus(Constants.ACTIVE);
    user.setSession(Constants.ACTIVE);
  }

  @Test
  void addTransaction_success() {
    TransactionDto transactionDto = new TransactionDto(500L, 1L);

    Transaction transaction = new Transaction(1L, 500L, user);

    when(registerRepo.findById(transactionDto.getUserId())).thenReturn(Optional.of(user));
    when(transactionRepo.save(transaction)).thenReturn(transaction);

    var result = service.addTransaction(transactionDto);
    assertNotNull(result);
    assertEquals(transactionDto.getAmount(), result.getAmount());
    assertEquals(transactionDto.getUserId(), result.getUserId());
  }

  @Test
  void addTransaction_userIdNotFoundException() {
    TransactionDto transactionDto = new TransactionDto(500L, 1L);

    Transaction transaction = new Transaction(1L, 500L, user);

    when(registerRepo.findById(transactionDto.getUserId())).thenReturn(Optional.empty());
    when(transactionRepo.save(transaction)).thenReturn(transaction);

    UserIdNotFoundException exception =
        assertThrows(
            UserIdNotFoundException.class,
            () -> {
              service.addTransaction(transactionDto);
            });

    assertEquals("Id not found", exception.getMessage());
  }

  @Test
  void addTransaction_UserNotLoggedInException() {
    TransactionDto transactionDto = new TransactionDto(500L, 1L);

    User logoutUser = new User();
    logoutUser.setUserId(1L);
    logoutUser.setMobileNumber("9876543210");
    logoutUser.setFullName("Ganya HT");
    logoutUser.setEmailId("g@gmail.com");
    logoutUser.setPassword("password".toCharArray());
    logoutUser.setPasswordStatus(Constants.ACTIVE);
    logoutUser.setSession(Constants.INACTIVE);

    Transaction transaction = new Transaction(1L, 500L, user);

    when(registerRepo.findById(transactionDto.getUserId())).thenReturn(Optional.of(logoutUser));
    when(transactionRepo.save(transaction)).thenReturn(transaction);

    UserNotLoggedInException exception =
        assertThrows(
            UserNotLoggedInException.class,
            () -> {
              service.addTransaction(transactionDto);
            });

    assertEquals("User Not Logged In", exception.getMessage());
  }
}
