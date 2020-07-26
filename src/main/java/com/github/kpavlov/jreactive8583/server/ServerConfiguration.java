package com.github.kpavlov.jreactive8583.server;

import com.github.kpavlov.jreactive8583.ConnectorConfiguration;

public class ServerConfiguration extends ConnectorConfiguration {

    public ServerConfiguration(final Builder builder) {
        super(builder);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static ServerConfiguration getDefault() {
        return newBuilder().build();
    }

    public static class Builder extends ConnectorConfiguration.Builder<Builder> {
        public ServerConfiguration build() {
            return new ServerConfiguration(this);
        }
    }
}
