package org.jreactive.iso8583.netty.pipeline;

import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import org.jreactive.iso8583.ConnectorConfiguration;
import org.jreactive.iso8583.ConnectorConfigurer;
import org.jreactive.iso8583.ConnectorConfigurerAdapter;
import org.jreactive.iso8583.server.ServerConfiguration;
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

    private Iso8583ChannelInitializer<Channel, io.netty.bootstrap.AbstractBootstrap, ConnectorConfiguration> channelInitializer;
    private ConnectorConfiguration configuration;

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

    @Before
    public void setUp() throws Exception {
        configuration = new ServerConfiguration();
        ConnectorConfigurer<ConnectorConfiguration, AbstractBootstrap> configurer = new ConnectorConfigurerAdapter<>();
        channelInitializer = new Iso8583ChannelInitializer<>(
                configuration,
                configurer,
                workerGroup,
                messageFactory,
                handlers);

        when(channel.pipeline()).thenReturn(pipeline);
    }

    @Test
    public void testInitChannelWithLogger() throws Exception {
        configuration.setAddLoggingHandler(true);

        channelInitializer.initChannel(channel);

        verify(pipeline).addLast(same(workerGroup), eq("logging"), any(IsoMessageLoggingHandler.class));
    }

    @Test
    public void testInitChannelWithoutLogger() throws Exception {
        configuration.setAddLoggingHandler(false);

        channelInitializer.initChannel(channel);

        verify(pipeline, never()).addLast(any(EventLoopGroup.class), anyString(), any(IsoMessageLoggingHandler.class));
    }
}