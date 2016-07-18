package com.bodhi.superagent;

import com.mashape.unirest.request.HttpRequest;

public class BasicCredentials implements Credentials{

    private String username;
    private String password;

    public BasicCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public HttpRequest setAuthentication(HttpRequest request) {
        return request.basicAuth(username, password);
    }
}
