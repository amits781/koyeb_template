package com.example.demo.service;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.example.demo.exception.ServiceAPIException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeminiApiService {

  private final ObjectMapper objectMapper;
  private final WebClient webClient;

  @Value("${app.gemini-api-key}")
  private String apiKey;

  public GeminiApiService(WebClient webClient, ObjectMapper objectMapper) {
    this.webClient = webClient;
    this.objectMapper = objectMapper;
  }

  public String generateContent(String requestBody) {
    String url = "/gemini-2.0-flash:generateContent?key=" + apiKey;
    try {
      String response = webClient.post().uri(url).bodyValue(requestBody).retrieve()
          .bodyToMono(String.class).map(this::extractText).block();
      return response;
    } catch (WebClientResponseException e) { // Handle API errors
      log.error("API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
      throw new ServiceAPIException(
          "API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
          HttpStatus.BAD_REQUEST);
    } catch (Exception e) { // Catch unexpected exceptions
      log.error("Unexpected Error: " + e.getMessage());
      throw new ServiceAPIException("Unexpected Error: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private String extractText(String response) {
    try {
      JsonNode rootNode = objectMapper.readTree(response);
      return rootNode.path("candidates").get(0) // First candidate
          .path("content").path("parts").get(0) // First part
          .path("text").asText(); // Extract text
    } catch (IOException e) {
      log.error("Error while parsing AI response: " + e.getMessage());
      throw new ServiceAPIException("Error while parsing AI response: " + e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
