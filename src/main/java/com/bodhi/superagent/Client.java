package com.bodhi.superagent;

import com.bodhi.superagent.backoff.BackoffCallback;
import com.bodhi.superagent.backoff.BackoffConfig;
import com.bodhi.superagent.backoff.BackoffResultHandler;
import com.bodhi.superagent.bulk.Bulk;
import com.bodhi.superagent.bulk.BulkClient;
import com.bodhi.superagent.file.FileClient;
import com.bodhi.superagent.patch.Patch;
import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Client
{
    private static final long DEFAULT_TIMEOUT = 30000;
    private static final int DEFAULT_LIMIT = Integer.MAX_VALUE;

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json";
    private static final String ACCEPT_HEADER = "Accept";

    private final FileClient fileClientDelegate;
    private final BulkClient bulkClientDelegate;
    private final Credentials credentials;

    private ClientConfig clientConfig;

    public Client(String uri, String namespace, Credentials credentials, long timeout)
    {
        this.clientConfig = new ClientConfig(uri, namespace, credentials, new BackoffConfig());
        this.credentials = clientConfig.getCredentials();
        this.fileClientDelegate = new FileClient(clientConfig);
        this.bulkClientDelegate = new BulkClient(clientConfig);
        Unirest.setTimeouts(timeout, timeout);
    }

    public Client(String uri, String namespace, Credentials credentials)
    {
        this(uri, namespace, credentials, DEFAULT_TIMEOUT);
    }

    public Client(Environment env, String namespace, Credentials credentials)
    {
        this(env.getUrl(), namespace, credentials, DEFAULT_TIMEOUT);
    }

    public Client(Environment env, String namespace, Credentials credentials, long timeout)
    {
        this(env.getUrl(), namespace, credentials, timeout);
    }

    //GET METHODS
    //
    public void get(String url, JSONObject query, ResultHandler<JsonNode> handler)
    {
        request(handler, (callback) -> {
            String fullUrl = getUrl(url, query);
            HttpRequest request = Unirest.get(fullUrl);
            credentials.setAuthentication(request);
            request.asStringAsync(callback);
        });
    }


    public void getAll(String url, JSONObject query, ResultHandler<JsonNode> handler)
    {
        getPages(new JSONArray(), url, query, 1, handler);
    }

    public void getPage(String url, JSONObject query, int page, int limit, ResultHandler<JsonNode> handler)
    {
        String pagingParam = "?paging=limit:" + limit + ",page:" + page;
        get(url + pagingParam, query, handler);

    }

    //POST METHODS
    public void post(String url, JSONObject body, ResultHandler<JsonNode> handler)
    {
        request(handler, (callback) -> {
            HttpRequestWithBody request = createPayloadRequest(HttpMethod.POST, url);
            request.body(body).asStringAsync(callback);
        });
    }

    public void post(String url, JSONArray body, ResultHandler<JsonNode> handler)
    {
        request(handler, (callback) -> {
            HttpRequestWithBody request = createPayloadRequest(HttpMethod.POST, url);
            request.body(body).asStringAsync(callback);
        });
    }

    //PUT METHOD
    public void put(String url, JSONObject body, ResultHandler<JsonNode> handler)
    {
        request(handler, (callback) -> {
            HttpRequestWithBody request = createPayloadRequest(HttpMethod.PUT, url);
            request.body(body).asStringAsync(callback);
        });
    }

    //PATCH METHODS
    public void patch(String url, Patch[] patches, ResultHandler<JsonNode> handler)
    {
        request(handler, (callback) -> {
            HttpRequestWithBody request = createPayloadRequest(HttpMethod.PATCH, url);
            JSONArray patchArray = new JSONArray();
            for (Patch patch : patches)
            {
                patchArray.put(patch);
            }
            request.body(patchArray).asStringAsync(callback);
        });

    }

    public void patch(String url, Patch patch, ResultHandler<JsonNode> handler)
    {
        patch(url, new Patch[]{patch}, handler);
    }

    //DELETE METHODS
    public void delete(String url, JSONObject query, ResultHandler<JsonNode> handler)
    {
        request(handler, (callback) -> {
            String fullUrl = getUrl(url, query);
            HttpRequest request = Unirest.delete(fullUrl);
            credentials.setAuthentication(request);
            request.asStringAsync(callback);
        });

    }

    //FILE METHODS
    public void uploadFile(String uploadPath, ContentType contentType, String bucket, byte[] body, ResultHandler<JsonNode> handler)
    {
        fileClientDelegate.upload(uploadPath, contentType, bucket, body, handler);
    }

    public void uploadFile(String uploadPath, String bucket, File file, ResultHandler<JsonNode> handler)
    {
        fileClientDelegate.upload(uploadPath, bucket, file, handler);
    }

    public void downloadFile(String downloadPath, String bucket, ResultHandler<InputStream> handler)
    {
        fileClientDelegate.download(downloadPath, bucket, handler);
    }


    public void deleteFile(String deletePath, String bucket, ResultHandler<JsonNode> handler)
    {
        fileClientDelegate.delete(deletePath, bucket, handler);
    }


    //BULK METHODS
    public void postBulk(Bulk bulk, ResultHandler<JsonNode> handler)
    {
        bulkClientDelegate.post(bulk, handler);
    }

    public void getBulk(String id, ResultHandler<JsonNode> handler)
    {
        bulkClientDelegate.get(id, handler);
    }

    // PRIVATE METHODS

    private void request(ResultHandler<JsonNode> handler, BackoffResultHandler<String> resultHandler)
    {
        //wrapping json request to avoid bug with non-json response (when no callback is being called due to json parse exception)
        ResultHandler<String> stringHandler = (stringResult) -> {
            String data = stringResult.getData();
            Result<JsonNode> result;
            try
            {
                JsonNode jsonNode = data != null ? new JsonNode(data) : null;
                result = new Result<>(stringResult.getStatusCode(), stringResult.getString(), jsonNode);
            }
            catch (Exception ex)
            {
                result = new Result<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage(), null);
            }
            handler.handle(result);
        };

        BackoffCallback<String> backoffCallback = new BackoffCallback<>(clientConfig.getBackoffConfig(), stringHandler, resultHandler);
        resultHandler.handle(backoffCallback);
    }

    private HttpRequestWithBody createPayloadRequest(HttpMethod method, String url)
    {
        String fullUrl = getUrl(url, null);
        HttpRequestWithBody request = new HttpRequestWithBody(method, fullUrl);
        addJsonHeaders(request);
        credentials.setAuthentication(request);
        return request;
    }

    public static HttpRequestWithBody addJsonHeaders(HttpRequestWithBody request)
    {
        return request.header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE).header(ACCEPT_HEADER, CONTENT_TYPE_HEADER_VALUE);
    }

    private String getUrl(String url, JSONObject query)
    {
        StringBuilder result = new StringBuilder();
        if (url.startsWith("/"))
        {
            result.append(clientConfig.getUri()).append(url);
        }
        else
        {
            result.append(clientConfig.getNamespaceUri()).append('/').append(url);
        }
        if (query != null)
        {
            result.append(result.toString().contains("paging=") ? "&where=" : "?where=");
            try
            {
                result.append(URLEncoder.encode(query.toString(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

    private void getPages(JSONArray result, String url, JSONObject query, int page, ResultHandler<JsonNode> handler)
    {
        getPage(url, query, page, DEFAULT_LIMIT, pageResult -> {
            if (pageResult.getStatusCode() == HttpStatus.SC_OK)
            {
                JSONArray array = pageResult.getData().getArray();
                if (array != null && array.length() > 0)
                {
                    for (int i = 0; i < array.length(); i++)
                    {
                        result.put(array.getJSONObject(i));
                    }
                    int nextPage = page + 1;
                    getPages(result, url, query, nextPage, handler);
                }
                else
                {
                    handler.handle(new Result<>(HttpStatus.SC_OK, null, new JsonNode(result.toString())));
                }
            }
            else
            {
                handler.handle(new Result<>(pageResult.getStatusCode(), pageResult.getString(), pageResult.getData()));
            }
        });
    }
}
