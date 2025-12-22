package com.example.demo.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.util.DateUtils;

@Component
public class SbiAccountParser implements BankEmailParser {

  // Regex to extract the core sentence:
  // "Your A/C XXXXX266404 has a debit by transfer of Rs 236.00 on 17/10/25"
  private static final Pattern MAIN_PATTERN =
      Pattern.compile("Your A/C\\s+(.*?)\\s+has a\\s+(.*?)\\s+of\\s+(Rs\\s*[\\d,.]+)");

  @Override
  public boolean canParse(EmailRequestBody email) {
    return (email.getFrom().contains("sbi.co.in") || email.getSubject().contains("SBI ALERT"));
  }

  @Override
  public EmailResponseBody parse(EmailRequestBody email) {
    // 1. Clean the body: Remove Hindi (Non-ASCII) and excessive whitespace/newlines
    String englishBody = cleanText(email.getBody());

    String account = "Unknown Account";
    String type = "Transaction"; // e.g., "debit by transfer"
    String amount = "0.00";

    Matcher matcher = MAIN_PATTERN.matcher(englishBody);
    if (matcher.find()) {
      account = matcher.group(1).trim(); // XXXXX266404
      type = matcher.group(2).trim(); // debit by transfer
      amount = matcher.group(3).trim(); // Rs 236.00
    }

    // SBI Alerts rarely contain a unique Transaction ID in the text body.
    // We return a placeholder.
    String tnxId = "tnx_id";

    return EmailResponseBody.builder().tnxSource("SBI Account " + account)
        .tnxAmount(amount.toLowerCase().replace(" ", "")) // rs236.00
        .tnxId(tnxId).tnxDate(DateUtils.getFormattedDate(email.getDate())).tnxDetails(type).build();
  }

  /**
   * Removes Hindi characters, newlines, and carriage returns. Keeps only English letters, numbers,
   * punctuation, and spaces.
   */
  private String cleanText(String input) {
    if (input == null)
      return "";
    // Replace non-ASCII characters with empty string
    String asciiOnly = input.replaceAll("[^\\p{ASCII}]", "");
    // Replace newlines/tabs with a single space to make regex easier
    return asciiOnly.replaceAll("\\s+", " ").trim();
  }

}
