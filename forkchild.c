#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

int main() {
    pid_t pid;

    // 1. Create a child process
    pid = fork();

    if (pid < 0) {
        // Error occurred
        fprintf(stderr, "Fork failed\n");
        return 1;
    } else if (pid == 0) {
        // 2. Child Process
        printf("Child process created (PID: %d). Running 'ls'...\n", getpid());
        
        // execlp searches for the program in the PATH
        // Arguments: filename, arg0 (program name), arg1... , NULL terminator
        execlp("ls", "ls", "-l", NULL);
        
        // If execlp returns, it must have failed
        perror("Exec failed");
        exit(1);
    } else {
        // 3. Parent Process
        printf("Parent process (PID: %d) waiting for child to complete...\n", getpid());
        
        // Wait for the child process to finish
        wait(NULL);
        
        printf("Child process completed.\n");
    }

    return 0;
}
