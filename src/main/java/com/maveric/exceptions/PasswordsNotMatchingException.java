package com.maveric.exceptions;

public class PasswordsNotMatchingException extends RuntimeException {
  public PasswordsNotMatchingException() {
    super();
  }

  public PasswordsNotMatchingException(String message) {
    super(message);
  }

  public PasswordsNotMatchingException(String message, Throwable cause) {
    super(message, cause);
  }

  public PasswordsNotMatchingException(Throwable cause) {
    super(cause);
  }

  protected PasswordsNotMatchingException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
