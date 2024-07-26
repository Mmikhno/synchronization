import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Program {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        Thread printThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Map.Entry<Integer, Integer> entry = sizeToFreq.entrySet()
                            .stream()
                            .max((e1, e2) -> e1.getValue().compareTo(e2.getValue())
                            ).get();
                    System.out.printf("Текущий максимум: %d (встретился %d раз)%n", entry.getKey(), entry.getValue());
                }
            }
        });
        printThread.start();
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                String path = generateRoute("RLRFR", 100);
                int countR = 0;
                for (int j = 0; j < path.length(); j++) {
                    if (path.charAt(j) == 'R') {
                        countR++;
                    }
                }
                synchronized (sizeToFreq) {
                    if (!sizeToFreq.containsKey(countR)) {
                        sizeToFreq.put(countR, 1);
                    } else {
                        sizeToFreq.put(countR, sizeToFreq.get(countR) + 1);
                    }
                    sizeToFreq.notify();
                }
            });
            thread.start();
            thread.join();
        }
        printThread.interrupt();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}