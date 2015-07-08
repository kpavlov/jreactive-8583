package org.jreactive.iso8583;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelPipeline;

public class ConnectorConfigurerAdapter<B extends AbstractBootstrap> implements ConnectorConfigurer<B> {

    @Override
    public void configureBootstrap(B bootstrap) {
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
    }

}
