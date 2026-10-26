package com.project.hardwarehub.controller;

import com.project.hardwarehub.service.GeminiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private GeminiChatService geminiChatService;

    // In-memory counter keyed by "userId_YYYY-MM-DD" -> count
    private final Map<String, Integer> dailyUsageMap = new ConcurrentHashMap<>();
    private static final int DAILY_LIMIT = 5;

    @PostMapping
    public ResponseEntity<?> handleChat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        Object userIdObj = request.get("userId");

        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty."));
        }

        if (userIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "You must be signed in to use the Hardware Assistant."));
        }

        String today = LocalDate.now().toString(); // e.g. "2026-09-10"
        String usageKey = userIdObj.toString() + "_" + today;

        int currentCount = dailyUsageMap.getOrDefault(usageKey, 0);

        // Check if user exceeded 5 questions
        if (currentCount >= DAILY_LIMIT) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Daily limit reached.",
                    "limitExceeded", true,
                    "message", "Today's limit of 5 questions is exhausted. Your quota will refresh tomorrow."
            ));
        }

        // Call Gemini
        String botReply = geminiChatService.askHardwareBot(message);

        // Increment count
        dailyUsageMap.put(usageKey, currentCount + 1);

        return ResponseEntity.ok(Map.of(
                "reply", botReply,
                "questionsUsed", currentCount + 1,
                "questionsRemaining", DAILY_LIMIT - (currentCount + 1)
        ));
    }
}