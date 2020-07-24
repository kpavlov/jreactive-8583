package com.github.kpavlov.jreactive8583.netty.codec;

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
    private IsoMessage message;
    private int frameLengthHeaderLength;
    private int frameLengthFieldOffset;
    private int maxFrameLength;
    private int frameLengthFieldAdjust;

    @BeforeEach
    public void beforeClass() {
        maxFrameLength = 8192;
        frameLengthHeaderLength = 4;
        frameLengthFieldOffset = 0;
        frameLengthFieldAdjust = 0;

        encoder = new Iso8583Encoder(frameLengthHeaderLength, true);

        decoder = new StringLengthFieldBasedFrameDecoder(
                maxFrameLength,
                frameLengthFieldOffset,
                frameLengthHeaderLength,
                frameLengthFieldAdjust,
                frameLengthHeaderLength
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
                frameLengthFieldOffset,
                frameLengthHeaderLength,
                buf.order()
        );

        //then
        assertThat(frameLength).isEqualTo(content.length());
    }
}
