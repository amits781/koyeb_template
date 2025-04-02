package com.example.demo.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.extractor.NERExtractor;

@Component
public class EmailProcessorICICI implements EmailProcessor {

  private static DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

  private final NERExtractor extractor;

  public EmailProcessorICICI(NERExtractor extractor) {
    this.extractor = extractor;
  }

  @Override
  public EmailResponseBody processEmail(EmailRequestBody request) {
    Map<String, List<String>> extractedToken =
        extractor.extractEntities(request.getBody().toLowerCase(), "custom-ner-model.bin");
    String amount = String.join(", ",
        CollectionUtils.isEmpty(extractedToken.getOrDefault("AMOUNT", null)) ? List.of()
            : extractedToken.getOrDefault("AMOUNT", null));
    String category = "Travel";
    String details = String.join(", ",
        CollectionUtils.isEmpty(extractedToken.getOrDefault("MERCHANT", null)) ? List.of()
            : extractedToken.getOrDefault("MERCHANT", null));
    String source = String.join(", ",
        CollectionUtils.isEmpty(extractedToken.getOrDefault("BANK", null)) ? List.of()
            : extractedToken.getOrDefault("BANK", null));
    String id = "tnx_id";

    ZonedDateTime gmtDateTime = ZonedDateTime.parse(request.getDate(), formatter);
    ZonedDateTime istDateTime = gmtDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
    String formattedIST = istDateTime.format(formatter);
    String date = formattedIST;
    EmailResponseBody emailResponse =
        EmailResponseBody.builder().tnxAmount(amount).tnxCategory(category).tnxDate(date)
            .tnxDetails(details).tnxSource(source).tnxId(id).build();
    return emailResponse;
  }

}
