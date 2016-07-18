package com.bodhi.superagent;

import com.googlecode.junittoolbox.PollingWait;
import org.junit.After;
import org.junit.Before;

import java.util.concurrent.TimeUnit;

public class BaseClientTest {

    protected Client client;

    protected PollingWait wait = new PollingWait().timeoutAfter(50, TimeUnit.SECONDS)
            .pollEvery(100, TimeUnit.MILLISECONDS);


    @Before
    public void init() {
        client =  new Client("https://api.bodhi-dev.io", "<namespace>", new BasicCredentials("<login>", "<password>"));
    }

    @After
    public void clean() {
        client = null;
    }
}
