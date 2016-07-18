package com.bodhi.superagent.bulk;

import org.json.JSONArray;
import org.json.JSONObject;


public class Bulk extends JSONObject {

    public Bulk(BulkConfig bulkConfig, JSONArray payload) {
        this.put("config", bulkConfig);
        this.put("payload", payload);
    }
}
