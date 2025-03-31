package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.example.demo.constant.ExtractionValue;
import com.example.demo.dto.EmailRequestBody;
import com.example.demo.dto.EmailResponseBody;
import com.example.demo.dto.RegexValueResponse;
import com.example.demo.entity.EmailExtraction;
import com.example.demo.exception.ServiceAPIException;
import com.example.demo.repository.EmailExtractionRepo;
import com.example.demo.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

  private EmailExtractionRepo repo;
  private GeminiApiService geminiApiService;
  private final ObjectMapper objectMapper;

  public EmailService(EmailExtractionRepo repo, GeminiApiService geminiApiService,
      ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.repo = repo;
    this.geminiApiService = geminiApiService;
  }

  public EmailResponseBody processEmail(EmailRequestBody request) {
    log.info("Receive email body: {}", request);
    List<EmailExtraction> allRegex = repo.findAll();
    String amount = null;
    String category = "Travel";
    String date = null;
    String details = null;
    String source = null;
    String id = null;
    List<ExtractionValue> missingValues = new ArrayList<>();

    if (CollectionUtils.isEmpty(allRegex)) {
      log.info("Initializing DB Regex Object");
      EmailExtraction extractionObject = new EmailExtraction();
      amount = getRegexFromAI(request.getBody(), extractionObject, ExtractionValue.AMOUNT);
      date = getRegexFromAI(request.getBody(), extractionObject, ExtractionValue.DATE);
      details = getRegexFromAI(request.getBody(), extractionObject, ExtractionValue.INFO);
      source = getRegexFromAI(request.getBody(), extractionObject, ExtractionValue.SOURCE);
      id = getRegexFromAI(request.getBody(), extractionObject, ExtractionValue.ID);
      repo.save(extractionObject);
    } else {
      Boolean saveObject = Boolean.FALSE;
      EmailExtraction extractionObject = new EmailExtraction();
      log.info("Found: {} DB Regex Object", allRegex.size());
      amount = getDataFromRegex(allRegex, ExtractionValue.AMOUNT, request.getBody());
      if (Strings.isEmpty(amount)) {
        saveObject = Boolean.TRUE;
        missingValues.add(ExtractionValue.AMOUNT);
        amount = getRegexFromAI(request.getBody(), extractionObject, ExtractionValue.AMOUNT);
      }
      date = getDataFromRegex(allRegex, ExtractionValue.DATE, request.getBody());
      if (Strings.isEmpty(date)) {
        saveObject = Boolean.TRUE;
        missingValues.add(ExtractionValue.DATE);
        date = getRegexFromAI(request.getBody(), extractionObject, ExtractionValue.DATE);
      }
      details = getDataFromRegex(allRegex, ExtractionValue.INFO, request.getBody());
      if (Strings.isEmpty(details)) {
        missingValues.add(ExtractionValue.INFO);
      }
      source = getDataFromRegex(allRegex, ExtractionValue.SOURCE, request.getBody());
      if (Strings.isEmpty(source)) {
        saveObject = Boolean.TRUE;
        missingValues.add(ExtractionValue.SOURCE);
        source = getRegexFromAI(request.getBody(), extractionObject, ExtractionValue.SOURCE);
      }
      id = getDataFromRegex(allRegex, ExtractionValue.ID, request.getBody());
      if (Strings.isEmpty(id)) {
        missingValues.add(ExtractionValue.ID);
      }
      if (saveObject)
        repo.save(extractionObject);
    }

    EmailResponseBody emailResponse = EmailResponseBody.builder().tnxAmount(amount)
        .tnxCategory(category).tnxDate(date).tnxDetails(details).tnxSource(source).tnxId(id)
        .build();
    log.info("Finish processing email body, response: {}", emailResponse);
    return emailResponse;
  }

  private String getRegexFromAI(String emailBody, EmailExtraction extractionObject,
      ExtractionValue extractionType) {
    String response =
        geminiApiService.generateContent(
            AppUtils.prepareQueryBodyForGemmeni(extractionType.getValue(), emailBody));
    log.info("Response for: {} is {}", extractionType.name(), response);
    RegexValueResponse responseObject = getRegexValueFromResponse(response);
    switch (extractionType) {
      case AMOUNT -> extractionObject.setAmountRex(responseObject.getRegex());
      case DATE -> extractionObject.setDateRex(responseObject.getRegex());
      case ID -> extractionObject.setIdRex(responseObject.getRegex());
      case SOURCE -> extractionObject.setSourceRex(responseObject.getRegex());
      case INFO -> extractionObject.setInfoRex(responseObject.getRegex());
      case DETAILS -> extractionObject.setDetailsRex(responseObject.getRegex());
      default -> throw new ServiceAPIException(
          "No ExtractionValue found for string: " + extractionType.name(), HttpStatus.BAD_REQUEST);
    };
    return responseObject.getValue();
  }

  private RegexValueResponse getRegexValueFromResponse(String reponse) {
    try {
      return objectMapper.readValue(reponse, RegexValueResponse.class);
    } catch (Exception e) {
      throw new ServiceAPIException(
          "Error converting JSON response to Object: " + e.getLocalizedMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private String getDataFromRegex(List<EmailExtraction> extractionObject,
      ExtractionValue extractionType, String emailBody) {
    List<String> regexList = switch (extractionType) {
      case AMOUNT -> extractionObject.stream().map(EmailExtraction::getAmountRex).toList();
      case DATE -> extractionObject.stream().map(EmailExtraction::getDateRex).toList();
      case ID -> extractionObject.stream().map(EmailExtraction::getIdRex).toList();
      case SOURCE -> extractionObject.stream().map(EmailExtraction::getSourceRex).toList();
      case INFO -> extractionObject.stream().map(EmailExtraction::getInfoRex).toList();
      case DETAILS -> extractionObject.stream().map(EmailExtraction::getDetailsRex).toList();
    };
    for (String regex : regexList) { // Loop through each regex
      if (Strings.isEmpty(regex))
        continue;
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(emailBody);
      if (matcher.find()) { // If a match is found
        return matcher.group(); // Return the first matched substring
      }
    }
    return null;
  }
}
