package com.example.demo.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.util.DateUtils;


@Component
public class AmazonPayParser implements BankEmailParser {

  /**
   * MASTER REGEX: Captures Beneficiary and Amount in one line. Looks for: "Paid [to/on] Amount" ->
   * (Capture Beneficiary) -> (Capture Amount) * Examples it matches: 1. Paid to Amount Jio Prepaid
   * ₹ 2545 2. Paid on Amount Amazon.in ₹2720.00 3. Paid to Amount irctc ₹2229.05
   */
  private static final Pattern MASTER_PATTERN = Pattern.compile(
      "Paid\\s+(?:to|on)\\s+Amount\\s+(.*?)\\s+([₹Rs.]+\\s*[\\d,.]+)", Pattern.CASE_INSENSITIVE);

  // Matches both "Order Id" and "Transaction ID"
  private static final Pattern ID_PATTERN = Pattern
      .compile("(?:Order|Transaction)\\s+I[Dd]\\s+([A-Za-z0-9-]+)", Pattern.CASE_INSENSITIVE);

  // Detect payment source based on keywords in body
  private static final Pattern VOUCHER_PATTERN =
      Pattern.compile("Amazon\\s+Vouchers", Pattern.CASE_INSENSITIVE);
  private static final Pattern BALANCE_PATTERN =
      Pattern.compile("Amazon\\s+Pay\\s+Balance", Pattern.CASE_INSENSITIVE);


  @Override
  public boolean canParse(EmailRequestBody email) {
    return email.getFrom().contains("amazonpay.in");
  }

  @Override
  public EmailResponseBody parse(EmailRequestBody email) {
    String body = email.getBody();
    String subject = email.getSubject();

    // 1. Extract Details and Amount using Master Regex
    String amount = "Unknown";
    String details = "Amazon Payment";

    Matcher masterMatcher = MASTER_PATTERN.matcher(body);
    if (masterMatcher.find()) {
      details = masterMatcher.group(1).trim(); // e.g., "Jio Prepaid", "Amazon.in"
      amount = masterMatcher.group(2).trim(); // e.g., "₹ 2545"
    } else {
      // Fallback: If body regex fails, try extracting amount from Subject
      // Subject: "Rs 2720.0 was paid..." or "Your Mobile recharge for Rs. 2545..."
      amount = extractFallbackAmount(subject);
    }

    // 2. Extract Transaction/Order ID
    String tnxId = extract(ID_PATTERN, body, "tnx_id");

    // 3. Determine Source (Balance vs Voucher)
    String source = "Amazon Pay"; // Default
    if (VOUCHER_PATTERN.matcher(body).find()) {
      source = "Amazon Vouchers";
    } else if (BALANCE_PATTERN.matcher(body).find()) {
      source = "Amazon Pay Balance";
    }

    return EmailResponseBody.builder().tnxSource(source).tnxAmount(cleanAmount(amount)) // standardizes
                                                                                        // spaces
        .tnxId(tnxId).tnxDate(DateUtils.getFormattedDate(email.getDate())).tnxDetails(details)
        .build();
  }

  // Helper to extract regex groups
  private String extract(Pattern pattern, String text, String defaultValue) {
    Matcher matcher = pattern.matcher(text);
    return matcher.find() ? matcher.group(1).trim() : defaultValue;
  }

  // Fallback amount extraction from subject
  private String extractFallbackAmount(String subject) {
    Matcher m = Pattern.compile("(Rs\\.?\\s*[\\d.]+|₹\\s*[\\d.]+)").matcher(subject);
    return m.find() ? m.group(1) : "0.00";
  }

  // Helper to clean up "₹ 2545" to "₹2545" (optional preference)
  private String cleanAmount(String raw) {
    return raw.toLowerCase().replace(" ", "");
  }

}
