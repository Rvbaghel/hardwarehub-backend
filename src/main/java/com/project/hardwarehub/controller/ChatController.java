package com.project.hardwarehub.controller;

import com.project.hardwarehub.service.GeminiChatService;
import com.project.hardwarehub.service.GeminiUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private GeminiChatService geminiChatService;

    @Autowired
    private GeminiUsageService geminiUsageService;

    /**
     * Returns the current shared daily AI usage for this user.
     * The same GeminiUsageService is used by chat, blog summaries, and comparisons.
     */
    @GetMapping("/usage")
    public ResponseEntity<?> getUsage(@RequestParam("userId") String userId) {
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "You must be signed in to check AI usage."
            ));
        }

        int questionsRemaining = geminiUsageService.getRemaining(userId);
        int questionsUsed = GeminiUsageService.DAILY_LIMIT - questionsRemaining;

        return ResponseEntity.ok(Map.of(
                "dailyLimit", GeminiUsageService.DAILY_LIMIT,
                "questionsUsed", questionsUsed,
                "questionsRemaining", questionsRemaining
        ));
    }

    @PostMapping
    public ResponseEntity<?> handleChat(@RequestBody Map<String, Object> request) {
        Object userIdObj = request.get("userId");
        Object messageObj = request.get("message");
        String message = messageObj instanceof String ? (String) messageObj : null;

        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Message cannot be empty."
            ));
        }

        if (userIdObj == null || userIdObj.toString().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "You must be signed in to use the Hardware Assistant."
            ));
        }

        try {
            var result = geminiUsageService.execute(
                    userIdObj.toString(),
                    () -> geminiChatService.askHardwareBot(message)
            );

            return ResponseEntity.ok(Map.of(
                    "reply", result.getResult(),
                    "questionsUsed", result.getQuestionsUsed(),
                    "questionsRemaining", result.getQuestionsRemaining(),
                    "dailyLimit", GeminiUsageService.DAILY_LIMIT
            ));
        } catch (GeminiUsageService.DailyLimitExceededException ex) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Daily limit reached.",
                    "limitExceeded", true,
                    "dailyLimit", GeminiUsageService.DAILY_LIMIT,
                    "questionsRemaining", 0,
                    "message", "Today's limit of 12 successful AI requests is exhausted. Your quota will refresh tomorrow."
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "error", "The AI service could not complete your request."
            ));
        }
    }
}
