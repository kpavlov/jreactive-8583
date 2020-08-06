@file:JvmName("ServerConfiguration")

package com.github.kpavlov.jreactive8583.server

import com.github.kpavlov.jreactive8583.ConnectorConfiguration

@SuppressWarnings("WeakerAccess")
class ServerConfiguration(
    builder: Builder
) : ConnectorConfiguration(builder) {

    companion object {
        @JvmStatic
        fun newBuilder(): Builder = Builder()

        @Suppress("unused")
        @JvmStatic
        fun getDefault(): ServerConfiguration = newBuilder().build()
    }

    class Builder : ConnectorConfiguration.Builder<Builder>() {

        fun build() = ServerConfiguration(this)
    }

}
