package com.example.demo.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.util.DateUtils;


@Component
public class HdfcUpiParser implements BankEmailParser {

    // Regex 1: "Rs.2229.05"
    // Captures "Rs." followed by digits and optional decimals
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(Rs\\.\\d+(?:\\.\\d{1,2})?)");

    // Regex 2: "debited from account 2045"
    // Captures the source account
    private static final Pattern SOURCE_PATTERN = Pattern.compile("debited from (account \\d+)");

    // Regex 3: "to VPA amznlpa... IRCTC on"
    // Captures everything between "to VPA" and "on" (non-greedy)
    private static final Pattern DETAILS_PATTERN = Pattern.compile("to VPA (.*?) on");

    // Regex 4: "reference number is 535338654522"
    // Captures the numeric ID
    private static final Pattern TXN_ID_PATTERN = Pattern.compile("reference number is (\\d+)");

    @Override
    public boolean canParse(EmailRequestBody email) {
        // Checks specific HDFC sender and UPI subject keywords
        return (email.getFrom().contains("hdfcbank.net") || email.getFrom().contains("hdfcbank.com")) &&
               email.getSubject().contains("UPI txn");
    }

    @Override
    public EmailResponseBody parse(EmailRequestBody email) {
        String body = email.getBody();

        String amount = extract(AMOUNT_PATTERN, body, "Unknown Amount");
        String sourceRaw = extract(SOURCE_PATTERN, body, "HDFC Account");
        String details = extract(DETAILS_PATTERN, body, "Unknown Beneficiary");
        String txnId = extract(TXN_ID_PATTERN, body, "tnx_id");

        // Format Source to look nicer: "account 2045" -> "HDFC Bank account 2045"
        String formattedSource = "HDFC Bank " + sourceRaw;

        return EmailResponseBody.builder()
                .tnxSource(formattedSource)
            .tnxAmount(amount.toLowerCase())
                .tnxId(txnId)
            .tnxDate(DateUtils.getFormattedDate(email.getDate()))
                .tnxDetails(details.trim())
                .build();
    }

    private String extract(Pattern pattern, String text, String defaultValue) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return defaultValue;
    }

}