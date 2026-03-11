import java.util.*;

class Q4 {
    private Map<String, Set<String>> nGramIndex;
    private int n;

    public Q4(int n) {
        this.n = n;
        this.nGramIndex = new HashMap<>();
    }

    public List<String> extractNGrams(String[] words) {
        List<String> nGrams = new ArrayList<>();
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(" ");
            }
            nGrams.add(sb.toString().trim());
        }
        return nGrams;
    }

    public void indexDocument(String docId, String[] words) {
        List<String> nGrams = extractNGrams(words);
        for (String gram : nGrams) {
            nGramIndex.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    public Map<String, Integer> analyzeDocument(String docId, String[] words) {
        List<String> nGrams = extractNGrams(words);
        Map<String, Integer> matchCount = new HashMap<>();
        for (String gram : nGrams) {
            if (nGramIndex.containsKey(gram)) {
                for (String otherDoc : nGramIndex.get(gram)) {
                    if (!otherDoc.equals(docId)) {
                        matchCount.put(otherDoc, matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }
        return matchCount;
    }

    public void reportSimilarity(String docId, String[] words, int totalNGrams) {
        Map<String, Integer> matches = analyzeDocument(docId, words);
        for (Map.Entry<String, Integer> entry : matches.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / totalNGrams;
            System.out.println("→ Found " + entry.getValue() + " matching n-grams with \""
                    + entry.getKey() + "\"");
            System.out.println("→ Similarity: " + String.format("%.2f", similarity) + "%");
            if (similarity > 60.0) {
                System.out.println("→ PLAGIARISM DETECTED");
            } else if (similarity > 10.0) {
                System.out.println("→ Suspicious");
            }
        }
    }

    public static void main(String[] args) {
        Q4 detector = new Q4(5);

        String[] essay089 = "This is a sample essay with some unique content".split(" ");
        String[] essay092 = "This is a sample essay with some plagiarized content repeated".split(" ");
        String[] essay123 = "This is a sample essay with some plagiarized content repeated again".split(" ");

        detector.indexDocument("essay_089.txt", essay089);
        detector.indexDocument("essay_092.txt", essay092);

        System.out.println("analyzeDocument(\"essay_123.txt\")");
        List<String> nGrams123 = detector.extractNGrams(essay123);
        System.out.println("→ Extracted " + nGrams123.size() + " n-grams");
        detector.reportSimilarity("essay_123.txt", essay123, nGrams123.size());
    }
}