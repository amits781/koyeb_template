package com.example.demo.parser;

import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;

public interface BankEmailParser {
  boolean canParse(EmailRequestBody email);

  EmailResponseBody parse(EmailRequestBody email);
}