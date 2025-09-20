package com.coderank.executor.execute;

public class ExecuteResponse {
    private final String stdout;
    private final String stderr;
    private final ExecStatus status;
    private final int execTimeMs;

    public ExecuteResponse(String stdout, String stderr, ExecStatus status, int execTimeMs) {
        this.stdout = stdout;
        this.stderr = stderr;
        this.status = status;
        this.execTimeMs = execTimeMs;
    }

    public String getStdout() { return stdout; }
    public String getStderr() { return stderr; }
    public ExecStatus getStatus() { return status; }
    public int getExecTimeMs() { return execTimeMs; }
}
