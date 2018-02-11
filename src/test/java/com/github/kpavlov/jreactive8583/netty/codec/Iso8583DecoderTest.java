package com.github.kpavlov.jreactive8583.netty.codec;

import com.solab.iso8583.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Iso8583DecoderTest {

    private Iso8583Decoder decoder;

    @Mock
    private MessageFactory messageFactory;
    @Mock
    private List out;
    @Mock
    private ByteBuf byteBuf;
    @Mock
    private ChannelHandlerContext ctx;

    @Before
    public void beforeClass() {
        decoder = new Iso8583Decoder(messageFactory);
    }

    @Test
    public void testDecodeEmptyBypeBufDoesNothing() throws Exception {
        when(byteBuf.isReadable()).thenReturn(false);

        decoder.decode(ctx, byteBuf, out);

        verifyZeroInteractions(ctx, out, messageFactory);
    }
}