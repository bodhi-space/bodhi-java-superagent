package com.bodhi.superagent.bulk;

import com.bodhi.superagent.Result;
import com.bodhi.superagent.ResultHandler;
import com.bodhi.superagent.backoff.BackoffCallback;
import com.bodhi.superagent.backoff.BackoffConfig;
import com.bodhi.superagent.backoff.BackoffResultHandler;
import com.mashape.unirest.http.HttpResponse;

public class BulkBackoffCallback<T> extends BackoffCallback<T> {

    private static final String BULK_ID_HEADER = "bulk_id";

    public BulkBackoffCallback(BackoffConfig backoffConfig, ResultHandler<T> resultHandler, BackoffResultHandler<T> backoffHandler) {
        super(backoffConfig, resultHandler, backoffHandler);
    }

    @Override
    protected Result<T> getResult(HttpResponse<T> httpResponse) {
        Result<T> result;
        switch (httpResponse.getStatus()) {
            case 200:
                result = new Result<>(httpResponse.getStatus(), null, httpResponse.getBody());
                break;
            case 202:
                String bulkId = httpResponse.getHeaders().getFirst(BULK_ID_HEADER);
                result = new Result<>(httpResponse.getStatus(), bulkId, null);
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
