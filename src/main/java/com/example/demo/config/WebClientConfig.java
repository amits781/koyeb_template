package com.example.demo.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebClientConfig {

    @Bean
    ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
}
