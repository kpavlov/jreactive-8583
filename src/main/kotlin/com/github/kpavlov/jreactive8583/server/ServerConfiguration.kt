@file:JvmName("ServerConfiguration")

package com.github.kpavlov.jreactive8583.server

import com.github.kpavlov.jreactive8583.ConnectorConfiguration

@SuppressWarnings("WeakerAccess")
public class ServerConfiguration(
    builder: Builder
) : ConnectorConfiguration(builder) {

    public companion object {
        @JvmStatic
        public fun newBuilder(): Builder = Builder()

        @Suppress("unused")
        @JvmStatic
        public fun getDefault(): ServerConfiguration = newBuilder().build()
    }

    public class Builder : ConnectorConfiguration.Builder<Builder>() {
        public fun build(): ServerConfiguration = ServerConfiguration(this)
    }
}
