package com.bodhi.superagent;

import com.mashape.unirest.http.JsonNode;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

public class NegativeTest extends BaseClientTest {

    @Test
    public void testGetNotJson() {
        Result<JsonNode>[] done = TestUtil.createResultArray(1);
        client.get("Something", null, result -> done[0] = result);
        wait.until(() -> done[0] != null);
        Assert.assertEquals(HttpStatus.SC_NOT_FOUND, done[0].getStatusCode());
        Assert.assertNotNull(done[0].getData());
    }
}
