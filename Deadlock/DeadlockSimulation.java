import java.util.concurrent.Semaphore;

// Requirement: Create shared resources classes: account1, account2
class Account {
    String name;
    double balance;
    // Requirement: Apply semaphore lock on the critical section
    Semaphore lock; 

    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
        // A Semaphore initialized with 1 acts as a Mutex (mutual exclusion)
        this.lock = new Semaphore(1);
    }
}

public class DeadlockSimulation {

    // Requirement: Transfer function with critical section
    public static void transfer(Account fromAccount, Account toAccount, double amount) {
        String threadName = Thread.currentThread().getName();
        
        try {
            System.out.println(threadName + " attempting to lock " + fromAccount.name);
            
            // 1. Acquire lock for the source account (HOLD)
            fromAccount.lock.acquire();
            System.out.println(threadName + " locked " + fromAccount.name);
            
            // ARTIFICIAL DELAY: This forces the Deadlock to happen.
            // It gives the other thread time to lock its own account before this thread continues.
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            System.out.println(threadName + " waiting for lock on " + toAccount.name + "...");
            
            // 2. Attempt to acquire lock for the destination account (WAIT)
            // This line will BLOCK forever because the other thread holds this lock.
            toAccount.lock.acquire();
            
            // --- CRITICAL SECTION (This part is never reached) ---
            System.out.println(threadName + " acquired both locks! Transferring...");
            if (fromAccount.balance >= amount) {
                fromAccount.balance -= amount;
                toAccount.balance += amount;
                System.out.println("Transfer Successful. New Balance: " + fromAccount.balance);
            } else {
                System.out.println("Insufficient funds.");
            }
            // -----------------------------------------------------

            // Release locks
            toAccount.lock.release();
            fromAccount.lock.release();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create shared resources
        Account account1 = new Account("Account 1", 1000);
        Account account2 = new Account("Account 2", 1000);

        // Requirement: Create 2 threads that will call this transfer function
        
        // Thread 1: Transfers from Account 1 -> Account 2
        Thread t1 = new Thread(() -> {
            transfer(account1, account2, 100);
        }, "Thread-1");

        // Thread 2: Transfers from Account 2 -> Account 1
        Thread t2 = new Thread(() -> {
            transfer(account2, account1, 100);
        }, "Thread-2");

        // Run at the same time
        t1.start();
        t2.start();
        
        // Note: The program will hang here indefinitely. 
        // You will have to manually stop it in your IDE.
    }
}