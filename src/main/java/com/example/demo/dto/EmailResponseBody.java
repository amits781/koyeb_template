package com.example.demo.dto;

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
}
