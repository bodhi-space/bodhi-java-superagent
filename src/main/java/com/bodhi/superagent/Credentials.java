package com.bodhi.superagent;

import com.mashape.unirest.request.HttpRequest;

public interface Credentials {
    HttpRequest setAuthentication(HttpRequest request);
}
