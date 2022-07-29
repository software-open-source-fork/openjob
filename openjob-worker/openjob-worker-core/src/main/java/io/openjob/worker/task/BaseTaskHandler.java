package io.openjob.worker.task;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author stelin <swoft@qq.com>
 * @since 1.0.0
 */
public abstract class BaseTaskHandler<T> {
    protected final Long id;
    protected final Integer handlerCoreThreadNum;
    protected final Integer handlerMaxThreadNum;
    protected final String handlerThreadName;
    protected ThreadPoolExecutor handlerExecutor;
    protected Integer pollSize;
    protected String pollThreadName;
    protected Thread pollThread;
    protected TaskQueue<T> queues;
    protected AtomicInteger activePollNum = new AtomicInteger(0);

    public BaseTaskHandler(Long id,
                           Integer handlerCoreThreadNum,
                           Integer handlerMaxThreadNum,
                           String handlerThreadName,
                           Integer pollSize,
                           String pollThreadName,
                           TaskQueue<T> queues) {
        this.id = id;
        this.handlerCoreThreadNum = handlerCoreThreadNum;
        this.handlerMaxThreadNum = handlerMaxThreadNum;
        this.handlerThreadName = handlerThreadName;
        this.pollSize = pollSize;
        this.pollThreadName = pollThreadName;
        this.queues = queues;
    }

    public void start() {
        // Handler thread executor.
        handlerExecutor = new ThreadPoolExecutor(
                this.handlerCoreThreadNum,
                this.handlerMaxThreadNum,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10240),
                new ThreadFactory() {
                    private final AtomicInteger index = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, String.format("%s-%d-%d", handlerThreadName, id, index.getAndIncrement()));
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        pollThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    List<T> tasks = this.pollTasks();
                    if (tasks.size() < this.pollSize) {
                        if (tasks.isEmpty()) {
                            Thread.sleep(1000L);
                            continue;
                        }
                        Thread.sleep(500L);
                    }
                }
            } catch (Throwable ex) {
                System.out.println(ex.getMessage());
            }
        }, this.pollThreadName + id);
        pollThread.start();
    }

    public abstract void handle(Long id, List<T> tasks);

    public void stop() {
        // Poll thread
        if (Objects.nonNull(pollThread)) {
            pollThread.interrupt();
        }

        // Handle thread pool
        if (Objects.nonNull(handlerExecutor)) {
            handlerExecutor.shutdownNow();
        }

        if (Objects.nonNull(this.queues)) {
            this.queues.clear();
        }
    }

    private synchronized List<T> pollTasks() {
        List<T> tasks = queues.poll(this.pollSize);
        if (!tasks.isEmpty()) {
            this.activePollNum.incrementAndGet();
            this.handle(id, tasks);
        }
        return tasks;
    }
}
