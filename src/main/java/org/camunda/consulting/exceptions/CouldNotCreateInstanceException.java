package org.camunda.consulting.exceptions;

public class CouldNotCreateInstanceException extends RuntimeException {
  public CouldNotCreateInstanceException(String errorMessage) {
    super(errorMessage);
  }
}
