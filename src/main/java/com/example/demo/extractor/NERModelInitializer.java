package com.example.demo.extractor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import com.example.demo.util.AppConstants;
import jakarta.annotation.PostConstruct;

@Service
public class NERModelInitializer {

  private static final Path MODEL_PATH = Paths.get(AppConstants.MODEL_PATH);
  private final NERExtractor extractor;

  public NERModelInitializer(NERExtractor extractor) {
    this.extractor = extractor;
  }

    @PostConstruct
    public void initModel() {
        try {
            // Ensure the directory exists
            if (Files.notExists(MODEL_PATH.getParent())) {
                Files.createDirectories(MODEL_PATH.getParent());
                System.out.println("Created directory: " + MODEL_PATH.getParent());
            }

            // Check if model file exists
            if (Files.notExists(MODEL_PATH)) {
                System.out.println("NER model file not found. Training a new one...");
                extractor.trainData();
            } else {
                System.out.println("NER model file already exists at: " + MODEL_PATH.toAbsolutePath());
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize NER model", e);
        }
    }
}
