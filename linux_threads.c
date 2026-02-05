#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

#define NUM_THREADS 5

// The function that each thread will execute
void *perform_work(void *argument) {
    int thread_id = *((int *)argument);
    printf("Thread %d: Started\n", thread_id);
    
    // Simulate work
    sleep(1);
    
    printf("Thread %d: Finished\n", thread_id);
    return NULL;
}

int main() {
    pthread_t threads[NUM_THREADS];
    int thread_args[NUM_THREADS];
    int result_code;
    
    printf("Main Process: Creating %d threads...\n", NUM_THREADS);

    for (int i = 0; i < NUM_THREADS; i++) {
        thread_args[i] = i;
        // Create the thread
        result_code = pthread_create(&threads[i], NULL, perform_work, &thread_args[i]);
        
        if (result_code) {
            printf("Error: Return code from pthread_create() is %d\n", result_code);
            exit(1);
        }
    }

    // Wait for all threads to complete
    for (int i = 0; i < NUM_THREADS; i++) {
        pthread_join(threads[i], NULL);
    }

    printf("Main Process: All threads completed.\n");
    return 0;
}
