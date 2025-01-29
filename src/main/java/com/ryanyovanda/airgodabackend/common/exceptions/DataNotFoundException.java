package com.ryanyovanda.airgodabackend.common.exceptions;

public class DataNotFoundException extends RuntimeException {
  public DataNotFoundException(String msg) {
    super(msg);
  }
}
