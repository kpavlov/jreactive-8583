package com.github.kpavlov.jreactive8583;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelPipeline;

public interface ConnectorConfigurer<C extends ConnectorConfiguration, B extends AbstractBootstrap> {

    /**
     * Hook added before completion of the bootstrap configuration.
     * <p>
     * This method is called during {@link AbstractIso8583Connector#init()} phase.
     * </p>
     *
     * @param bootstrap     AbstractBootstrap to configure
     * @param configuration A {@link ConnectorConfiguration} to use
     * @implSpec This implementation does nothing
     */
    default void configureBootstrap(B bootstrap, C configuration) {
        // this method was intentionally left blank
    }

    /**
     * Hook added before completion of the pipeline configuration.
     * <p>
     * This method is called during
     * {@link com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer#initChannel(io.netty.channel.Channel)} phase.
     * </p>
     *
     * @param pipeline      A {@link ChannelPipeline} being configured
     * @param configuration A {@link ConnectorConfiguration} to use
     * @implSpec This implementation does nothing
     */
    default void configurePipeline(ChannelPipeline pipeline, C configuration) {
        // this method was intentionally left blank
    }
}
