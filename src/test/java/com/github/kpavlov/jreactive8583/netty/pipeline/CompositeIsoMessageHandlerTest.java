package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompositeIsoMessageHandlerTest {

    @Mock
    private IsoMessageListener<IsoMessage> listener1;
    @Mock
    private IsoMessageListener<IsoMessage> listener2;
    @Mock
    private IsoMessageListener<IsoMessage> listener3;
    @Mock
    private IsoMessage message;
    @Mock
    private ChannelHandlerContext ctx;

    private CompositeIsoMessageHandler<IsoMessage> handler = new CompositeIsoMessageHandler<>();

    @Before
    public void setUp() {
        //noinspection unchecked
        handler.addListeners(listener1, listener2, listener3);
    }

    @Test
    public void shouldRemoveListener() throws Exception {
        //given
        when(listener1.applies(message)).thenReturn(true);
        when(listener3.applies(message)).thenReturn(true);
        when(listener1.onMessage(ctx, message)).thenReturn(true);

        //when
        handler.removeListener(listener2);
        handler.channelRead(ctx, message);

        //then
        verify(listener1).onMessage(ctx, message);
        verify(listener2, never()).onMessage(ctx, message);
        verify(listener3).onMessage(ctx, message);
    }

    @Test
    public void shouldHandleWithAppropriateHandler() throws Exception {
        //given
        when(listener1.applies(message)).thenReturn(false);
        when(listener2.applies(message)).thenReturn(true);
        when(listener3.applies(message)).thenReturn(false);
        when(listener2.onMessage(ctx, message)).thenReturn(true);

        //when
        handler.channelRead(ctx, message);

        //then
        verify(listener1, never()).onMessage(ctx, message);
        verify(listener2).onMessage(ctx, message);
        verify(listener3, never()).onMessage(ctx, message);
    }

    @Test
    public void testStopProcessing() throws Exception {
        //given
        when(listener1.applies(message)).thenReturn(true);
        when(listener2.applies(message)).thenReturn(true);
        when(listener1.onMessage(ctx, message)).thenReturn(true);
        when(listener2.onMessage(ctx, message)).thenReturn(false);

        //when
        handler.channelRead(ctx, message);

        //then
        verify(listener1).onMessage(ctx, message);
        verify(listener2).onMessage(ctx, message);
        verify(listener3, never()).onMessage(ctx, message);
    }

    @Test
    public void shouldNotFailOnExceptionInFailsafeMode() throws Exception {
        //given
        handler = new CompositeIsoMessageHandler<>(false);
        //noinspection unchecked
        handler.addListeners(listener1, listener2, listener3);

        when(listener1.applies(message)).thenReturn(true);
        when(listener2.applies(message)).thenThrow(new RuntimeException("Expected exception"));
        when(listener3.applies(message)).thenReturn(true);
        when(listener1.onMessage(ctx, message)).thenReturn(true);

        // when
        handler.channelRead(ctx, message);

        //then
        verify(listener1).onMessage(ctx, message);
        verify(listener2, never()).onMessage(ctx, message);
        verify(listener3).onMessage(ctx, message);
    }
}