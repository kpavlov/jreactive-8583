package org.jreactive.iso8583;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelPipeline;

public interface ConnectorConfigurer<B extends AbstractBootstrap> {

    /**
     * Hook added before completion of the bootstrap configuration.
     * <p>
     * This method is called during {@link AbstractIso8583Connector#init()} phase.
     *
     * @param bootstrap AbstractBootstrap to configure
     */
    void configureBootstrap(B bootstrap);

    void configurePipeline(ChannelPipeline pipeline);
}
