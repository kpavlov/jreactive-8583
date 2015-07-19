package org.jreactive.iso8583;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelPipeline;

public interface ConnectorConfigurer<C extends ConnectorConfiguration, B extends AbstractBootstrap> {

    /**
     * Hook added before completion of the bootstrap configuration.
     * <p>
     * This method is called during {@link AbstractIso8583Connector#init()} phase.
     * </p>
     *
     * @param bootstrap     AbstractBootstrap to configure.
     * @param configuration A {@link ConnectorConfiguration} to use.
     */
    void configureBootstrap(B bootstrap, C configuration);

    void configurePipeline(ChannelPipeline pipeline, C configuration);
}
