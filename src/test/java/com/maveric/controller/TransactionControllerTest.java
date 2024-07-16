package com.maveric.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.maveric.dto.TransactionDto;
import com.maveric.exceptions.UserIdNotFoundException;
import com.maveric.exceptions.UserNotLoggedInException;
import com.maveric.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionControllerTest {

  @Mock private TransactionService service;

  @InjectMocks private TransactionController controller;

  private TransactionDto dto;

  @BeforeEach
  void setUp() {
    dto = new TransactionDto(500L, 1L);
  }

  @Test
  void addTransaction_success() {
    when(service.addTransaction(dto)).thenReturn(dto);
    ResponseEntity<TransactionDto> response = controller.addTransaction(dto);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  void add_transaction_user_not_found() {
    when(service.addTransaction(dto)).thenThrow(new UserIdNotFoundException("Id not found"));

    assertThrows(
        UserIdNotFoundException.class,
        () -> {
          controller.addTransaction(dto);
        });
  }

  @Test
  void add_transaction_User_not_logged_in() {
    when(service.addTransaction(dto)).thenThrow(new UserNotLoggedInException("User Not Logged In"));

    assertThrows(
        UserNotLoggedInException.class,
        () -> {
          controller.addTransaction(dto);
        });
  }
}
