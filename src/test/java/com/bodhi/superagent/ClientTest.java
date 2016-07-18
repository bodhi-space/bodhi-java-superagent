package com.bodhi.superagent;

import com.bodhi.superagent.TestUtil;
import com.bodhi.superagent.patch.Patch;
import com.bodhi.superagent.patch.PatchOperation;
import com.mashape.unirest.http.JsonNode;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class ClientTest extends BaseClientTest {



    @Test
    public void testGet() {
        final Result<JsonNode>[] done = TestUtil.createResultArray(1);
        client.get("resources/Agent", null, result -> done[0] = result);
        wait.until(() -> done[0] != null);
        Result<JsonNode> result = done[0];
        Assert.assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        JSONArray array = result.getData().getArray();
        Assert.assertNotNull(array);
        Assert.assertTrue(array.length() > 0);
    }

    @Test
    public void testGetAll() {
        final Result<JsonNode>[] done = TestUtil.createResultArray(1);
        client.getAll("resources/Agent", null, result -> done[0] = result);
        wait.until(() -> done[0] != null);
        Result<JsonNode> result = done[0];
        Assert.assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        JSONArray array = result.getData().getArray();
        Assert.assertNotNull(array);
        Assert.assertTrue(array.length() > 0);
    }


    @Test
    public void testPost() {
        final Result<JsonNode>[] done = TestUtil.createResultArray(3);
        JSONObject delete = new JSONObject();
        delete.put("role", "TEST-JAVA-CLIENT");
        client.delete("resources/AgentAppRole", delete, data -> done[0] = data);
        wait.until(() -> done[0] != null);
        //Assert.assertEquals(done[0].getStatusCode(), HttpStatus.SC_NO_CONTENT);

        JSONObject payload = new JSONObject();
        payload.put("app_id", "none");
        payload.put("role", "TEST-JAVA-CLIENT");

        String postUrl = "resources/AgentAppRole";
        client.post(postUrl, payload, data -> {
            done[1] = data;
        });
        wait.until(() -> done[1] != null);
        Assert.assertEquals(HttpStatus.SC_CREATED, done[1].getStatusCode());
        String location = done[1].getString();
        Assert.assertNotNull(location);
        Assert.assertTrue(location.contains(postUrl));

        client.get(location, null, data -> done[2] = data);
        wait.until(() -> done[2] != null);

        Assert.assertEquals(HttpStatus.SC_OK, done[2].getStatusCode());
        JSONObject object = done[2].getData().getObject();
        Assert.assertNotNull(object);
        Assert.assertTrue(location.contains(object.getString("sys_id")));
    }

    @Test
    public void testPut() {
        final Result<JsonNode>[] done = TestUtil.createResultArray(4);
        JSONObject delete = new JSONObject();
        delete.put("role", "TEST-JAVA-CLIENT-PUT");
        client.delete("resources/AgentAppRole", delete, data -> done[0] = data);
        wait.until(() -> done[0] != null);

        JSONObject payload = new JSONObject();
        payload.put("app_id", "none");
        payload.put("role", "TEST-JAVA-CLIENT-PUT");

        String postUrl = "resources/AgentAppRole";
        client.post(postUrl, payload, data -> {
            done[1] = data;
        });
        wait.until(() -> done[1] != null);
        Assert.assertEquals(HttpStatus.SC_CREATED, done[1].getStatusCode());
        String location = done[1].getString();

        payload.put("app_id", "some-put");
        client.put(location, payload, data -> done[2] = data);
        wait.until(() -> done[2] != null);
        Assert.assertEquals(HttpStatus.SC_NO_CONTENT, done[2].getStatusCode());

        client.get(location, null, data -> done[3] = data);
        wait.until(() -> done[3] != null);
        JSONObject object = done[3].getData().getObject();
        Assert.assertNotNull(object);
        Assert.assertTrue(location.contains(object.getString("sys_id")));
        Assert.assertEquals("some-put", object.getString("app_id"));
    }

    @Test
    public void testPatch() {
        final Result<JsonNode>[] done = TestUtil.createResultArray(4);
        JSONObject delete = new JSONObject();
        delete.put("role", "TEST-JAVA-CLIENT-PATCH");
        client.delete("resources/AgentAppRole", delete, data -> done[0] = data);
        wait.until(() -> done[0] != null);

        JSONObject payload = new JSONObject();
        payload.put("app_id", "none");
        payload.put("role", "TEST-JAVA-CLIENT-PATCH");

        String postUrl = "resources/AgentAppRole";
        client.post(postUrl, payload, data -> {
            done[1] = data;
        });
        wait.until(() -> done[1] != null);
        Assert.assertEquals(HttpStatus.SC_CREATED, done[1].getStatusCode());
        String location = done[1].getString();

        Patch patch = new Patch(PatchOperation.REPLACE, "/app_id", "some-patch");
        client.patch(location, patch, data -> done[2] = data);
        wait.until(() -> done[2] != null);
        Assert.assertEquals(HttpStatus.SC_NO_CONTENT, done[2].getStatusCode());

        client.get(location, null, data -> done[3] = data);
        wait.until(() -> done[3] != null);
        JSONObject object = done[3].getData().getObject();
        Assert.assertNotNull(object);
        Assert.assertTrue(location.contains(object.getString("sys_id")));
        Assert.assertEquals("some-patch", object.getString("app_id"));
    }
}
