package com.maveric.exceptions;

public class PasswordEmptyException extends RuntimeException {
  public PasswordEmptyException() {
    super();
  }

  public PasswordEmptyException(String message) {
    super(message);
  }

  public PasswordEmptyException(String message, Throwable cause) {
    super(message, cause);
  }

  public PasswordEmptyException(Throwable cause) {
    super(cause);
  }

  protected PasswordEmptyException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
