package com.bodhi.superagent.backoff;

public class BackoffConfig {

    private long waitMillis = 1000;
    private int retries = 10;

    public BackoffConfig(int retries, long waitMillis) {
        this.retries = retries;
        this.waitMillis = waitMillis;
    }

    public BackoffConfig() {
    }

    public long getWaitMillis() {
        return waitMillis;
    }

    public int getRetries() {
        return retries;
    }
}
