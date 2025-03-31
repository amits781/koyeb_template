package com.example.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ValidateHeader {

  @Value("${app.my-secret}")
  private String secretValue;

  public Boolean validateSecret(String inputHeaderSecret) {
    return secretValue.equals(inputHeaderSecret);
  }

}
