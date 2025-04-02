package com.example.demo.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

  private EmailProcessorICICI processorIcici;

  public EmailService(@Lazy EmailProcessorICICI processorIcici) {
    this.processorIcici = processorIcici;
  }

  public EmailResponseBody processEmail(EmailRequestBody request) {
    EmailResponseBody emailResponse;
    log.info("Receive email body: {}", request);
    if(request.getFrom().contains("icici")) {
      emailResponse = processorIcici.processEmail(request);
    } else {
      emailResponse = EmailResponseBody.builder().tnxAmount(null).tnxCategory(null).tnxDate(null)
      .tnxDetails(null).tnxSource(null).tnxId(null).build();
    }
    log.info("Finish processing email body, response: {}", emailResponse);
    return emailResponse;
  }
}
