package com.maveric.exceptions;

public class EmailNotFoundException extends RuntimeException {
  public EmailNotFoundException() {
    super();
  }

  public EmailNotFoundException(String message) {
    super(message);
  }

  public EmailNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public EmailNotFoundException(Throwable cause) {
    super(cause);
  }

  protected EmailNotFoundException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
