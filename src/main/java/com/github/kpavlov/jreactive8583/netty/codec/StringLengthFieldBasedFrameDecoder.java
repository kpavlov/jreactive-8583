package com.github.kpavlov.jreactive8583.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.CharsetUtil;

import java.nio.ByteOrder;

/**
 * Netty's {@link LengthFieldBasedFrameDecoder} assumes the frame length header is a binary encoded integer.
 * This overrides it's frame length decoding to implement the case when the frame length header is String encoded.
 * <p>
 * Uses {@link CharsetUtil#US_ASCII} for decoding
 */
public class StringLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * @param maxFrameLength      the maximum length of the frame.  If the length of the frame is
     *                            greater than this value, <code>TooLongFrameException</code> will be
     *                            thrown.
     * @param lengthFieldOffset   the offset of the length field
     * @param lengthFieldLength   the length of the length field
     * @param lengthAdjustment    the compensation value to add to the value of the length field
     * @param initialBytesToStrip the number of first bytes to strip out from the decoded frame
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
