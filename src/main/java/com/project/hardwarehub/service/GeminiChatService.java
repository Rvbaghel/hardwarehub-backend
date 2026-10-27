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

    public GeminiChatService() { this.restClient = RestClient.builder().build(); }

    public String askHardwareBot(String userPrompt) {
        return generateText(
                "You are HardwareHub AI, an expert embedded electronics and hardware engineering assistant. "
                        + "Answer concisely and specifically with voltage tolerances, pinouts, logic levels, and datasheets. "
                        + "Politely decline questions unrelated to electronics or computing hardware.",
                userPrompt);
    }

    public String summarizeBlog(String title, String manufacturer, String useCases, String content) {
        String prompt = "Summarize this hardware blog for a reader. Use clear headings and concise bullet points. "
                + "Include what the component is, key specifications stated in the article, common uses, and compatibility/safety notes. "
                + "Do not invent specifications.\\nTitle: " + safe(title)
                + "\\nManufacturer: " + safe(manufacturer)
                + "\\nUse cases: " + safe(useCases)
                + "\\nArticle:\\n" + safe(content);
        return generateText("You are HardwareHub's technical editor. Be accurate, concise, and grounded in the supplied blog.", prompt);
    }

    public String compareHardware(String title, String manufacturer, String useCases, String content, String category) {
        String prompt = "Compare the component in this blog with exactly five relevant real-world hardware alternatives. "
                + "Return Markdown: a short introduction, a table with columns Component, Typical use, Main advantage, Limitation, Compatibility note, "
                + "and a short selection guide. Do not invent precise specifications, prices, or availability. If uncertain, advise checking the datasheet. "
                + "Clearly state alternatives may not be drop-in replacements."
                + "\\nBlog title: " + safe(title) + "\\nManufacturer: " + safe(manufacturer)
                + "\\nCategory: " + safe(category) + "\\nUse cases: " + safe(useCases) + "\\nArticle:\\n" + safe(content);
        return generateText("You are HardwareHub's hardware comparison specialist. Be accurate and explain compatibility limitations.", prompt);
    }

    private String generateText(String systemInstruction, String userPrompt) {
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalStateException("Gemini API key is not configured on the server.");

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=" + apiKey;
        Map<String, Object> requestBody = Map.of(
                "system_instruction", Map.of("parts", List.of(Map.of("text", systemInstruction))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userPrompt))))
        );

        try {
            Map<?, ?> response = restClient.post().uri(url)
                    .contentType(MediaType.APPLICATION_JSON).body(requestBody)
                    .retrieve().body(Map.class);
            if (response != null && response.get("candidates") instanceof List<?> candidates && !candidates.isEmpty()) {
                Object candidateObj = candidates.get(0);
                if (candidateObj instanceof Map<?, ?> candidate
                        && candidate.get("content") instanceof Map<?, ?> content
                        && content.get("parts") instanceof List<?> parts && !parts.isEmpty()
                        && parts.get(0) instanceof Map<?, ?> part && part.get("text") != null) {
                    return part.get("text").toString();
                }
            }
            throw new IllegalStateException("No response received from Gemini.");
        } catch (Exception ex) {
            throw new IllegalStateException("Gemini request failed: " + ex.getMessage(), ex);
        }
    }

    private String safe(String value) { return value == null ? "" : value; }
}
