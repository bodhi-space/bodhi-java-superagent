package com.bodhi.superagent;

public enum Environment {
    DEV("https://api.bodhi-dev.io"),
    TEST("https://api.bodhi-qa.io"),
    PROD("https://api.bodhi.space");

    private String url;

    Environment(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
