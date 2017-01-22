package com.github.kpavlov.jreactive8583.server;

import com.github.kpavlov.jreactive8583.ConnectorConfiguration;

public class ServerConfiguration extends ConnectorConfiguration {

    /**
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public ServerConfiguration() {
        this(newBuilder());
    }

    public ServerConfiguration(Builder builder) {
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
