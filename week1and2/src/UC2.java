import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UC2 {
    private Map<String, AtomicInteger> stock;
    private Map<String, Queue<Integer>> waitingList;

    public UC2() {
        stock = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    public void addProduct(String productId, int initialStock) {
        stock.put(productId, new AtomicInteger(initialStock));
        waitingList.put(productId, new LinkedList<>());
    }

    public int checkStock(String productId) {
        return stock.getOrDefault(productId, new AtomicInteger(0)).get();
    }

    public String purchaseItem(String productId, int userId) {
        AtomicInteger currentStock = stock.get(productId);
        if (currentStock == null) {
            return "Product not found";
        }
        synchronized (currentStock) {
            if (currentStock.get() > 0) {
                currentStock.decrementAndGet();
                return "Success, " + currentStock.get() + " units remaining";
            } else {
                Queue<Integer> queue = waitingList.get(productId);
                queue.add(userId);
                return "Added to waiting list, position #" + queue.size();
            }
        }
    }

    public List<Integer> getWaitingList(String productId) {
        return new ArrayList<>(waitingList.getOrDefault(productId, new LinkedList<>()));
    }
}