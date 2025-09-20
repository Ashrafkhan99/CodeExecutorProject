package com.coderank.executor.execute;

public class SystemBusyException extends RuntimeException {
    private final String reason; // submit or permits
    private final Integer queueSize;
    private final Integer maxConcurrent;

    public SystemBusyException(String reason, Integer queueSize, Integer maxConcurrent) {
        super("System busy: " + reason);
        this.reason = reason;
        this.queueSize = queueSize;
        this.maxConcurrent = maxConcurrent;
    }

    public String getReason() { return reason; }
    public Integer getQueueSize() { return queueSize; }
    public Integer getMaxConcurrent() { return maxConcurrent; }
}
