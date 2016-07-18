package com.bodhi.superagent;

public class Result<T> {
    private int statusCode;
    private String strValue;
    private T data;

    public Result(int statusCode, String strValue, T data) {
        this.statusCode = statusCode;
        this.strValue = strValue;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getString() {
        return strValue;
    }

    public T getData() {
        return data;
    }
}
