package com.example.demo.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.dto.ResponseBody;

public class ResponseHandler {

  public static ResponseEntity<Object> generateResponse(Object responseObj, HttpStatus status, String message){
    ResponseBody response = ResponseBody.builder().message(message).responseBody(responseObj).build();
    return new ResponseEntity<Object>(response, status);
  }
}
