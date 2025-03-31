package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceAPIException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private String message;
  private HttpStatus status;

  public ServiceAPIException(String message, HttpStatus status) {
    super();
    this.message = message;
    this.status = status;
  }
}
