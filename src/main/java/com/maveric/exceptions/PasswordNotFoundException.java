package com.maveric.exceptions;

public class PasswordNotFoundException extends RuntimeException {
  public PasswordNotFoundException() {
    super();
  }

  public PasswordNotFoundException(String message) {
    super(message);
  }

  public PasswordNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public PasswordNotFoundException(Throwable cause) {
    super(cause);
  }

  protected PasswordNotFoundException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
