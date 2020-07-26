package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.ConnectorConfiguration;
import com.github.kpavlov.jreactive8583.ConnectorConfigurer;
import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.github.kpavlov.jreactive8583.server.ServerConfiguration;
import com.solab.iso8583.IsoMessage;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Iso8583ChannelInitializerTest {

    @Mock
    private EventLoopGroup workerGroup;
    @Mock
    private MessageFactory<IsoMessage> messageFactory;
    @Mock
    private ChannelHandler handlers;
    @Mock
    private Channel channel;
    @Mock
    private ChannelPipeline pipeline;
    private ConnectorConfigurer configurer;
    private ServerConfiguration.Builder configurationBuilder;

    @BeforeEach
    public void setUp() {
        configurationBuilder = ServerConfiguration.newBuilder();
        configurer = new ConnectorConfigurer() {
        };

        when(channel.pipeline()).thenReturn(pipeline);
    }

    @Test
    public void testInitChannelWithLogger() {
        //given
        configurationBuilder.addLoggingHandler(true);
        final var channelInitializer = createChannelInitializer(configurer);

        // when
        channelInitializer.initChannel(channel);

        //then
        verify(pipeline).addLast(same(workerGroup), eq("logging"), any(IsoMessageLoggingHandler.class));
    }

    @Test
    public void testInitChannelWithoutLogger() {
        //given
        configurationBuilder.addLoggingHandler(false);

        final var channelInitializer = createChannelInitializer(configurer);

        //when
        channelInitializer.initChannel(channel);

        //then
        verify(pipeline, never()).addLast(any(EventLoopGroup.class), anyString(), any(IsoMessageLoggingHandler.class));
    }

    @Test
    public void testInitChannelWithDefaultLoggingSetting() {
        //given
        final var channelInitializer = createChannelInitializer(configurer);

        //when
        channelInitializer.initChannel(channel);

        //then
        verify(pipeline, never())
            .addLast(any(EventLoopGroup.class), anyString(), any(IsoMessageLoggingHandler.class));
    }

    private Iso8583ChannelInitializer<Channel, AbstractBootstrap<?, ?>, ConnectorConfiguration> createChannelInitializer(final ConnectorConfigurer<ConnectorConfiguration, AbstractBootstrap<?, ?>> configurer) {
        return new Iso8583ChannelInitializer(
            configurationBuilder.build(),
            configurer,
            workerGroup,
            messageFactory,
            handlers);
    }
}
