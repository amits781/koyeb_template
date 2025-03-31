package com.example.demo.util;

public class AppUtils {

  private static String templateRequestBody =
      """
          {
              "system_instruction": {
                  "parts": [
                      {
                          "text": "You are a text extractor. Your job is to extract the information from the given text and provide me regex used to extract that text"
                      }
                  ]
              },
              "contents": [
                  {
                      "parts": [
                          {
                              "text": "Extract :search_term and give me regex for that from the given email string : :email_body"
                          }
                      ]
                  }
              ],
              "generationConfig": {
                  "response_mime_type": "application/json",
                  "response_schema": {
                      "type": "OBJECT",
                      "properties": {
                          "value": {
                              "type": "STRING"
                          },
                          "regex": {
                              "type": "STRING"
                          }
                      }
                  }
              }
          }
          """;

  public static String prepareQueryBodyForGemmeni(String searchTerm, String emailBody) {
    return
        templateRequestBody.replace(":search_term", searchTerm).replace(":email_body", emailBody);

  }
}
