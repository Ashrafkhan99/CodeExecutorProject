package com.coderank.executor.execute;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ConcurrencyConfig {

    @Bean
    public ThreadPoolExecutor execPool(ConcurrentExecProperties props) {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(props.getQueueCapacity());
        ThreadFactory tf = new ThreadFactory() {
            private final AtomicInteger c = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("exec-worker-" + c.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        };
        ThreadPoolExecutor ex = new ThreadPoolExecutor(
                props.getMaxConcurrent(),
                props.getMaxConcurrent(),
                0L,
                TimeUnit.MILLISECONDS,
                queue,
                tf,
                new ThreadPoolExecutor.AbortPolicy()
        );
        ex.prestartAllCoreThreads();
        return ex;
    }

    @Bean
    public Semaphore execPermits(ConcurrentExecProperties props) {
        return new Semaphore(props.getMaxConcurrent());
    }
}
