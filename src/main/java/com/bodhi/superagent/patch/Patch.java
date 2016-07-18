package com.bodhi.superagent.patch;

import org.json.JSONObject;

public class Patch extends JSONObject {

    public Patch(PatchOperation op, String path, Object value) {
        this.put("op", op.getOp());
        this.put("path", path);
        this.put("value", value);
    }
}
