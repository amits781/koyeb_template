package com.example.demo.service;


import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.exception.ServiceAPIException;
import com.example.demo.parser.BankEmailParser;


@Service
public class TransactionService {

    private final List<BankEmailParser> parsers;

    public TransactionService(List<BankEmailParser> parsers) {
        this.parsers = parsers;
    }

    public EmailResponseBody processEmail(EmailRequestBody request) {
      // 1. Find the correct parser (or throw if none matches)
      BankEmailParser parser = parsers.stream().filter(p -> p.canParse(request)).findFirst()
          .orElseThrow(() -> new ServiceAPIException("No parser found for this email format",
              HttpStatus.BAD_REQUEST));

      // 2. Parse the email
      EmailResponseBody response = parser.parse(request);

      // 3. VALIDATION: Check if mandatory fields are missing or contain default "Unknown" values
      if (isInvalid(response.getTnxSource()) || isInvalid(response.getTnxAmount())) {
        throw new ServiceAPIException(
            "Parsing incomplete: Failed to extract Source or Amount from email.",
            HttpStatus.UNPROCESSABLE_ENTITY);
      }

      return response;
    }


    /**
     * Helper to check if a field is Null, Empty, or contains "Unknown" (from parser defaults)
     */
    private boolean isInvalid(String value) {
      return value == null || value.trim().isEmpty() || value.toLowerCase().contains("unknown");
    }
}