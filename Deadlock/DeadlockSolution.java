import java.util.concurrent.Semaphore;

class Account {
    String name;
    double balance;
    Semaphore lock; 

    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.lock = new Semaphore(1);
    }
}

public class DeadlockSolution {

    public static void transfer(Account fromAccount, Account toAccount, double amount) {
        String threadName = Thread.currentThread().getName();
        
        // --- DEADLOCK SOLUTION: LOCK ORDERING ---
        // We decide which lock to grab first based on the Account name.
        // This ensures that ALL threads always lock "Account 1" before "Account 2".
        Account firstLock;
        Account secondLock;

        // Use string comparison to decide order. 
        // If fromAccount is "Account 1" and toAccount is "Account 2", we lock from then to.
        // If swapped, we STILL lock "Account 1" (now toAccount) first.
        if (fromAccount.name.compareTo(toAccount.name) < 0) {
            firstLock = fromAccount;
            secondLock = toAccount;
        } else {
            firstLock = toAccount;
            secondLock = fromAccount;
        }
        // ----------------------------------------

        try {
            System.out.println(threadName + " determining lock order: First " + firstLock.name + ", Second " + secondLock.name);
            
            // 1. Acquire the first lock (Always the 'lower' account)
            firstLock.lock.acquire();
            System.out.println(threadName + " locked " + firstLock.name);
            
            // Artificial delay to prove deadlock is impossible now
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            System.out.println(threadName + " waiting for lock on " + secondLock.name + "...");
            
            // 2. Acquire the second lock
            secondLock.lock.acquire();
            System.out.println(threadName + " locked " + secondLock.name);
            
            // --- CRITICAL SECTION ---
            System.out.println(threadName + " acquired both locks! Transferring...");
            if (fromAccount.balance >= amount) {
                fromAccount.balance -= amount;
                toAccount.balance += amount;
                System.out.println("Success! " + fromAccount.name + ": " + fromAccount.balance + " | " + toAccount.name + ": " + toAccount.balance);
            } else {
                System.out.println("Insufficient funds.");
            }
            // ------------------------

            // Release locks
            secondLock.lock.release();
            firstLock.lock.release();
            System.out.println(threadName + " released locks.");
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Account account1 = new Account("Account 1", 1000);
        Account account2 = new Account("Account 2", 1000);

        // Thread 1: Transfers from Acc 1 -> Acc 2
        Thread t1 = new Thread(() -> {
            transfer(account1, account2, 100);
        }, "Thread-1");

        // Thread 2: Transfers from Acc 2 -> Acc 1
        Thread t2 = new Thread(() -> {
            transfer(account2, account1, 100);
        }, "Thread-2");

        t1.start();
        t2.start();
        
        // This time, the program will finish successfully!
    }
}