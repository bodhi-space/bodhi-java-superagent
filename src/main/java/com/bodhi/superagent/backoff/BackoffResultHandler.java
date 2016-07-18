package com.bodhi.superagent.backoff;

@FunctionalInterface
public interface BackoffResultHandler<T> {
    void handle(BackoffCallback<T> backoffCallback);
}
