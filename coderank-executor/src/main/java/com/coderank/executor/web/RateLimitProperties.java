package com.coderank.executor.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ratelimit")
public class RateLimitProperties {

    private final Execute execute = new Execute();

    public Execute getExecute() { return execute; }

    public static class Execute {
        /** Allowed requests per minute to POST /api/execute (per user or IP). */
        private int perMinute = 30;
        public int getPerMinute() { return perMinute; }
        public void setPerMinute(int perMinute) { this.perMinute = perMinute; }
    }
}
