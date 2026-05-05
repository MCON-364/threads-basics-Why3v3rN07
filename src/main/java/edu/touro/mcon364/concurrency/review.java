package edu.touro.mcon364.concurrency;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class review {

    //runnable with lambda

    void usingRunnable() throws InterruptedException {
        Runnable task = () -> System.out.println(Thread.currentThread().getName());
        Thread t1 = new Thread(task, "worker-1");
        t1.start();

        Thread t2 = new Thread(() -> System.out.println(Thread.currentThread().getName()), "worker-2");
        t2.start();

        t1.join();
        t2.join();

        // start, join, isAlive, getName, setName, getId, sleep, interrupt, getState, getPriority, setPriority, isDaemon, setDaemon

    }

    //daemons

    void daemons() throws InterruptedException {
        Thread daemonThread1 = new Thread(() -> System.out.println(Thread.currentThread().getName() + " running"), "daemon-thread");
        daemonThread1.setDaemon(true);
        daemonThread1.start();

        Thread daemonThread2 = new Thread(() -> System.out.print(Thread.currentThread().getName() + " running"), "daemon-thread-2");
        daemonThread2.setDaemon(true);
        daemonThread2.start();

        System.out.println(daemonThread1.isAlive());
        System.out.println(daemonThread2.isAlive());

        daemonThread1.join();

        System.out.println("Main thread is exiting, all daemons will be terminated.");
    }

    //synchronized - locks the whole object/method

    private int num;

    public synchronized void increment() {
        num++;
    }

    public synchronized int getCount() {
        return num;
    }

    //synchronized collections

    private List<Integer> nums = Collections.synchronizedList(new ArrayList<>());


    // Atomics - for a single shared var
    // AtomicInteger, AtomicLong, AtomicReference<T>
    AtomicInteger count = new AtomicInteger(0);
    count.incrementAndGet();
    count.decrementAndGet();
    count.addAndGet(5);
    int value = count.get();
    count.set(42);
    boolean changed = count.compareAndSet(42, 43);

    //reentrantlock - explicit control
    private final Lock lock = new ReentrantLock();
    private int val = 0;

    public void incrementval() {
        lock.lock();
        try {
            val++;
        } finally {
            lock.unlock();
        }
    }
    // tryLock(), tryLock(timeout, unit), lockInterruptibly()

    //readwritelock - separate locks for readers and writers
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private Map<String, String> cache = new HashMap<>();

    public String get(String key) {
        readLock.lock();
        try {
            return cache.get(key);   // many threads at once
        } finally {
            readLock.unlock();
        }
    }

    public void put(String key, String value) {
        writeLock.lock();
        try {
            cache.put(key, value);   // exclusive access
        } finally {
            writeLock.unlock();
        }
    }


    // synchronizers - CountDownLatch, Semaphore, CyclicBarrier, Phaser

    //CountDownLatch — wait until a count reaches zero.
    CountDownLatch latch = new CountDownLatch(3);

    // worker threads call:
    latch.countDown();

    // coordinator thread calls:
    latch.await();

    //Semaphore — allow only a limited number of threads through.
    Semaphore slots = new Semaphore(2);

    slots.acquire();
    try

        {
            useSharedPrinter();
        } finally

        {
            slots.release();
        }

    //CyclicBarrier — make a fixed group wait until everyone arrives.
    CyclicBarrier barrier = new CyclicBarrier(3);

    // each worker does part 1
    barrier.await();
    // now all three begin part 2

    //Phaser — like a more flexible, multi-phase barrier.
    Phaser phaser = new Phaser(3);

    phaser.arriveAndAwaitAdvance();
    // phase 0 finished, move to phase 1
    phaser.arriveAndAwaitAdvance();
    // phase 1 finished, move to phase 2

    // executors - ExecutorService, Executors.newFixedThreadPool, submit(), shutdown(), awaitTermination()
    ExecutorService pool = Executors.newFixedThreadPool(4);
    for(Task task : tasks) {
        pool.submit(() -> process(task));
    }
    pool.shutdown();

    //callables and futures - Callable<T>, Future<T>, submit(Callable), future.get(), future.isDone(), future.cancel()
    ExecutorService pooly = Executors.newFixedThreadPool(2);
    Future<Integer> future = pooly.submit(() -> {
        return 21 + 21;
    });
    Integer answer = future.get(); //blocks, call at end



    /// design patterns - producer-consumer, worker pool, pipeline

    // producer-consumer:
    // one or more producers generate work items and put them in a shared queue;
    // one or more consumers take items from the queue and process them.
    // Use a thread-safe queue (e.g., LinkedBlockingQueue) to coordinate between producers and consumers.

    BlockingQueue<Task> queue = new LinkedBlockingQueue<>();

    Runnable producer = () -> {
        queue.put(new Task("email report"));
    };

    Runnable consumer = () -> {
        Task task = queue.take();
        process(task);
    };

    //worker pool:
    // a fixed number of worker threads take tasks from a shared queue and execute them.
    // Use an ExecutorService with a fixed thread pool to manage the worker threads, and submit tasks to the pool for execution.
    // This limits concurrency and can improve performance by reusing threads.

    ExecutorService pool =
            Executors.newFixedThreadPool(4);

for (Task task : tasks) {
        pool.submit(() -> process(task));
    }

pool.shutdown();

    Future<Integer> f =
            pool.submit(() -> compute());

    Integer result = f.get();

    //pipeline:
    // a series of processing stages, where each stage is handled by a separate thread or thread pool.
    // Each stage takes input from the previous stage, processes it, and passes it to the next stage.
    // Use BlockingQueues to connect the stages and allow for asynchronous processing between them.
    // Work moves through stages: read, clean, transform, save. Different stages can run concurrently if handoff is safe.

    BlockingQueue raw = new LinkedBlockingQueue<>();
    BlockingQueue valid = new LinkedBlockingQueue<>();

// Stage 1: Reader
new Thread(() -> {
        for (Row r : readRows()) {
            raw.put(r);
        }
    }).start();

// Stage 2: Validator
new Thread(() -> {
        while (true) {
            Row r = raw.take();
            if (isValid(r)) valid.put(r);
        }
    }).start();

// Stage 3: Saver
new Thread(() -> {
        while (true) {
            Row r = valid.take();
            save(r);
        }
    }).start();
}




