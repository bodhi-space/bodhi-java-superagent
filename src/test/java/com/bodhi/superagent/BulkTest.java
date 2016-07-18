package com.bodhi.superagent;

import com.bodhi.superagent.bulk.Bulk;
import com.bodhi.superagent.bulk.BulkConfig;
import com.bodhi.superagent.bulk.ConfigOp;
import com.mashape.unirest.http.JsonNode;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class BulkTest extends BaseClientTest {


    @Test
    public void bulkTest() throws InterruptedException {
        BulkConfig bulkConfig = new BulkConfig(ConfigOp.INSERT, true, "BulkTest");
        JSONArray payload = new JSONArray();
        payload.put(new JSONObject().put("name", "leo").put("other_field", "mauris non"));
        payload.put(new JSONObject().put("name", "orci").put("other_field", "sit amet justo"));
        payload.put(new JSONObject().put("name", "nulla").put("other_field", "pulvinar sed nisl nunc rhoncus"));

        Bulk bulk = new Bulk(bulkConfig, payload);

        Result<JsonNode>[] done = TestUtil.createResultArray(2);
        client.postBulk(bulk, result -> done[0] = result);
        wait.until(() -> done[0] != null);
        Assert.assertEquals(HttpStatus.SC_ACCEPTED, done[0].getStatusCode());
        String bulk_id = done[0].getString();
        Assert.assertNotNull(bulk_id);
        Assert.assertTrue(bulk_id.length() > 0);

        Thread.sleep(5000);

        client.getBulk(bulk_id, result -> done[1] = result);
        wait.until(() -> done[1] != null);
        Assert.assertEquals(HttpStatus.SC_OK, done[1].getStatusCode());
        Assert.assertEquals(3, done[1].getData().getArray().length());

    }

}
