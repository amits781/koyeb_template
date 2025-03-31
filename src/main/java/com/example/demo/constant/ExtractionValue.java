package com.example.demo.constant;

import java.util.Arrays;
import org.springframework.http.HttpStatus;
import com.example.demo.exception.ServiceAPIException;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum ExtractionValue {
  
  AMOUNT("amount"), ID("transaction id"), SOURCE("source of transaction"), INFO(
      "info of transaction like where this happened"), DATE("transaction date and time"), DETAILS(
          "any other details specifying source/destination of transaction");
  
  private final String value;
  
  private ExtractionValue(String value) {
    this.value = value;
  }

  // Static method to fetch enum from value
  public static ExtractionValue fromValue(String value) {
    return Arrays.stream(ExtractionValue.values())
        .filter(extractionObject -> extractionObject.value.equals(value)).findFirst().orElseThrow(
            () -> new ServiceAPIException("No ExtractionValue found for string: " + value,
                HttpStatus.BAD_REQUEST));
  }
}
