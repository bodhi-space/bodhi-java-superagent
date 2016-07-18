package com.bodhi.superagent.backoff;

import com.bodhi.superagent.Result;
import com.bodhi.superagent.ResultHandler;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;

public class BackoffCallback<T> implements Callback<T> {

    private static final String LOCATION_HEADER = "Location";
    private final ResultHandler<T> resultHandler;
    private final BackoffResultHandler<T> backoffHandler;

    private static final Log log = LogFactory.getLog(BackoffCallback.class);
    private final BackoffConfig backoffConfig;
    private int retry = 1;

    public BackoffCallback(BackoffConfig backoffConfig, ResultHandler<T> resultHandler, BackoffResultHandler<T> backoffHandler) {
        this.backoffConfig = backoffConfig;
        this.resultHandler = resultHandler;
        this.backoffHandler = backoffHandler;
    }

    private static final int BACKOFF_STATUS_CODE = 429;

    public void completed(HttpResponse<T> httpResponse) {
        if (httpResponse.getStatus() == BACKOFF_STATUS_CODE && retry <= backoffConfig.getRetries()) {
            try {
                Thread.sleep(backoffConfig.getWaitMillis());
                log.info("Retrying in " + backoffConfig.getWaitMillis() + " milliseconds ..., retry " + retry + " of " + backoffConfig.getRetries());
                retry++;
                backoffHandler.handle(this);
            } catch (InterruptedException e) {
                resultHandler.handle(new Result<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null));
            }
        } else {
            Result<T> result = getResult(httpResponse);
            resultHandler.handle(result);
        }
    }

    public void failed(UnirestException e) {
        resultHandler.handle(new Result<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), null));
    }

    public void cancelled() {
        resultHandler.handle(new Result<>(HttpStatus.SC_NOT_MODIFIED, "Request cancelled", null));
    }

    protected Result<T> getResult(HttpResponse<T> httpResponse) {
        Result<T> result;
        switch (httpResponse.getStatus()) {
            case 200:
            case 202:
                result = new Result<>(httpResponse.getStatus(), null, httpResponse.getBody());
                break;
            case 201:
                result = new Result<>(httpResponse.getStatus(), httpResponse.getHeaders().getFirst(LOCATION_HEADER), null);
                break;
            case 204:
                result = new Result<>(httpResponse.getStatus(), null, null);
                break;
            default:
                result = new Result<>(httpResponse.getStatus(), null, httpResponse.getBody());
        }
        return result;
    }

}
