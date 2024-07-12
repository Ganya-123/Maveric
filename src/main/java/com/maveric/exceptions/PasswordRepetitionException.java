package com.maveric.exceptions;

public class PasswordRepetitionException extends RuntimeException {
  public PasswordRepetitionException() {
    super();
  }

  public PasswordRepetitionException(String message) {
    super(message);
  }

  public PasswordRepetitionException(String message, Throwable cause) {
    super(message, cause);
  }

  public PasswordRepetitionException(Throwable cause) {
    super(cause);
  }

  protected PasswordRepetitionException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
