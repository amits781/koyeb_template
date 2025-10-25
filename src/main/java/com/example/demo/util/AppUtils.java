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

  private static String sampleData = """
          <START:BANK> icici bank online <END> dear customer, your <START:CARD_TYPE> icici bank credit card <END> xx <START:CARD_NUMBER> 1234 <END> has been used for a transaction of <START:AMOUNT> inr 725.00 <END> on <START:DATE> mar 30, 2025 <END> at <START:TIME> 04:15:10 <END> . info: <START:MERCHANT> m s nagarmal sheonarain a <END> .
          your <START:CARD_TYPE> visa credit card <END> ending in <START:CARD_NUMBER> 1234 <END> was used for an online transaction of <START:AMOUNT> inr 1500.00 <END> at <START:MERCHANT> amazon <END> on <START:DATE> may 10, 2025 <END> at <START:TIME> 10:15:30 am <END>.
          dear customer, your <START:CARD_TYPE> sbi debit card <END> <START:CARD_NUMBER> xx9876 <END> has been used for a transaction of <START:AMOUNT> inr 1,550.75 <END> on <START:DATE> apr 12, 2025 <END> at <START:TIME> 18:45:22 <END> . info: <START:MERCHANT> reliance retail limited <END> .
           " """;

  private static String templateRequestBodyV2 =
          """
              {
                  "system_instruction": {
                      "parts": [
                          {
                              "text": "You are a text extractor. Your job is to extract the information from the given email body and provide me 
                                    data and NER training data for the given scenario used to extract that data.
                                You must output **only in English**, under all circumstances.
                                If the input text contains any non-English language (e.g., Hindi, Tamil, etc.), you must first translate it fully into English
                                and then extract the information. Do not include any text in other languages in the final output.
                                Data to be extracted and the format of training data are : """
          + sampleData
          + """
                          }
                      ]
                  },
                  "contents": [
                      {
                          "parts": [
                              {
                                        "text": "Extract the data and give me training data only in english for given email string : ':email_body' also ensure none of the field in response is null and the response should be valid JSON Ensure no field in the response is null, and the JSON must be valid.
              If the input contains any non-English text, translate it fully into English first, then extract data."
                              }
                          ]
                      }
                  ],
                  "generationConfig": {
                      "response_mime_type": "application/json",
                      "response_schema": {
                          "type": "OBJECT",
                          "properties": {
                              "values": {
                                  "type": "STRING"
                              },
                              "trainingdata": {
                                  "type": "STRING",
                                  "pattern": "^[A-Za-z0-9 ,.:;<>/_-]+$"
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

  public static String prepareQueryBodyForGemmeni(String emailBody) {
    return
            templateRequestBodyV2.replace(":email_body", emailBody);

  }
}
