package com.example.demo.service;


import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.entity.ProcessedEmail;
import com.example.demo.exception.ServiceAPIException;
import com.example.demo.parser.BankEmailParser;
import com.example.demo.repo.ProcessedEmailRepository;


@Service
public class TransactionService {

  private final List<BankEmailParser> parsers;
  private final ProcessedEmailRepository emailRepository;

  public TransactionService(List<BankEmailParser> parsers,
      ProcessedEmailRepository emailRepository) {
    this.parsers = parsers;
    this.emailRepository = emailRepository;
  }

  public EmailResponseBody processEmail(EmailRequestBody request) {
    if (emailRepository.existsById(request.getMessageId())) {
      throw new ServiceAPIException("Duplicate Transaction: Email with Message-ID "
          + request.getMessageId() + " has already been processed.", HttpStatus.CONFLICT);
    }

    BankEmailParser parser = parsers.stream().filter(p -> p.canParse(request)).findFirst()
        .orElseThrow(() -> new ServiceAPIException("No parser found for this email format",
            HttpStatus.BAD_REQUEST));

    EmailResponseBody response = parser.parse(request);

    if (isInvalid(response.getTnxSource()) || isInvalid(response.getTnxAmount())) {
      throw new ServiceAPIException(
          "Parsing incomplete: Failed to extract Source or Amount from email.",
          HttpStatus.UNPROCESSABLE_ENTITY);
    }

    ProcessedEmail logEntity =
        ProcessedEmail.builder().messageId(request.getMessageId()).from(request.getFrom())
            .tnxAmount(response.getTnxAmount()).tnxCategory(response.getTnxCategory())
            .tnxDate(response.getTnxDate()).tnxDetails(response.getTnxDetails())
            .tnxId(response.getTnxId()).tnxSource(response.getTnxSource()).build();

    emailRepository.save(logEntity);

    return response;
  }


  /**
   * Helper to check if a field is Null, Empty, or contains "Unknown" (from parser defaults)
   */
  private boolean isInvalid(String value) {
    return value == null || value.trim().isEmpty() || value.toLowerCase().contains("unknown");
  }
}
