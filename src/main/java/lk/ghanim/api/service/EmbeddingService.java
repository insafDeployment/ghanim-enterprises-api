package lk.ghanim.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmbeddingService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.embedding.url}")
    private String embeddingUrl;

    private final RestTemplate restTemplate;


    public float[] generateEmbedding(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(apiKey);


            Map<String, Object> body = Map.of("inputs", text);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            // Use exchange() instead of postForObject()
            ResponseEntity<List> response = restTemplate.exchange(
                    embeddingUrl,
                    HttpMethod.POST,
                    request,
                    List.class
            );

            List responseBody = response.getBody();
            if (responseBody == null || responseBody.isEmpty()) return null;

            List<Double> embedding;

            // Check if response is [[0.1, 0.2, ...]] or [0.1, 0.2, ...]
            if (responseBody.get(0) instanceof List) {
                // Doubly nested — unwrap outer list
                embedding = (List<Double>) responseBody.get(0);
            } else {
                // Already flat list
                embedding = (List<Double>) responseBody;
            }

            float[] result = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                result[i] = embedding.get(i).floatValue();
            }

            log.info("Generated embedding for: {}", text);
            return result;

        } catch (Exception e) {
            log.error("Failed to generate embedding: {}",
                    e.getMessage());
            return null;
        }
    }

    public String buildProductText(String name,
                                   String description,
                                   String categoryName) {
        return String.format(
                "Product: %s. Category: %s. Description: %s",
                name, categoryName, description != null
                        ? description : ""
        );
    }
}