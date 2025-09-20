package com.coderank.executor.execute;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.exec")
public class ExecProperties {
    private int timeoutSeconds = 5;
    private int overallTimeoutSeconds = 12;
    private String memory = "256m";
    private double cpus = 0.5;
    private int pidsLimit = 128;
    private int tmpfsSizeMb = 64;

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    public int getOverallTimeoutSeconds() { return overallTimeoutSeconds; }
    public void setOverallTimeoutSeconds(int overallTimeoutSeconds) { this.overallTimeoutSeconds = overallTimeoutSeconds; }
    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }
    public double getCpus() { return cpus; }
    public void setCpus(double cpus) { this.cpus = cpus; }
    public int getPidsLimit() { return pidsLimit; }
    public void setPidsLimit(int pidsLimit) { this.pidsLimit = pidsLimit; }
    public int getTmpfsSizeMb() { return tmpfsSizeMb; }
    public void setTmpfsSizeMb(int tmpfsSizeMb) { this.tmpfsSizeMb = tmpfsSizeMb; }
}
