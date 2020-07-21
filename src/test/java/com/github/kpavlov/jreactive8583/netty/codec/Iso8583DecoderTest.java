package com.github.kpavlov.jreactive8583.netty.codec;

import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.solab.iso8583.IsoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Iso8583DecoderTest {

    private Iso8583Decoder decoder;

    @Mock
    private MessageFactory<IsoMessage> messageFactory;
    @Mock
    private List<Object> out;
    @Mock
    private ByteBuf byteBuf;
    @Mock
    private ChannelHandlerContext ctx;

    @BeforeEach
    public void beforeClass() {
        decoder = new Iso8583Decoder(messageFactory);
    }

    @Test
    public void testDecodeEmptyByteBufDoesNothing() throws Exception {
        when(byteBuf.isReadable()).thenReturn(false);

        decoder.decode(ctx, byteBuf, out);

        verifyNoInteractions(ctx, out, messageFactory);
    }
}
