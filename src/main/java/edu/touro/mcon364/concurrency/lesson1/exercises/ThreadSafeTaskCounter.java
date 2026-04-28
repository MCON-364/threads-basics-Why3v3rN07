package edu.touro.mcon364.concurrency.lesson1.exercises;

/**
 * Exercise 1:
 * Make this class thread-safe so that concurrent increments do not lose updates.
 */
public class ThreadSafeTaskCounter {

    private int count;

    public synchronized void increment() {
        // TO-DO: make this operation thread-safe
        count++;
    }

    public synchronized int getCount() {
        // TO-DO: ensure callers see a correct value under concurrent use
        return count;
    }
}
