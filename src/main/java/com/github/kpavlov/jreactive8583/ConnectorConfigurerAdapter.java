package com.github.kpavlov.jreactive8583;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.ChannelPipeline;

public class ConnectorConfigurerAdapter<C extends ConnectorConfiguration, B extends AbstractBootstrap> implements ConnectorConfigurer<C, B> {

    /**
     * @implSpec This implementation does nothing
     */
    @Override
    public void configureBootstrap(B bootstrap, C configuration) {
    }

    /**
     * @implSpec This implementation does nothing
     */
    @Override
    public void configurePipeline(ChannelPipeline pipeline, C configuration) {
    }

}
