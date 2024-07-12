package com.maveric.service;

import com.maveric.constants.Constants;
import com.maveric.dto.TransactionDto;
import com.maveric.entity.Transaction;
import com.maveric.entity.User;
import com.maveric.exceptions.UserIdNotFoundException;
import com.maveric.exceptions.UserNotLoggedInException;
import com.maveric.repo.RegisterRepo;
import com.maveric.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final RegisterRepo registerRepo;

  private final TransactionRepo transactionRepo;

  private final ModelMapper mapper;

  @Transactional
  public TransactionDto addTransaction(TransactionDto request) {
    User user =
        registerRepo
            .findById(request.getUserId())
            .orElseThrow(() -> new UserIdNotFoundException("Id not found"));
    if (user.getSession().equals(Constants.ACTIVE)) {
      Transaction transaction = new Transaction();
      transaction.setAmount(request.getAmount());
      transaction.setUser(user);
      transactionRepo.save(transaction);

      return request;
    } else {
      throw new UserNotLoggedInException("User Not Logged In");
    }
  }
}
