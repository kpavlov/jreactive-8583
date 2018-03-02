package com.github.kpavlov.jreactive8583.client;

import com.github.kpavlov.jreactive8583.ConnectorConfiguration;

public class ClientConfiguration extends ConnectorConfiguration {

    /**
     * Default client reconnect interval in milliseconds.
     */
    private static final int DEFAULT_RECONNECT_INTERVAL = 100;

    private int reconnectInterval = DEFAULT_RECONNECT_INTERVAL;

    /**
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public ClientConfiguration() {
        this(newBuilder());
    }

    private ClientConfiguration(Builder builder) {
        super(builder);
        this.reconnectInterval = builder.reconnectInterval;
    }

    public static ClientConfiguration.Builder newBuilder() {
        return new ClientConfiguration.Builder();
    }

    public static ClientConfiguration getDefault() {
        return newBuilder().build();
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
     * @param reconnectInterval   interval between reconnects, in milliseconds.
     */
    @Deprecated
    public void setReconnectInterval(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public static class Builder extends ConnectorConfiguration.Builder<Builder> {
        private int reconnectInterval = DEFAULT_RECONNECT_INTERVAL;

        public Builder reconnectInterval(int reconnectInterval) {
            this.reconnectInterval = reconnectInterval;
            return this;
        }

        /**
         * @deprecated Use {@link #reconnectInterval(int)} instead
         */
        @Deprecated
        public Builder withReconnectInterval(int reconnectInterval) {
            return reconnectInterval(reconnectInterval);
        }

        public ClientConfiguration build() {
            return new ClientConfiguration(this);
        }
    }
}
