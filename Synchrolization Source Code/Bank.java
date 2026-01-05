import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank implements Runnable {

    static int balance = 0;
    
    // 'static' lock to simulate class-level lock in Python
    // static Lock lock = new ReentrantLock(); 

    public void deposit() {
        // try { Thread.sleep(1000); } catch (InterruptedException e) {} // Simulates delay
        balance += 100;
    }

    public void withdraw() {
        balance -= 100;
    }

    public int getValue() {
        return balance;
    }

    @Override
    public void run() {
        // lock.lock(); // Equivalent to: with Bank.lock:
        try {
            this.deposit();
            System.out.println("Value for Thread after deposit " + Thread.currentThread().getName() + " " + this.getValue());
            
            this.withdraw();
            System.out.println("Value for Thread after withdraw " + Thread.currentThread().getName() + " " + this.getValue());
        } finally {
            // lock.unlock();
        }
    }
}