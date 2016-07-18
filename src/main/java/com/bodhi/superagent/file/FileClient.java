package com.bodhi.superagent.file;


import com.bodhi.superagent.ClientConfig;
import com.bodhi.superagent.Credentials;
import com.bodhi.superagent.ResultHandler;
import com.bodhi.superagent.backoff.BackoffCallback;
import com.bodhi.superagent.backoff.BackoffResultHandler;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class FileClient {

    public static final String UPLOAD_FILE_NAME = "uploadFile";
    private final ClientConfig clientConfig;
    private Credentials credentials;

    public FileClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.credentials = clientConfig.getCredentials();
    }

    public void upload(String uploadPath, ContentType contentType, byte[] body, ResultHandler<JsonNode> handler) {
        fileRequest(handler, (callback) -> {
            String fullUri = getUploadUri(uploadPath);
            String fileName = uploadPath.substring(uploadPath.lastIndexOf('/') + 1);
            HttpRequestWithBody request = Unirest.put(fullUri);
            credentials.setAuthentication(request);
            request.field(UPLOAD_FILE_NAME, new ByteArrayInputStream(body), ContentType.create(contentType.getMimeType()), fileName)
                    .asJsonAsync(callback);

        });

    }


    public void upload(String uploadPath, File file, ResultHandler<JsonNode> handler) {
        fileRequest(handler, (callback) -> {
            String fullUri = getUploadUri(uploadPath);
            HttpRequestWithBody request = Unirest.put(fullUri);
            credentials.setAuthentication(request);
            request.field(UPLOAD_FILE_NAME, file)
                    .asJsonAsync(callback);

        });
    }

    public void download(String downloadPath, ResultHandler<InputStream> handler) {
        fileRequest(handler, (callback) -> {
            String fullUri = clientConfig.getUri() + "/" + clientConfig.getNamespace() + "/controllers/vertx/download/" + downloadPath;
            HttpRequest request = Unirest.get(fullUri);
            credentials.setAuthentication(request);
            request.asBinaryAsync(callback);
        });
    }

    public void delete(String deletePath, ResultHandler<JsonNode> handler) {
        fileRequest(handler, (callback) -> {
            String fullUri = getUploadUri(deletePath);
            HttpRequestWithBody request = Unirest.delete(fullUri);
            credentials.setAuthentication(request);
            request.asJsonAsync(callback);
        });
    }

    private String getUploadUri(String uploadPath) {
        return clientConfig.getUri() + "/" + clientConfig.getNamespace() + "/controllers/vertx/upload/" + uploadPath;
    }

    private <T> void fileRequest(ResultHandler<T> handler, BackoffResultHandler<T> resultHandler) {
        BackoffCallback<T> backoffCallback = new BackoffCallback<>(clientConfig.getBackoffConfig(), handler, resultHandler);
        resultHandler.handle(backoffCallback);
    }


}
