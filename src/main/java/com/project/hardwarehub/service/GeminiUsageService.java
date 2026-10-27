package com.project.hardwarehub.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Shared daily quota for chat, blog summaries, and comparisons.
 * In-memory only: restarts clear usage; multiple instances do not share it.
 */
@Service
public class GeminiUsageService {
    public static final int DAILY_LIMIT = 12;
    private final Map<String, Integer> dailyUsage = new HashMap<>();

    public static class DailyLimitExceededException extends RuntimeException {
        public DailyLimitExceededException() { super("Daily AI request limit reached."); }
    }

    public static class UsageResult<T> {
        private final T result;
        private final int questionsUsed;
        private final int questionsRemaining;
        public UsageResult(T result, int used, int remaining) {
            this.result = result; this.questionsUsed = used; this.questionsRemaining = remaining;
        }
        public T getResult() { return result; }
        public int getQuestionsUsed() { return questionsUsed; }
        public int getQuestionsRemaining() { return questionsRemaining; }
    }

    /** Failed AI calls do not consume a credit. Check, execute, and increment are atomic per app instance. */
    public synchronized <T> UsageResult<T> execute(String userId, Supplier<T> action) {
        String key = userId + "_" + LocalDate.now();
        int used = dailyUsage.getOrDefault(key, 0);
        if (used >= DAILY_LIMIT) throw new DailyLimitExceededException();
        T result = action.get();
        int newUsed = used + 1;
        dailyUsage.put(key, newUsed);
        return new UsageResult<>(result, newUsed, DAILY_LIMIT - newUsed);
    }

    public synchronized int getRemaining(String userId) {
        return Math.max(0, DAILY_LIMIT - dailyUsage.getOrDefault(userId + "_" + LocalDate.now(), 0));
    }
}
