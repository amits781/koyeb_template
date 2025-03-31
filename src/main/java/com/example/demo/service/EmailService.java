package com.example.demo.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

  public EmailResponseBody processEmail(EmailRequestBody request) {
    log.info("Receive email body: {}", request);
    EmailResponseBody emailResponse = EmailResponseBody.builder().tnxAmount("2000")
        .tnxCategory("Travel").tnxDate(LocalDateTime.now().toString()).tnxDetails("Uber")
        .tnxDetails("1234-234").tnxSource("xxx1003").build();
    log.info("Finish processing email body, response: {}", emailResponse);
    return emailResponse;
  }
}
