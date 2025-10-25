package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.exception.ServiceAPIException;
import com.example.demo.service.EmailService;
import com.example.demo.util.ResponseHandler;
import com.example.demo.util.ValidateHeader;

@RestController
public class EmailController {

  private EmailService service;
  private ValidateHeader headerValidator;

  public EmailController(EmailService service, ValidateHeader headerValidator) {
    this.service = service;
    this.headerValidator = headerValidator;
  }

  @PostMapping("/v2/email/process")
  public ResponseEntity<Object> parseEmailV2(@RequestHeader(name = "secret-key") String secretKey,
                                             @RequestBody EmailRequestBody request) {
    if (!headerValidator.validateSecret(secretKey)) {
      throw new ServiceAPIException("Invalid secret key", HttpStatus.UNAUTHORIZED);
    }
    return ResponseHandler.generateResponse(service.processEmailV2(request), HttpStatus.OK,
            "Success");
  }

}
