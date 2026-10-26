package com.project.hardwarehub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiChatService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestClient restClient;

    public GeminiChatService() {
        this.restClient = RestClient.builder().build();
    }

    public String askHardwareBot(String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is not configured on the server.");
        }

        // Change this line:
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=" + apiKey;

        // System prompt instructing the model to act as a hardware specialist
        String systemInstruction = "You are HardwareHub AI, an expert embedded electronics and hardware engineering assistant. "
                + "Answer concisely and specifically with voltage tolerances, pinouts, logic levels, and datasheets. "
                + "Politely decline questions that are not related to electronics or computing hardware.";

        // Payload structure required by the Gemini API
        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", systemInstruction))
                ),
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(Map.of("text", userPrompt))
                        )
                )
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            // Navigate response JSON: candidates[0].content.parts[0].text
            if (response != null && response.containsKey("candidates")) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
                    List<?> parts = (List<?>) content.get("parts");
                    Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
                    return firstPart.get("text").toString();
                }
            }
            return "No response received from hardware model.";
        } catch (Exception e) {
            return "Error querying hardware assistant: " + e.getMessage();
        }
    }
}