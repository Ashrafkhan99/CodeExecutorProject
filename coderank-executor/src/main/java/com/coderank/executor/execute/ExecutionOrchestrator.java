package com.coderank.executor.execute;

import com.coderank.executor.language.ExecLanguage;
import com.coderank.executor.user.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExecutionOrchestrator {
    private final ThreadPoolExecutor pool;
    private final Semaphore permits;
    private final DockerRunner dockerRunner;
    private final ConcurrentExecProperties props;
    private final ConcurrentHashMap<String, AtomicInteger> inFlight = new ConcurrentHashMap<>();

    public ExecutionOrchestrator(ThreadPoolExecutor pool,
                                 Semaphore permits,
                                 DockerRunner dockerRunner,
                                 ConcurrentExecProperties props) {
        this.pool = pool;
        this.permits = permits;
        this.dockerRunner = dockerRunner;
        this.props = props;
    }

    public DockerRunner.Result submitAndWait(User user, ExecLanguage lang, String source, String stdin) {
        String key = (user != null && user.getId() != null) ? "u:" + user.getId() : "u:anon";
        AtomicInteger counter = inFlight.computeIfAbsent(key, k -> new AtomicInteger());
        if (counter.get() >= props.getPerUserMaxInFlight()) {
            throw new TooManyInFlightException(user != null ? String.valueOf(user.getId()) : null, props.getPerUserMaxInFlight());
        }
        counter.incrementAndGet();
        try {
            FutureTask<DockerRunner.Result> task = new FutureTask<>(() -> {
                if (!permits.tryAcquire(props.getPermitTimeoutMs(), TimeUnit.MILLISECONDS)) {
                    throw new SystemBusyException("permits", pool.getQueue().size(), props.getMaxConcurrent());
                }
                try {
                    return dockerRunner.run(lang, source, stdin);
                } finally {
                    permits.release();
                }
            });
            boolean queued;
            try {
                queued = pool.getQueue().offer(task, props.getSubmitTimeoutMs(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SystemBusyException("submit", pool.getQueue().size(), props.getMaxConcurrent());
            }
            if (!queued) {
                throw new SystemBusyException("submit", pool.getQueue().size(), props.getMaxConcurrent());
            }
            try {
                return task.get();
            } catch (ExecutionException ee) {
                Throwable cause = ee.getCause();
                if (cause instanceof RuntimeException re) throw re;
                if (cause instanceof Error err) throw err;
                throw new RuntimeException(cause);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new SystemBusyException("submit", pool.getQueue().size(), props.getMaxConcurrent());
            }
        } finally {
            counter.decrementAndGet();
        }
    }
}
