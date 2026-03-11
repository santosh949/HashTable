import java.util.*;
import java.util.concurrent.*;

class TokenBucket {
    private int tokens;
    private long lastRefillTime;
    private final int maxTokens;
    private final int refillRatePerHour;

    public TokenBucket(int maxTokens, int refillRatePerHour) {
        this.maxTokens = maxTokens;
        this.refillRatePerHour = refillRatePerHour;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        long hours = elapsed / 3600000;
        if (hours > 0) {
            tokens = maxTokens;
            lastRefillTime = now;
        }
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    public synchronized int getRemainingTokens() {
        refill();
        return tokens;
    }

    public synchronized long getResetTime() {
        return lastRefillTime + 3600000;
    }

    public int getMaxTokens() {
        return maxTokens;
    }
}

public class Q6 {
    private Map<String, TokenBucket> clientBuckets;

    public Q6() {
        clientBuckets = new ConcurrentHashMap<>();
    }

    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(clientId,
                k -> new TokenBucket(1000, 1000));
        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            long retryAfter = (bucket.getResetTime() - System.currentTimeMillis()) / 1000;
            return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
        }
    }

    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);
        if (bucket == null) {
            return "No requests made yet.";
        }
        int used = bucket.getMaxTokens() - bucket.getRemainingTokens();
        return "{used: " + used + ", limit: " + bucket.getMaxTokens() +
                ", reset: " + bucket.getResetTime() + "}";
    }

    public static void main(String[] args) {
        Q6 rateLimiter = new Q6();

        System.out.println(rateLimiter.checkRateLimit("abc123"));
        System.out.println(rateLimiter.checkRateLimit("abc123"));

        for (int i = 0; i < 998; i++) {
            rateLimiter.checkRateLimit("abc123");
        }
        System.out.println(rateLimiter.checkRateLimit("abc123"));
        System.out.println(rateLimiter.getRateLimitStatus("abc123"));
    }
}