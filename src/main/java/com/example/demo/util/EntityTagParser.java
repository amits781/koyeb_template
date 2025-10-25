package com.example.demo.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityTagParser {
  public static void main(String[] args) {
    String tnxSource = " |  | ";
    System.out.println(tnxSource.matches(".*[A-Za-z].*"));
  }

  public static Map<String, List<String>> extractEntity(String text) {
    // Regex to match patterns like <START:TYPE> value <END>
    Pattern pattern = Pattern.compile("<START:(\\w+)>\\s*(.*?)\\s*<END>");
    Matcher matcher = pattern.matcher(text);

    Map<String, List<String>> entityMap = new LinkedHashMap<>();

    while (matcher.find()) {
      String entityType = matcher.group(1).trim();
      String entityValue = matcher.group(2).trim();
      entityMap.computeIfAbsent(entityType, k -> new ArrayList<>()).add(entityValue);
    }
    return entityMap;
  }


}
