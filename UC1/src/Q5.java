import java.util.*;
import java.util.concurrent.*;

public class Q5 {
    private Map<String, Integer> pageViews;
    private Map<String, Set<String>> uniqueVisitors;
    private Map<String, Integer> trafficSources;

    public Q5() {
        pageViews = new ConcurrentHashMap<>();
        uniqueVisitors = new ConcurrentHashMap<>();
        trafficSources = new ConcurrentHashMap<>();
    }

    public void processEvent(String url, String userId, String source) {
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    public void getDashboard() {
        System.out.println("Top Pages:");
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        pq.addAll(pageViews.entrySet());

        int rank = 1;
        while (!pq.isEmpty() && rank <= 10) {
            Map.Entry<String, Integer> entry = pq.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int uniques = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();
            System.out.println(rank + ". " + url + " - " + views + " views (" + uniques + " unique)");
            rank++;
        }

        int totalSources = trafficSources.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalSources;
            System.out.println(entry.getKey() + ": " + String.format("%.2f", percentage) + "%");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Q5 dashboard = new Q5();

        dashboard.processEvent("/article/breaking-news", "user_123", "google");
        dashboard.processEvent("/article/breaking-news", "user_456", "facebook");
        dashboard.processEvent("/sports/championship", "user_789", "direct");
        dashboard.processEvent("/article/breaking-news", "user_123", "google");
        dashboard.processEvent("/sports/championship", "user_111", "google");

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(dashboard::getDashboard, 0, 5, TimeUnit.SECONDS);
    }
}