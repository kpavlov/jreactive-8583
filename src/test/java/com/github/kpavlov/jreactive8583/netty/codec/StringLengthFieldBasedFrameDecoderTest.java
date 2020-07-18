package com.github.kpavlov.jreactive8583.netty.codec;

import com.github.kpavlov.jreactive8583.ConnectorConfiguration;
import com.solab.iso8583.IsoMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StringLengthFieldBasedFrameDecoderTest {

    private Iso8583Encoder encoder;
    private StringLengthFieldBasedFrameDecoder decoder;

    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private ConnectorConfiguration config;
    @Mock
    private IsoMessage message;

    @BeforeEach
    public void beforeClass() {
        when(config.getMaxFrameLength()).thenReturn(8192);
        when(config.getFrameLengthFieldOffset()).thenReturn(0);
        when(config.getFrameLengthFieldLength()).thenReturn(4);
        when(config.getFrameLengthFieldAdjust()).thenReturn(0);
        when(config.encodeFrameLengthAsString()).thenReturn(true);

        encoder = new Iso8583Encoder(config.getFrameLengthFieldLength(), config.encodeFrameLengthAsString());
        decoder = new StringLengthFieldBasedFrameDecoder(
                config.getMaxFrameLength(),
                config.getFrameLengthFieldOffset(),
                config.getFrameLengthFieldLength(),
                config.getFrameLengthFieldAdjust(),
                config.getFrameLengthFieldLength()
        );
    }

    @Test
    public void shouldGetUnadjustedFrameLength() {
        // given
        final var content = "MESSAGE";
        when(message.writeData()).thenReturn(content.getBytes(StandardCharsets.US_ASCII));

        final var buf = Unpooled.buffer();
        encoder.encode(ctx, message, buf);
        assertThat(buf.toString(StandardCharsets.US_ASCII)).isEqualTo("0007MESSAGE");

        //when
        final var frameLength = decoder.getUnadjustedFrameLength(
                buf,
                config.getFrameLengthFieldOffset(),
                config.getFrameLengthFieldLength(),
                buf.order()
        );

        //then
        assertThat(frameLength).isEqualTo(content.length());
    }
}
