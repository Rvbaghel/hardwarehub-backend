package com.project.hardwarehub.controller;

import com.project.hardwarehub.entity.HardwareBlog;
import com.project.hardwarehub.repository.HardwareBlogRepository;
import com.project.hardwarehub.service.GeminiChatService;
import com.project.hardwarehub.service.GeminiUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/blog-ai")
@CrossOrigin(origins = "*")
public class BlogAiController {
    @Autowired private HardwareBlogRepository hardwareBlogRepository;
    @Autowired private GeminiChatService geminiChatService;
    @Autowired private GeminiUsageService geminiUsageService;

    // Initial in-memory one-time tracking; a restart clears this set.
    private final Set<String> usedFeatures = ConcurrentHashMap.newKeySet();

    @PostMapping("/{blogId}/summary")
    public ResponseEntity<?> summarize(@PathVariable Long blogId, @RequestBody Map<String, Object> request) {
        return runFeature(blogId, request, "summary");
    }

    @PostMapping("/{blogId}/compare")
    public ResponseEntity<?> compare(@PathVariable Long blogId, @RequestBody Map<String, Object> request) {
        return runFeature(blogId, request, "compare");
    }

    private synchronized ResponseEntity<?> runFeature(Long blogId, Map<String, Object> request, String feature) {
        Object userIdObj = request.get("userId");
        if (userIdObj == null || userIdObj.toString().isBlank())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please sign in to use blog AI features."));

        String userId = userIdObj.toString();
        String useKey = userId + ":" + blogId + ":" + feature;
        if (usedFeatures.contains(useKey))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "This feature has already been used for this blog.",
                    "alreadyUsed", true, "feature", feature));

        HardwareBlog blog = hardwareBlogRepository.findById(blogId).orElse(null);
        if (blog == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Blog not found."));

        try {
            GeminiUsageService.UsageResult<String> result = geminiUsageService.execute(userId, () -> {
                if ("summary".equals(feature)) {
                    return geminiChatService.summarizeBlog(blog.getTitle(), blog.getManufacturer(),
                            blog.getPrimaryUseCases(), blog.getContent());
                }
                String categoryName = blog.getCategory() == null ? "" : blog.getCategory().getName();
                return geminiChatService.compareHardware(blog.getTitle(), blog.getManufacturer(),
                        blog.getPrimaryUseCases(), blog.getContent(), categoryName);
            });
            usedFeatures.add(useKey);
            return ResponseEntity.ok(Map.of(
                    "result", result.getResult(), "feature", feature, "blogId", blogId,
                    "questionsUsed", result.getQuestionsUsed(),
                    "questionsRemaining", result.getQuestionsRemaining()));
        } catch (GeminiUsageService.DailyLimitExceededException ex) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "error", "Daily AI request limit reached.", "limitExceeded", true,
                    "dailyLimit", GeminiUsageService.DAILY_LIMIT,
                    "message", "Today's limit of 12 successful AI requests is exhausted."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "AI generation failed. Please try again later."));
        }
    }
}
