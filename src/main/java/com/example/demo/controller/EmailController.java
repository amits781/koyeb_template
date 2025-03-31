package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.service.EmailService;
import com.example.demo.util.ResponseHandler;

@RestController
public class EmailController {

  private EmailService service;

  public EmailController(EmailService service) {
    this.service = service;
  }

  @PostMapping("/email/process")
  public ResponseEntity<Object> saveEmployee(@RequestBody EmailRequestBody request) {
    return ResponseHandler.generateResponse(service.processEmail(request), HttpStatus.OK,
        "Success");
  }

}
