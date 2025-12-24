public class Main {
    public static void main(String[] args) {
        Bank bankInstance = new Bank();

        // Creating threads
        Thread t1 = new Thread(bankInstance, "Thread1");
        Thread t2 = new Thread(bankInstance, "Thread2");
        Thread t3 = new Thread(bankInstance, "Thread3");

        // Starting threads
        t1.start();
        t2.start();
        t3.start();

        // Joining threads (waiting for them to finish)
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}