package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.service.TransactionService;
import com.example.demo.util.ResponseHandler;
import com.example.demo.util.ValidateHeader;

@RestController
public class EmailController {

  private ValidateHeader headerValidator;
  private TransactionService transactionService;

  public EmailController(ValidateHeader headerValidator,
      TransactionService transactionService) {
    this.headerValidator = headerValidator;
    this.transactionService = transactionService;
  }


  @PostMapping("/email/process")
  public ResponseEntity<Object> parseEmailV3(@RequestHeader(name = "secret-key") String secretKey,
      @RequestBody EmailRequestBody request) {
    // if (!headerValidator.validateSecret(secretKey)) {
    // throw new ServiceAPIException("Invalid secret key", HttpStatus.UNAUTHORIZED);
    // }
    return ResponseHandler.generateResponse(transactionService.processEmail(request), HttpStatus.OK,
        "Success");
  }

}
