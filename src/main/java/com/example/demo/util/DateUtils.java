package com.example.demo.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");

  public static String getFormattedDate(String date) {
    ZonedDateTime gmtDateTime = ZonedDateTime.parse(date, formatter);
    ZonedDateTime istDateTime = gmtDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
    return istDateTime.format(formatter);
  }

}
