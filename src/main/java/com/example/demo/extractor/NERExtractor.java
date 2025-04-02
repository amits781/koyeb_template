package com.example.demo.extractor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.example.demo.exception.ServiceAPIException;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

@Component
@Slf4j
public class NERExtractor {

  private final ResourceLoader resourceLoader;

  public NERExtractor(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  public Map<String, List<String>> extractEntities(String text, String modelPath) {
    try (InputStream modelStream =
        resourceLoader.getResource("classpath:" + modelPath).getInputStream()) {
      log.info("NERExtractor::extractEntities: Model found: {}, begin extraction.", modelPath);
      TokenNameFinderModel customModel = new TokenNameFinderModel(modelStream);
      WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
      NameFinderME nameFinder = new NameFinderME(customModel);
      String[] tokens = tokenizer.tokenize(text);
      log.info("Extracted token length:{}", tokens.length);
      Span[] nameSpans = nameFinder.find(tokens);
      Map<String, List<String>> entitySet = new HashMap<String, List<String>>();
      for (Span span : nameSpans) {
        String entity =
            String.join(" ", Arrays.copyOfRange(tokens, span.getStart(), span.getEnd()));
        entity = entity.trim();
        String spanType = span.getType().trim();
        List<String> entities = entitySet.getOrDefault(spanType, new ArrayList<String>());
        entities.add(entity);
        entitySet.put(spanType, entities);
      }
      log.info("NERExtractor::extractEntities: Exit processing data.");
      return entitySet;
    } catch (Exception e) {
      log.error("Error Occured during text extraction: {}", e.getMessage());
      throw new ServiceAPIException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
