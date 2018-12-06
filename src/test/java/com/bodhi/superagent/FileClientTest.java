package com.bodhi.superagent;

import com.mashape.unirest.http.JsonNode;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileClientTest extends BaseClientTest
{

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private static final String DEFAULT_BUCKET = "apps";


    @Test
    public void testUploadBody()
    {
        byte[] body = "test body upload double".getBytes();
        String uploadPath = "test-body-upload-double/test-body-upload-double.txt";
        deleteFile(uploadPath);
        @SuppressWarnings("unchecked") Result<JsonNode>[] done = TestUtil.createResultArray(1);
        client.uploadFile(uploadPath, ContentType.TEXT_PLAIN, DEFAULT_BUCKET, body, result -> done[0] = result);
        wait.until(() -> done[0] != null);
        testFileResponseValidCreated(done[0]);

    }

    @Test
    public void testDoubleUploadBody()
    {
        byte[] body = "test body upload".getBytes();
        String uploadPath = "test-body-upload/test-body-upload.txt";
        deleteFile(uploadPath);
        final Result<JsonNode>[] done = TestUtil.createResultArray(2);
        client.uploadFile(uploadPath, ContentType.TEXT_PLAIN, DEFAULT_BUCKET, body, result -> done[0] = result);
        wait.until(() -> done[0] != null);
        testFileResponseValidCreated(done[0]);
        client.uploadFile(uploadPath, ContentType.TEXT_PLAIN, DEFAULT_BUCKET, body, result -> done[1] = result);
        wait.until(() -> done[1] != null);
        testFileResponseValidCreated(done[1]);
    }


    @Test
    public void testUploadFile() throws IOException
    {
        String uploadPath = "test-body-file/test-body-file.txt";
        deleteFile(uploadPath);
        byte[] content = new byte[]{3, 4, 5};
        File file = folder.newFile("test-upload.txt");
        Files.write(Paths.get(file.getAbsolutePath()), content);

        @SuppressWarnings("unchecked") Result<JsonNode>[] done = TestUtil.createResultArray(1);
        client.uploadFile(uploadPath, DEFAULT_BUCKET, file, result -> done[0] = result);
        wait.until(() -> done[0] != null);
        testFileResponseValidCreated(done[0]);

    }

    @Test
    public void testDownloadFile() throws IOException
    {
        String uploadPath = "test-download-file/test-download-file.txt";
        deleteFile(uploadPath);
        byte[] content = new byte[]{7, 8, 9};
        File file = folder.newFile("test-download.txt");
        Files.write(Paths.get(file.getAbsolutePath()), content);

        Result<JsonNode>[] done = TestUtil.createResultArray(1);
        client.uploadFile(uploadPath, DEFAULT_BUCKET, file, result -> done[0] = result);
        wait.until(() -> done[0] != null);
        testFileResponseValidCreated(done[0]);
        Result<InputStream>[] downloadDone = TestUtil.createResultArray(1);
        client.downloadFile(uploadPath, DEFAULT_BUCKET, result -> downloadDone[0] = result);
        wait.until(() -> downloadDone[0] != null);
        Result<InputStream> downloadResult = downloadDone[0];
        Assert.assertEquals(HttpStatus.SC_OK, downloadResult.getStatusCode());

        byte[] resultData = new byte[content.length];
        int bytesRead = downloadResult.getData().read(resultData);
        Assert.assertEquals(content.length, bytesRead);
        Assert.assertArrayEquals(content, resultData);

    }


    private void deleteFile(String deletePath)
    {
        Result<JsonNode>[] done = TestUtil.createResultArray(1);
        client.deleteFile(deletePath, DEFAULT_BUCKET, result -> done[0] = result);
        wait.until(() -> done[0] != null);
    }

    private void testFileResponseValidCreated(Result<JsonNode> data)
    {
        Assert.assertNotNull(data);
        Assert.assertEquals(HttpStatus.SC_ACCEPTED, data.getStatusCode());
        Assert.assertNotNull(data.getData().getArray());
        Assert.assertEquals(1, data.getData().getArray().length());
        Assert.assertTrue(data.getData().getArray().get(0) instanceof JSONObject);
        JSONObject object = (JSONObject) data.getData().getArray().get(0);
        Assert.assertEquals(DEFAULT_BUCKET, object.getString("bucket"));
    }

}
