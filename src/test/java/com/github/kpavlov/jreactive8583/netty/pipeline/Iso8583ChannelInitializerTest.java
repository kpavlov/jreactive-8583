package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.ConnectorConfiguration;
import com.github.kpavlov.jreactive8583.ConnectorConfigurer;
import com.github.kpavlov.jreactive8583.ConnectorConfigurerAdapter;
import com.github.kpavlov.jreactive8583.server.ServerConfiguration;
import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Iso8583ChannelInitializerTest {

    @Mock
    private EventLoopGroup workerGroup;
    @Mock
    private MessageFactory<com.solab.iso8583.IsoMessage> messageFactory;
    @Mock
    private ChannelHandler handlers;
    @Mock
    private Channel channel;
    @Mock
    private ChannelPipeline pipeline;
    private ConnectorConfigurer<ConnectorConfiguration, AbstractBootstrap> configurer;
    private ServerConfiguration.Builder configurationBuilder;

    @Before
    public void setUp() throws Exception {
        configurationBuilder = ServerConfiguration.newBuilder();
        configurer = new ConnectorConfigurerAdapter<>();

        when(channel.pipeline()).thenReturn(pipeline);
    }

    @Test
    public void testInitChannelWithLogger() throws Exception {
        //given
        configurationBuilder.withAddLoggingHandler(true);
        Iso8583ChannelInitializer<Channel, AbstractBootstrap, ConnectorConfiguration> channelInitializer = createChannelInitializer(configurer);

        // when
        channelInitializer.initChannel(channel);

        //then
        verify(pipeline).addLast(same(workerGroup), eq("logging"), any(IsoMessageLoggingHandler.class));
    }

    @Test
    public void testInitChannelWithoutLogger() throws Exception {
        //given
        configurationBuilder.withAddLoggingHandler(false);

        Iso8583ChannelInitializer<Channel, AbstractBootstrap, ConnectorConfiguration> channelInitializer = createChannelInitializer(configurer);

        //when
        channelInitializer.initChannel(channel);

        //then
        verify(pipeline, never()).addLast(any(EventLoopGroup.class), anyString(), any(IsoMessageLoggingHandler.class));
    }

    private Iso8583ChannelInitializer<Channel, AbstractBootstrap, ConnectorConfiguration> createChannelInitializer(ConnectorConfigurer<ConnectorConfiguration, AbstractBootstrap> configurer) {
        return new Iso8583ChannelInitializer<>(
                configurationBuilder.build(),
                configurer,
                workerGroup,
                messageFactory,
                handlers);
    }
}