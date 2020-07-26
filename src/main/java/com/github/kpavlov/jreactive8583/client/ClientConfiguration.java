package com.github.kpavlov.jreactive8583.client;

import com.github.kpavlov.jreactive8583.ConnectorConfiguration;

@SuppressWarnings("WeakerAccess")
public class ClientConfiguration extends ConnectorConfiguration {

    /**
     * Default client reconnect interval in milliseconds.
     */
    private static final int DEFAULT_RECONNECT_INTERVAL = 100;

    private int reconnectInterval;

    /**
     * Legacy constructor
     *
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public ClientConfiguration() {
        this(newBuilder());
    }

    private ClientConfiguration(final Builder builder) {
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
     * Set reconnect interval
     *
     * @param reconnectInterval interval between reconnects, in milliseconds.
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setReconnectInterval(final int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public static class Builder extends ConnectorConfiguration.Builder<Builder> {
        private int reconnectInterval = DEFAULT_RECONNECT_INTERVAL;

        public Builder reconnectInterval(final int reconnectInterval) {
            this.reconnectInterval = reconnectInterval;
            return this;
        }

        /**
         * @deprecated Use {@link #reconnectInterval(int)} instead
         *
         * @param reconnectInterval Reconnect interval in millis
         * @return Tha same {@link Builder}
         */
        @Deprecated
        public Builder withReconnectInterval(final int reconnectInterval) {
            return reconnectInterval(reconnectInterval);
        }

        public ClientConfiguration build() {
            return new ClientConfiguration(this);
        }
    }
}
