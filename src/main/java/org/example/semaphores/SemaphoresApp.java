package org.example.semaphores;

import lombok.SneakyThrows;

import java.util.concurrent.Semaphore;

public class SemaphoresApp {

    private static final Semaphore semaphore = new Semaphore(2);

    @SneakyThrows
    public static void main(String[] args) {
        semaphore.release();
        System.out.println(semaphore.getQueueLength() + " " + semaphore.availablePermits());
        Thread thread1 = getThread();
        Thread thread2 = getThread();
        Thread thread3 = getThread();
        thread1.start();
        thread2.start();
        thread3.start();
        for (int i = 0; i < 10; i++) {
            System.out.println(semaphore.getQueueLength() + " " + semaphore.availablePermits());
            Thread.sleep(500); // Sleep to allow threads to acquire and release the semaphore
        }
    }

    private static Thread getThread() {
        return new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + " acquired the semaphore");
                Thread.sleep(2000); // Simulate some work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                semaphore.release();
                System.out.println(Thread.currentThread().getName() + " released the semaphore");
            }
        });
    }
}
