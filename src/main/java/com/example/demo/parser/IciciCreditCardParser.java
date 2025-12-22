package com.example.demo.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.util.DateUtils;


@Component
public class IciciCreditCardParser implements BankEmailParser {

  // Regex to find: "transaction of INR 4.00"
  private static final Pattern AMOUNT_PATTERN = Pattern.compile("transaction of (INR\\s+[\\d,.]+)");

  // Regex to find: "Info: AMAZON PAY IN E COMMERCE."
  private static final Pattern INFO_PATTERN = Pattern.compile("Info:\\s*(.*?)\\.");


  private static final Pattern SOURCE_PATTERN =
      Pattern.compile("Your\\s+(.*?)\\s+has\\s+been\\s+used");

  @Override
  public boolean canParse(EmailRequestBody email) {
    return email.getFrom().contains("icicibank.com")
        && email.getSubject().contains("Transaction alert");
  }

  @Override
  public EmailResponseBody parse(EmailRequestBody email) {
    String body = email.getBody();

    String amount = extract(AMOUNT_PATTERN, body, "Unknown");
    String details = extract(INFO_PATTERN, body, "Unknown");
    String source = extract(SOURCE_PATTERN, body, "Unknown");

    return EmailResponseBody.builder().tnxSource(source).tnxAmount(amount.toLowerCase())
        .tnxDate(DateUtils.getFormattedDate(email.getDate())).tnxDetails(details).build();
  }

  private String extract(Pattern pattern, String text, String defaultValue) {
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.group(1).trim();
    }
    return defaultValue;
  }

}
