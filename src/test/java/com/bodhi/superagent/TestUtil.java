package com.bodhi.superagent;

public class TestUtil {

    @SuppressWarnings("unchecked")
    static <T> Result<T>[] createResultArray(int size) {
        return new Result[size];
    }
}
