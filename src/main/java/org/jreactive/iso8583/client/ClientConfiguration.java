package org.jreactive.iso8583.client;

import org.jreactive.iso8583.ConnectorConfiguration;

public class ClientConfiguration extends ConnectorConfiguration {

    /**
     * Default client reconnect interval in milliseconds.
     */
    private static final int DEFAULT_RECONNECT_INTERVAL = 100;

    private int reconnectInterval = DEFAULT_RECONNECT_INTERVAL;

    public ClientConfiguration() {
        this(newBuilder());
    }

    /**
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public ClientConfiguration(Builder builder) {
        super(builder);
        this.reconnectInterval = builder.reconnectInterval;
    }

    public static ClientConfiguration.Builder newBuilder() {
        return new ClientConfiguration.Builder();
    }

    /**
     * Client reconnect interval in milliseconds.
     *
     * @return interval between reconnects, in milliseconds.
     */
    public int getReconnectInterval() {
        return reconnectInterval;
    }

    /**
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setReconnectInterval(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public static class Builder extends ConnectorConfiguration.Builder<ClientConfiguration> {
        private int reconnectInterval = DEFAULT_RECONNECT_INTERVAL;

        public Builder withReconnectInterval(int reconnectInterval) {
            this.reconnectInterval = reconnectInterval;
            return this;
        }

        public ClientConfiguration build() {
            return new ClientConfiguration(this);
        }
    }
}
