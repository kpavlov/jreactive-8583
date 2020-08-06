@file:JvmName("ClientConfiguration")

package com.github.kpavlov.jreactive8583.client

import com.github.kpavlov.jreactive8583.ConnectorConfiguration

open class ClientConfiguration(
    builder: Builder
) : ConnectorConfiguration(builder) {

    /**
     * Client reconnect interval in milliseconds.
     *
     * @return interval between reconnects, in milliseconds.
     */
    val reconnectInterval: Int = builder.reconnectInterval

    companion object {

        /**
         * Default client reconnect interval in milliseconds.
         */
        const val DEFAULT_RECONNECT_INTERVAL = 100

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @Suppress("unused")
        @JvmStatic
        fun getDefault(): ClientConfiguration {
            return newBuilder().build()
        }
    }

    @Suppress("unused")
    data class Builder(
        var reconnectInterval: Int = DEFAULT_RECONNECT_INTERVAL
    ) : ConnectorConfiguration.Builder<Builder>() {

        fun reconnectInterval(reconnectInterval: Int) =
            apply { this.reconnectInterval = reconnectInterval }

        fun build() = ClientConfiguration(this)
    }

}

