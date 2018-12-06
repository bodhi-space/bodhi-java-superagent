package com.bodhi.superagent;

import com.bodhi.superagent.backoff.BackoffConfig;

public class ClientConfig {

    private String uri;
    private final String filesUri;
    private String namespace;
    private Credentials credentials;
    private BackoffConfig backoffConfig;

    public ClientConfig(String uri, String namespace, Credentials credentials, BackoffConfig backoffConfig) {
        this.uri = uri;
        this.filesUri = uri.replace("api", "files");
        this.namespace = namespace;
        this.credentials = credentials;
        this.backoffConfig = backoffConfig;
    }

    public String getUri() {
        return uri;
    }

    public String getNamespace() {
        return namespace;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public BackoffConfig getBackoffConfig() {
        return backoffConfig;
    }

    public String getNamespaceUri() {
        return uri+"/"+namespace;
    }

    public String getFilesUri()
    {
        return filesUri;
    }
}
