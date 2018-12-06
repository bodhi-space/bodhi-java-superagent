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

public class FileClient
{

    private static final String UPLOAD_FILE_NAME = "uploadFile";
    private final ClientConfig clientConfig;
    private Credentials credentials;

    private static final String NAMESPACE_HEADER = "namespace";

    private static final String FILES_SERVICE_PATH = "%s/files/%s/%s";


    public FileClient(ClientConfig clientConfig)
    {
        this.clientConfig = clientConfig;
        this.credentials = clientConfig.getCredentials();
    }

    public void upload(String uploadPath, ContentType contentType, String bucket, byte[] body, ResultHandler<JsonNode> handler)
    {
        fileRequest(handler, (callback) -> {
            String fullUri = getFilePath(uploadPath, bucket);
            String fileName = uploadPath.substring(uploadPath.lastIndexOf('/') + 1);
            HttpRequestWithBody request = Unirest.put(fullUri);
            request.header(NAMESPACE_HEADER, clientConfig.getNamespace());
            credentials.setAuthentication(request);
            request.field(UPLOAD_FILE_NAME, new ByteArrayInputStream(body), ContentType.create(contentType.getMimeType()), fileName).asJsonAsync(callback);

        });

    }


    public void upload(String uploadPath, String bucket, File file, ResultHandler<JsonNode> handler)
    {
        fileRequest(handler, (callback) -> {
            String fullUri = getFilePath(uploadPath, bucket);
            HttpRequestWithBody request = Unirest.put(fullUri);
            request.header(NAMESPACE_HEADER, clientConfig.getNamespace());
            credentials.setAuthentication(request);
            request.field(UPLOAD_FILE_NAME, file).asJsonAsync(callback);

        });
    }

    public void download(String downloadPath, String bucket, ResultHandler<InputStream> handler)
    {
        fileRequest(handler, (callback) -> {
            String fullUri = getFilePath(downloadPath, bucket);
            HttpRequest request = Unirest.get(fullUri);
            request.header(NAMESPACE_HEADER, clientConfig.getNamespace());
            credentials.setAuthentication(request);
            request.asBinaryAsync(callback);
        });
    }

    public void delete(String deletePath, String bucket, ResultHandler<JsonNode> handler)
    {
        fileRequest(handler, (callback) -> {
            String fullUri = getFilePath(deletePath, bucket);
            HttpRequestWithBody request = Unirest.delete(fullUri);
            request.header(NAMESPACE_HEADER, clientConfig.getNamespace());
            credentials.setAuthentication(request);
            request.asJsonAsync(callback);
        });
    }

    private <T> void fileRequest(ResultHandler<T> handler, BackoffResultHandler<T> resultHandler)
    {
        BackoffCallback<T> backoffCallback = new BackoffCallback<>(clientConfig.getBackoffConfig(), handler, resultHandler);
        resultHandler.handle(backoffCallback);
    }

    private String getFilePath(String path, String bucket)
    {
        return String.format(FILES_SERVICE_PATH, clientConfig.getFilesUri(), path, bucket);
    }


}
