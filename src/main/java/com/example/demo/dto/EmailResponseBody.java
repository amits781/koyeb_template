package com.example.demo.dto;

import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailResponseBody {
  private String tnxSource;
  private String tnxAmount;
  private String tnxId;
  private String tnxDate;
  private String tnxDetails;
  private String tnxCategory;

  @JsonIgnore
  public boolean isNotValidData() {
    boolean invalidSource = ObjectUtils.isEmpty(tnxSource) || !tnxSource.matches(".*[A-Za-z].*");
    return ObjectUtils.isEmpty(tnxAmount) || ObjectUtils.isEmpty(tnxDate)
        || invalidSource;
  }
}
