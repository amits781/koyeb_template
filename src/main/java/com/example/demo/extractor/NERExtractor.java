package com.example.demo.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.example.demo.exception.ServiceAPIException;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

@Component
@Slf4j
public class NERExtractor {

  private final ResourceLoader resourceLoader;

  public NERExtractor(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  public Map<String, List<String>> extractEntities(String text, String modelPath) {
    try (InputStream modelStream =
        resourceLoader.getResource("file:data/" + modelPath).getInputStream()) {
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

  public void trainData() throws IOException {
    // Load training data
    Resource resource = resourceLoader.getResource("file:data/custom-ner-data.txt");

    InputStreamFactory inputStreamFactory =
        () -> resource.getInputStream();
    ObjectStream<String> lineStream =
        new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
    ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
      System.out.println("---- Contents of custom-ner-data.txt ----");
      reader.lines().forEach(System.out::println);
    }
    TokenNameFinderFactory factory = new TokenNameFinderFactory();

    // Ensure Training Parameters are set properly
    TrainingParameters params = TrainingParameters.defaultParams();
    params.put(TrainingParameters.ITERATIONS_PARAM, "100"); // Increase iterations
    params.put(TrainingParameters.CUTOFF_PARAM, "1"); // Allow small dataset training

    // Train model
    TokenNameFinderModel model = NameFinderME.train("en", // Language
        null, // Model type
        sampleStream, params, factory);

    // Save trained model
    try (OutputStream modelOut = Files.newOutputStream(Paths.get("data/custom-ner-model-v2.bin"))) {
      model.serialize(modelOut);
    }

    System.out.println("Custom model trained and saved successfully!");
  }
}
