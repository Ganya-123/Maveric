package com.maveric.globalexp;

import com.maveric.dto.ErrorDto;
import com.maveric.exceptions.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorDto> handleEmailAlreadyExistsException(
      EmailAlreadyExistsException ex) {
    ErrorDto errorDto = new ErrorDto(HttpStatus.CONFLICT, ex.getMessage(), Collections.emptyList());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
  }

  @ExceptionHandler(EmailNotFoundException.class)
  public ResponseEntity<ErrorDto> handleEmailNotFoundException(EmailNotFoundException ex) {
    ErrorDto errorDto =
        new ErrorDto(HttpStatus.NOT_FOUND, ex.getMessage(), Collections.emptyList());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
  }

  @ExceptionHandler(PasswordsNotMatchingException.class)
  public ResponseEntity<ErrorDto> passwordsNotMatching(PasswordsNotMatchingException ex) {
    ErrorDto errorDto = new ErrorDto(HttpStatus.CONFLICT, ex.getMessage(), Collections.emptyList());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
  }

  @ExceptionHandler(PasswordRepetitionException.class)
  public ResponseEntity<ErrorDto> passwordsRepition(PasswordRepetitionException ex) {
    ErrorDto errorDto = new ErrorDto(HttpStatus.CONFLICT, ex.getMessage(), Collections.emptyList());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
  }

  @ExceptionHandler(PasswordNotFoundException.class)
  public ResponseEntity<ErrorDto> passwordsNotFound(PasswordNotFoundException ex) {
    ErrorDto errorDto =
        new ErrorDto(HttpStatus.NOT_FOUND, ex.getMessage(), Collections.emptyList());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
  }

  @ExceptionHandler(UserIdNotFoundException.class)
  public ResponseEntity<ErrorDto> userIdNotFound(UserIdNotFoundException ex) {
    ErrorDto errorDto =
        new ErrorDto(HttpStatus.NOT_FOUND, ex.getMessage(), Collections.emptyList());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
  }

  @ExceptionHandler(PasswordEmptyException.class)
  public ResponseEntity<ErrorDto> handlePasswordEmptyException(PasswordEmptyException ex) {
    ErrorDto errorDto =
        new ErrorDto(HttpStatus.NO_CONTENT, ex.getMessage(), Collections.emptyList());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(errorDto);
  }

  @ExceptionHandler(UserNotLoggedInException.class)
  public ResponseEntity<ErrorDto> userIdNotFound(UserNotLoggedInException ex) {
    ErrorDto errorDto =
        new ErrorDto(HttpStatus.FORBIDDEN, ex.getMessage(), Collections.emptyList());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
    ErrorDto errorDto = new ErrorDto(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
    ErrorDto errorResponse =
        new ErrorDto(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please try again later.",
            null);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
