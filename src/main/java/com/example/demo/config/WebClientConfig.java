package com.example.demo.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebClientConfig {
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models";

    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
}
