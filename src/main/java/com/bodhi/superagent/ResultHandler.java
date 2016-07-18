package com.bodhi.superagent;

@FunctionalInterface
public interface ResultHandler<T> {
    void handle(Result<T> result);
}
