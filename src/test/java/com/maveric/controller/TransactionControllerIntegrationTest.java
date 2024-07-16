package com.maveric.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.dto.TransactionDto;
import com.maveric.exceptions.UserIdNotFoundException;
import com.maveric.exceptions.UserNotLoggedInException;
import com.maveric.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
public class TransactionControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private TransactionService transactionService;

  @Autowired private ObjectMapper objectMapper;

  private TransactionDto dto;

  @BeforeEach
  void setUp() {
    dto = new TransactionDto(500L, 1L);
  }

  @Test
  void add_transaction_success() throws Exception {

    when(transactionService.addTransaction(dto)).thenReturn(dto);

    mockMvc
        .perform(
            post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated());
  }

  @Test
  void add_transaction_User_not_found() throws Exception {

    when(transactionService.addTransaction(dto))
        .thenThrow(new UserIdNotFoundException("Id not found"));

    mockMvc
        .perform(
            post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(jsonPath("$.message").value("Id not found"));
  }

  @Test
  void add_transaction_User_not_logged_in() throws Exception {

    when(transactionService.addTransaction(dto))
        .thenThrow(new UserNotLoggedInException("User Not Logged In"));

    mockMvc
        .perform(
            post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(jsonPath("$.message").value("User Not Logged In"));
  }
}
