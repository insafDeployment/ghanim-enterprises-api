package lk.ghanim.api.service;


import lk.ghanim.api.dto.request.ChatRequest;
import lk.ghanim.api.dto.response.ChatResponse;
import lk.ghanim.api.entity.Product;
import lk.ghanim.api.repository.CategoryRepository;
import lk.ghanim.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatbotService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ChatResponse chat(ChatRequest request) {
        int maxRetries = 3;
        int delayMs = 2000;

        for (int i = 0; i < maxRetries; i++) {
            try {
                String systemPrompt = buildSystemPrompt();
                String fullPrompt = buildFullPrompt(
                        request, systemPrompt
                );

                String url = geminiApiUrl + "?key=" + geminiApiKey;

                HttpHeaders headers = new HttpHeaders();

                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body = Map.of(
                        "contents", List.of(
                                Map.of(
                                        "parts", List.of(
                                                Map.of("text", fullPrompt)
                                        )
                                )
                        ),
                        "generationConfig", Map.of(
                                "maxOutputTokens", 800,
                                "temperature", 0.7
                        )
                );

                HttpEntity<Map<String, Object>> entity =
                        new HttpEntity<>(body, headers);

                Map<String,Object> response =
                        restTemplate.postForObject(
                                url, entity, Map.class
                        );

                String reply = extractGeminiReply(response);

                return ChatResponse.builder()
                        .message(reply)
                        .success(true)
                        .build();
            } catch (Exception e) {
                log.warn("Attempt {} failed: {}", i + 1, e.getMessage());
                if (i < maxRetries - 1) {
                    try { Thread.sleep(delayMs); }
                    catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                }
            }
        }

        return ChatResponse.builder()
                .message("Sorry, I am having trouble right now. Please try again in a moment.")
                .success(false)
                .build();

    }

    private String buildSystemPrompt() {
        List<Product> products =
                productRepository.findByActiveTrue();

        String productList = products.stream()
                .map(p -> String.format(
                        "- %s (%s): Rs. %s retail | Stock: %d",
                        p.getName(),
                        p.getCategory().getName(),
                        p.getRetailPrice().toString(),
                        p.getStock()
                ))
                .collect(Collectors.joining("\n"));

        String categories = categoryRepository
                .findByActiveTrue()
                .stream()
                .map(c -> c.getName())
                .collect(Collectors.joining(", "));

        return """
            You are a helpful shopping assistant for
            Ghanim Enterprises, a household products
            shop in Sri Lanka.

            SHOP POLICIES:
            - Currency: Sri Lankan Rupees (Rs.)
            - Free delivery on orders over Rs. 5,000
            - Delivery fee Rs. 350 below Rs. 5,000
            - 7 day easy returns
            - Promo: SAVE10 for 10%% off, FLAT500 for Rs. 500 off
            - Both retail and wholesale pricing available

            CATEGORIES: %s

            OUR PRODUCTS:
            %s

            RULES:
            - Only recommend products from the list above
            - Always mention prices in Rs.
            - Keep responses under 100 words
            - Be friendly and helpful
            - If product not in list say we do not carry it
            - IMPORTANT: Do NOT use markdown formatting,
              asterisks, or bullet symbols in your response.
              Use plain text only. Use line breaks to separate items.
            """.formatted(categories, productList);
    }

    private String buildFullPrompt(
            ChatRequest request, String systemPrompt) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(systemPrompt).append("\n\n");

        if (request.getHistory() != null &&
                !request.getHistory().isEmpty()) {
            prompt.append("CONVERSATION HISTORY:\n");
            for (ChatRequest.ChatMessage msg :
                    request.getHistory()) {
                prompt.append(
                        msg.getRole().toUpperCase()
                ).append(": ").append(
                        msg.getContent()
                ).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("CUSTOMER: ")
                .append(request.getMessage())
                .append("\n\nASSISTANT:");

        return prompt.toString();
    }

    private String extractGeminiReply(
            Map<String, Object> response) {
        try {
            if (response == null) return "No response";

            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>)
                            response.get("candidates");

            if (candidates == null || candidates.isEmpty())
                return "No response";

            Map<String, Object> content =
                    (Map<String, Object>)
                            candidates.get(0).get("content");

            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>)
                            content.get("parts");

            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            log.error("Failed to extract reply: {}",
                    e.getMessage());
            return "Sorry I could not process that.";
        }
    }

}
