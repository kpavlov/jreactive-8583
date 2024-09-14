@file:JvmName("ClientConfiguration")

package com.github.kpavlov.jreactive8583.client

import com.github.kpavlov.jreactive8583.ConnectorConfiguration

public open class ClientConfiguration(
    builder: Builder
) : ConnectorConfiguration(builder) {

    /**
     * Client reconnect interval in milliseconds.
     *
     * @return interval between reconnects, in milliseconds.
     */
    internal val reconnectInterval: Int = builder.reconnectInterval

    public companion object {

        /**
         * Default client reconnect interval in milliseconds.
         */
        public const val DEFAULT_RECONNECT_INTERVAL: Int = 100

        @JvmStatic
        public fun newBuilder(): Builder = Builder()

        @Suppress("unused")
        @JvmStatic
        public fun getDefault(): ClientConfiguration = newBuilder().build()
    }

    @Suppress("unused")
    public data class Builder(
        var reconnectInterval: Int = DEFAULT_RECONNECT_INTERVAL
    ) : ConnectorConfiguration.Builder<Builder>() {

        public fun reconnectInterval(reconnectInterval: Int): Builder =
            apply { this.reconnectInterval = reconnectInterval }

        public fun build(): ClientConfiguration = ClientConfiguration(this)
    }
}
