package com.coderank.executor.execute;

public class TooManyInFlightException extends RuntimeException {
    private final String userId;
    private final int limit;

    public TooManyInFlightException(String userId, int limit) {
        super("Too many in-flight executions" + (userId != null ? " for user " + userId : ""));
        this.userId = userId;
        this.limit = limit;
    }

    public String getUserId() { return userId; }
    public int getLimit() { return limit; }
}
