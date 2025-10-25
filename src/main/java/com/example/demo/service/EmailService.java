package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

  private EmailProcessorGeneric processorGeneric;

  public EmailService(EmailProcessorGeneric processorGeneric) {
    this.processorGeneric = processorGeneric;
  }

  public EmailResponseBody processEmailV2(EmailRequestBody request) {
    log.info("Receive email body: {}", request.getSubject());
    EmailResponseBody emailResponse;
    emailResponse = processorGeneric.processEmail(request);
    log.info("Finish processing email body, response: {}", emailResponse.getTnxId());
    return emailResponse;
  }
}
