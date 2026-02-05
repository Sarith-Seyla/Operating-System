import java.util.concurrent.*;
import java.util.*;

public class ThreadActivity {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 1. SingleThreadExecutor ===");
        runSingleThread();

        System.out.println("\n=== 2. CachedThreadPool ===");
        runCachedPool();

        System.out.println("\n=== 3. Fork-Join Parallelism Visualization ===");
        runForkJoin();
    }

    // --- 1. Single Thread Executor ---
    private static void runSingleThread() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        // Submit 5 tasks. They will run one by one because it's a single thread.
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " running on " + Thread.currentThread().getName());
            });
        }
        executor.shutdown();
        try { executor.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    // --- 2. Cached Thread Pool ---
    private static void runCachedPool() {
        // Creates new threads as needed, but will reuse previously constructed threads when they are available.
        ExecutorService executor = Executors.newCachedThreadPool();
        
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " running on " + Thread.currentThread().getName());
            });
        }
        executor.shutdown();
        try { executor.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    // --- 3. Fork-Join Visualization ---
    private static void runForkJoin() {
        ForkJoinPool pool = new ForkJoinPool();
        // Create a task to sum numbers from 0 to 20
        VisualizeTask task = new VisualizeTask(0, 20);
        pool.invoke(task);
    }

    // Recursive Task to visualize the splitting
    static class VisualizeTask extends RecursiveAction {
        private int start;
        private int end;
        private static final int THRESHOLD = 5; // Split if range is larger than 5

        public VisualizeTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            // Visualize the current range and thread
            String indent = "  ".repeat(start/5); // Indentation for visual effect
            System.out.println(indent + "Thread: " + Thread.currentThread().getName() + " processing range [" + start + " - " + end + "]");

            if ((end - start) <= THRESHOLD) {
                // Base case: Compute directly
                System.out.println(indent + "-> Reached base case. Working...");
            } else {
                // Recursive case: Split task
                int mid = (start + end) / 2;
                System.out.println(indent + "-> Splitting into [" + start + "-" + mid + "] and [" + mid + "-" + end + "]");
                
                VisualizeTask leftTask = new VisualizeTask(start, mid);
                VisualizeTask rightTask = new VisualizeTask(mid, end);
                
                // Fork subtasks
                invokeAll(leftTask, rightTask);
            }
        }
    }
}
