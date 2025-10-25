package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.dto.GeminiNERDataResponse;
import com.example.demo.extractor.NERExtractor;
import com.example.demo.util.AppUtils;
import com.example.demo.util.EntityTagParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailProcessorGeneric implements EmailProcessor {

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

    private final NERExtractor extractor;
    private final GeminiApiService geminiApiService;

    public EmailProcessorGeneric(NERExtractor extractor, GeminiApiService geminiApiService, ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.extractor = extractor;
        this.geminiApiService = geminiApiService;
    }

    public EmailResponseBody getTrainingData(EmailRequestBody request) {
      String geminiQuery = AppUtils.prepareQueryBodyForGemmeni(request.getBody().toLowerCase());
        GeminiNERDataResponse gemResponse = geminiApiService.generateContent(geminiQuery);
        System.out.println(gemResponse);

        try {
          Path filePath = Paths.get("data/custom-ner-data.txt");
          System.out.println(filePath.toString());
            // Append a single string
            Files.write(filePath,
                (gemResponse.getTrainingdata() + System.lineSeparator()).getBytes(),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            System.out.println("String appended to file using NIO.2 Files.write()!");
            extractor.trainData();
        } catch (IOException e) {
            System.err.println("An error occurred while appending to the file: " + e.getMessage());
        }
        Map<String, List<String>> extractedToken =
            EntityTagParser.extractEntity(gemResponse.getTrainingdata());
        return prepareTnxResponseData(request, extractedToken);
    }

    @Override
    public EmailResponseBody processEmail(EmailRequestBody request) {
        Map<String, List<String>> extractedToken =
            extractor.extractEntities(request.getBody().toLowerCase(), "custom-ner-model-v2.bin");
        EmailResponseBody response = prepareTnxResponseData(request, extractedToken);

        int retryCount = 0;
        while (response.isNotValidData() && retryCount < 1) {
          response = getTrainingData(request);
          retryCount++;
        }
        return response;
      }

      private EmailResponseBody prepareTnxResponseData(EmailRequestBody request,
          Map<String, List<String>> extractedToken) {
        String amount = String.join(", ",
            CollectionUtils.isEmpty(extractedToken.getOrDefault("AMOUNT", null)) ? List.of()
                : extractedToken.getOrDefault("AMOUNT", null));
        String category = "Travel";
        String details = String.join(", ",
            CollectionUtils.isEmpty(extractedToken.getOrDefault("MERCHANT", null)) ? List.of()
                : extractedToken.getOrDefault("MERCHANT", null));
        String source = getSourceName(extractedToken);
        String id = "tnx_id";

        ZonedDateTime gmtDateTime = ZonedDateTime.parse(request.getDate(), formatter);
        ZonedDateTime istDateTime = gmtDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
        String date = istDateTime.format(formatter);
        return EmailResponseBody.builder().tnxAmount(amount).tnxCategory(category).tnxDate(date)
            .tnxDetails(details).tnxSource(source).tnxId(id).build();
    }

    private String getSourceName(Map<String, List<String>> extractedToken) {
        String bankName = String.join(", ",
                CollectionUtils.isEmpty(extractedToken.getOrDefault("BANK", null)) ? List.of()
                        : extractedToken.getOrDefault("BANK", null));
        String cardType = String.join(", ",
                CollectionUtils.isEmpty(extractedToken.getOrDefault("CARD_TYPE", null)) ? List.of()
                        : extractedToken.getOrDefault("CARD_TYPE", null));
        String cardNumber = String.join(", ",
                CollectionUtils.isEmpty(extractedToken.getOrDefault("CARD_NUMBER", null)) ? List.of()
                        : extractedToken.getOrDefault("CARD_NUMBER", null));
        return String.join(" | ", bankName, cardType, cardNumber);
    }

}
