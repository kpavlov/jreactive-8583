@file:JvmName("StringLengthFieldBasedFrameDecoder")

package com.github.kpavlov.jreactive8583.netty.codec

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.util.CharsetUtil
import java.nio.ByteOrder

/**
 * Netty's [LengthFieldBasedFrameDecoder] assumes the frame length header
 * is a binary encoded integer.
 * This overrides it's frame length decoding to implement the case when
 * the frame length header is String encoded.
 *
 *
 * Uses [CharsetUtil.US_ASCII] for decoding
 */
internal open class StringLengthFieldBasedFrameDecoder

/**
 * @param maxFrameLength      the maximum length of the frame.  If the length of the frame is
 * greater than this value, `TooLongFrameException` will be
 * thrown.
 * @param lengthFieldOffset   the offset of the length field
 * @param lengthFieldLength   the length of the length field
 * @param lengthAdjustment    the compensation value to add to the value of the length field
 * @param initialBytesToStrip the number of first bytes to strip out from the decoded frame
 * @see LengthFieldBasedFrameDecoder
 */
constructor(
    maxFrameLength: Int,
    lengthFieldOffset: Int,
    lengthFieldLength: Int,
    lengthAdjustment: Int,
    initialBytesToStrip: Int
) : LengthFieldBasedFrameDecoder(
    maxFrameLength,
    lengthFieldOffset,
    lengthFieldLength,
    lengthAdjustment,
    initialBytesToStrip
) {
    public override fun getUnadjustedFrameLength(
        buf: ByteBuf,
        offset: Int,
        length: Int,
        order: ByteOrder
    ): Long {
        var b = buf
        b = b.order(order)
        val lengthBytes = ByteArray(length)
        b.getBytes(offset, lengthBytes)
        val s = String(lengthBytes, CharsetUtil.US_ASCII)
        return s.toLong()
    }
}
