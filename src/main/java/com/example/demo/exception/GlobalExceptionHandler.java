package com.example.demo.exception;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.demo.util.ResponseHandler;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // Handle ServiceAPIException exceptions
  @ExceptionHandler(ServiceAPIException.class)
  public ResponseEntity<Object> handleResourceNotFound(ServiceAPIException ex) {
    log.error("Error: ServiceAPIException occured, {}", ex.getMessage());
    return ResponseHandler.generateResponse(null, ex.getStatus(), ex.getMessage());
    }
}
