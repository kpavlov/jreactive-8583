package org.jreactive.iso8583.server;

import org.jreactive.iso8583.ConnectorConfiguration;

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

    public static class Builder extends ConnectorConfiguration.Builder<ServerConfiguration> {
        public ServerConfiguration build() {
            return new ServerConfiguration(this);
        }
    }
}
