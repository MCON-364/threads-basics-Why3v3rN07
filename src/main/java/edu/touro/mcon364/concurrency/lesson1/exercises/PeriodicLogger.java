package edu.touro.mcon364.concurrency.lesson1.exercises;

import static java.lang.Thread.sleep;

/**
 * Exercise 3: sleep(), isAlive(), and the daemon flag
 *
 * A PeriodicLogger should log a message on a background thread at a fixed
 * interval for a fixed number of ticks, then stop.
 *
 * Requirements:
 * - start(): create a background thread that sleeps for intervalMs milliseconds,
 *   then appends one line to the log, and repeats this exactly 'ticks' times total.
 *   The thread must be marked as a DAEMON thread before it is started.
 *   The thread must be given the name "periodic-logger".
 *   start() must return BEFORE the background thread has finished (non-blocking).
 *
 * - isRunning(): return true if the background thread exists and is still alive,
 *   false otherwise.
 *
 * - awaitCompletion(): block the calling thread until the background thread finishes.
 *
 * - getLog(): return the messages appended so far (one per tick).
 *
 * Hint: Thread.sleep(intervalMs) pauses the current thread.
 *       thread.isAlive() tells you whether a thread is still running.
 *       setDaemon(true) must be called BEFORE start().
 */
public class PeriodicLogger {

    private final int ticks;
    private final long intervalMs;
    private final java.util.List<String> log = new java.util.ArrayList<>();
    private Thread worker;

    public PeriodicLogger(int ticks, long intervalMs) {
        if (ticks <= 0) throw new IllegalArgumentException("ticks must be positive");
        if (intervalMs < 0) throw new IllegalArgumentException("intervalMs must be non-negative");
        this.ticks = ticks;
        this.intervalMs = intervalMs;
    }

    /**
     * Create, configure, and start the background thread.
     * Must return before the thread finishes (i.e. do NOT join here).
     */
    public void start() {
        // TO-DO: create a daemon thread named "periodic-logger" that
        //       sleeps for intervalMs then appends "tick N" (1-based) to log,
        //       repeating 'ticks' times total, then starts it.
        Thread logger = new Thread(
                () -> {
                    for (int i = 1; i <= ticks; i++) {
                        try {
                            sleep(intervalMs);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        log.add("tick " + i);
                    }
                }, "periodic-logger"
        );
        logger.setDaemon(true);
        this.worker = logger;
        logger.start();
    }

    /**
     * Returns true while the background thread is still running.
     */
    public boolean isRunning() {
        // TO-DO: return whether the worker thread is alive
        return worker.isAlive();
    }

    /**
     * Blocks until the background thread finishes.
     */
    public void awaitCompletion() throws InterruptedException {
        // TO-DO: join the worker thread
        worker.join();
    }

    /** Returns the log messages collected so far. */
    public java.util.List<String> getLog() {
        return java.util.Collections.unmodifiableList(log);
    }
}

