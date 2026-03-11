import java.util.HashMap;
import java.util.Map;

public class UC1 {
    private Map<String, Integer> usernames;
    private Map<String, Integer> attempts;

    public UC1() {
        usernames = new HashMap<>();
        attempts = new HashMap<>();
    }

    public boolean checkAvailability(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
        return !usernames.containsKey(username);
    }

    public String[] suggestAlternatives(String username) {
        String[] suggestions = new String[4];
        int index = 0;
        for (int i = 1; i <= 3; i++) {
            String alt = username + i;
            if (!usernames.containsKey(alt)) {
                suggestions[index++] = alt;
            }
        }
        String altDot = username.replace("_", ".");
        if (!usernames.containsKey(altDot)) {
            suggestions[index++] = altDot;
        }
        String[] result = new String[index];
        System.arraycopy(suggestions, 0, result, 0, index);
        return result;
    }

    public String getMostAttempted() {
        return attempts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    public void addUser(String username, int userId) {
        usernames.put(username, userId);
    }
}
