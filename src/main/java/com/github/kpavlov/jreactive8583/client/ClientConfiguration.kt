package com.github.kpavlov.jreactive8583.client;

import com.github.kpavlov.jreactive8583.ConnectorConfiguration;


@SuppressWarnings("WeakerAccess")
public class ClientConfiguration(
    builder: Builder
) : ConnectorConfiguration(builder) {

    /**
     * Client reconnect interval in milliseconds.
     *
     * @return interval between reconnects, in milliseconds.
     */
    val reconnectInterval: Int = builder.reconnectInterval;

    companion object {

        /**
         * Default client reconnect interval in milliseconds.
         */
        const val DEFAULT_RECONNECT_INTERVAL = 100;

        @JvmStatic
        public fun newBuilder(): Builder {
            return ClientConfiguration.Builder();
        }

        @JvmStatic
        public fun getDefault(): ClientConfiguration {
            return newBuilder().build();
        }
    }

    public class Builder : ConnectorConfiguration.Builder<Builder>() {

        var reconnectInterval = DEFAULT_RECONNECT_INTERVAL;

        public fun build(): ClientConfiguration {
            return com.github.kpavlov.jreactive8583.client.ClientConfiguration(this);
        }
    }

}

