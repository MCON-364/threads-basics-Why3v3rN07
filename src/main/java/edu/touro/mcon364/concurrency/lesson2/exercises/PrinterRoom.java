package edu.touro.mcon364.concurrency.lesson2.exercises;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exercise 3 — Limiting concurrent access with {@link Semaphore}.
 *
 * Scenario: a shared printer room has exactly {@code printerCount} printers.
 * Many threads (people) want to print, but only {@code printerCount} may print
 * at the same time.  All others must wait.
 *
 * Your tasks:
 *
 * (A) Implement {@link #print(String)}.
 *     - Acquire one permit from the semaphore before printing.
 *     - Increment {@code activeCount} while printing (to let tests observe
 *       concurrent usage).
 *     - Release the permit in a {@code finally} block.
 *     - Decrement {@code activeCount} after releasing.
 *
 * (B) Observe via tests that {@code maxObservedConcurrency} never exceeds
 *     the number of available printers.
 */
public class PrinterRoom {

    private final int printerCount;
    // Declare a private final Semaphore field
    private final Semaphore semaphore;

    // counters visible to tests
    private final AtomicInteger activeCount   = new AtomicInteger(0);
    private final AtomicInteger maxObserved   = new AtomicInteger(0);
    private final AtomicInteger completedJobs = new AtomicInteger(0);

    public PrinterRoom(int printerCount) {
        this.printerCount = printerCount;
        // Initialize the semaphore so that exactly printerCount threads
        //       may be inside print() at the same time
        this.semaphore = new Semaphore(printerCount);
    }

    /**
     * Simulate printing {@code document}.
     * Must block if all printers are busy.
     *
     * @param document the document to print
     */
    public void print(String document) throws InterruptedException {
        // Block here until a printer permit is available
        semaphore.acquire();

        try {
            // Record that one more job is now active, then update the
            //       high-water mark if the new active count is a new maximum
            activeCount.incrementAndGet();
            maxObserved.updateAndGet(maxObs -> Math.max(maxObs, activeCount.get()));

            // Simulate printing time
            Thread.sleep(50);

            // Record that this job has finished
            completedJobs.incrementAndGet();

        } finally {
            // Signal that one more printer is free again — do this even
            //       if an exception was thrown, and update the active count
            activeCount.decrementAndGet();
            semaphore.release();

        }
    }

    /** Returns the number of currently active print jobs. */
    public int getActiveCount() { return activeCount.get(); }

    /** Returns the peak number of simultaneous print jobs observed. */
    public int getMaxObservedConcurrency() { return maxObserved.get(); }

    /** Returns the total number of jobs that have completed. */
    public int getCompletedJobs() { return completedJobs.get(); }

    public int getPrinterCount() { return printerCount; }
}
