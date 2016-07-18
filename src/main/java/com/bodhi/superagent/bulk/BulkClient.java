package com.bodhi.superagent.bulk;

import com.bodhi.superagent.Client;
import com.bodhi.superagent.ClientConfig;
import com.bodhi.superagent.Credentials;
import com.bodhi.superagent.ResultHandler;
import com.bodhi.superagent.backoff.BackoffResultHandler;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

public class BulkClient {


    private final Credentials credentials;
    private final ClientConfig clientConfig;

    public BulkClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.credentials = clientConfig.getCredentials();
    }

    public void post(Bulk bulk, ResultHandler<JsonNode> handler) {
        bulkRequest(handler, (callback) -> {
            String bulkUrl = clientConfig.getNamespaceUri() + "/bulk";
            HttpRequestWithBody request = Unirest.post(bulkUrl);
            credentials.setAuthentication(request);
            Client.addJsonHeaders(request);
            request.body(bulk).asJsonAsync(callback);
        });

    }

    public void get(String id, ResultHandler<JsonNode> handler) {
        bulkRequest(handler, (callback) -> {
            String bulkUrl = clientConfig.getNamespaceUri() + "/bulk/" + id;
            GetRequest request = Unirest.get(bulkUrl);
            credentials.setAuthentication(request);
            request.asJsonAsync(callback);
        });

    }

    private <T> void bulkRequest(ResultHandler<T> handler, BackoffResultHandler<T> resultHandler) {
        BulkBackoffCallback<T> backoffCallback = new BulkBackoffCallback<>(clientConfig.getBackoffConfig(), handler, resultHandler);
        resultHandler.handle(backoffCallback);
    }

}
