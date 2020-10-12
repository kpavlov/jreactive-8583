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

    internal companion object {

        /**
         * Default client reconnect interval in milliseconds.
         */
        const val DEFAULT_RECONNECT_INTERVAL = 100

        @JvmStatic
        fun newBuilder(): Builder = Builder()

        @Suppress("unused")
        @JvmStatic
        fun getDefault(): ClientConfiguration = newBuilder().build()
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
