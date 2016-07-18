package com.bodhi.superagent.bulk;

import org.json.JSONObject;

public class BulkConfig extends JSONObject {

    public BulkConfig(ConfigOp op, boolean report, String target) {
        put("op", op.getOp());
        put("report", report);
        put("target", target);
    }

    public BulkConfig(ConfigOp op, String target) {
        this(op, false, target);
    }

}
