package com.ryanyovanda.airgodabackend.common.exceptions;

public class DuplicateEmailException extends RuntimeException {
  public DuplicateEmailException(String msg) {
    super(msg);
  }
}
