package com.example.demo.service;

import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;

public interface EmailProcessor {

  public EmailResponseBody processEmail(EmailRequestBody request);

}
