package com.bodhi.superagent;

import com.mashape.unirest.request.HttpRequest;

public class BearerCredentials implements Credentials
{
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private String token;

    public BearerCredentials(String token)
    {
        this.token = token;
    }

    @Override
    public HttpRequest setAuthentication(HttpRequest request)
    {
        return request.header(AUTHORIZATION_HEADER, "Bearer " + token);
    }
}
