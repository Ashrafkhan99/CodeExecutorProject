package com.coderank.executor.execute;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.exec.concurrent")
public class ConcurrentExecProperties {
    private int maxConcurrent = 6;       // pool size & semaphore permits
    private int queueCapacity = 20;      // pool queue size
    private int submitTimeoutMs = 150;   // max time to enqueue before rejecting
    private int permitTimeoutMs = 200;   // max time to get semaphore before rejecting
    private int perUserMaxInFlight = 2;  // fairness

    public int getMaxConcurrent() { return maxConcurrent; }
    public void setMaxConcurrent(int maxConcurrent) { this.maxConcurrent = maxConcurrent; }
    public int getQueueCapacity() { return queueCapacity; }
    public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }
    public int getSubmitTimeoutMs() { return submitTimeoutMs; }
    public void setSubmitTimeoutMs(int submitTimeoutMs) { this.submitTimeoutMs = submitTimeoutMs; }
    public int getPermitTimeoutMs() { return permitTimeoutMs; }
    public void setPermitTimeoutMs(int permitTimeoutMs) { this.permitTimeoutMs = permitTimeoutMs; }
    public int getPerUserMaxInFlight() { return perUserMaxInFlight; }
    public void setPerUserMaxInFlight(int perUserMaxInFlight) { this.perUserMaxInFlight = perUserMaxInFlight; }
}
