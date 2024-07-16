package com.maveric.controller;

import com.maveric.dto.TransactionDto;
import com.maveric.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController {
  private final TransactionService service;

  @PostMapping("/transaction")
  public ResponseEntity<TransactionDto> addTransaction(@Valid @RequestBody TransactionDto request) {
    var response = service.addTransaction(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
