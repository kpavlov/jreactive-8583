@file:JvmName("ConnectorConfigurer")

package com.github.kpavlov.jreactive8583

import io.netty.bootstrap.AbstractBootstrap
import io.netty.channel.ChannelPipeline

public interface ConnectorConfigurer<C : ConnectorConfiguration, B : AbstractBootstrap<*, *>> {
    /**
     * Hook added before completion of the bootstrap configuration.
     *
     *
     * This method is called during [AbstractIso8583Connector.init] phase.
     *
     *
     *
     * This implementation does nothing
     *
     * @param bootstrap     AbstractBootstrap to configure
     * @param configuration A [ConnectorConfiguration] to use
     */
    @JvmDefault
    public fun configureBootstrap(bootstrap: B, configuration: C) {
        // this method was intentionally left blank
    }

    /**
     * Hook added before completion of the pipeline configuration.
     *
     *
     * This method is called during
     * [com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer.initChannel] phase.
     *
     *
     * This implementation does nothing
     *
     * @param pipeline      A [ChannelPipeline] being configured
     * @param configuration A [ConnectorConfiguration] to use
     */
    @JvmDefault
    public fun configurePipeline(pipeline: ChannelPipeline, configuration: C) {
        // this method was intentionally left blank
    }
}
