package com.github.kpavlov.jreactive8583.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.CharsetUtil;

import java.nio.ByteOrder;

/**
 *  Netty's {@link LengthFieldBasedFrameDecoder} assumes the frame length header is a binary encoded integer.
 *  This overrides it's frame length decoding to implement the case when the frame length header is String encoded.
 *
 *  Uses {@link CharsetUtil#US_ASCII} for decoding
 */
public class StringLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * @see LengthFieldBasedFrameDecoder
     */
    public StringLengthFieldBasedFrameDecoder(
            final int maxFrameLength,
            final int lengthFieldOffset, final int lengthFieldLength,
            final int lengthAdjustment, final int initialBytesToStrip) {
        super(
                maxFrameLength,
                lengthFieldOffset, lengthFieldLength, lengthAdjustment,
                initialBytesToStrip);
    }

    @Override
    protected long getUnadjustedFrameLength(ByteBuf buf, final int offset, final int length, final ByteOrder order) {
        buf = buf.order(order);
        final var lengthBytes = new byte[length];
        buf.getBytes(offset, lengthBytes);
        final var s = new String(lengthBytes, CharsetUtil.US_ASCII);
        return Long.parseLong(s);
    }
}
