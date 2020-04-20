package com.github.kpavlov.jreactive8583.netty.codec

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.util.CharsetUtil
import java.nio.ByteOrder

/**
 * Netty's [LengthFieldBasedFrameDecoder] assumes the frame length header is a binary encoded integer.
 * This overrides it's frame length decoding to implement the case when the frame length header is String encoded.
 *
 *
 * Uses [CharsetUtil.US_ASCII] for decoding
 */
class StringLengthFieldBasedFrameDecoder
/**
 * @see LengthFieldBasedFrameDecoder
 */
(
        maxFrameLength: Int,
        lengthFieldOffset: Int, lengthFieldLength: Int,
        lengthAdjustment: Int, initialBytesToStrip: Int) : LengthFieldBasedFrameDecoder(
        maxFrameLength,
        lengthFieldOffset, lengthFieldLength, lengthAdjustment,
        initialBytesToStrip) {
    override fun getUnadjustedFrameLength(buf: ByteBuf, offset: Int, length: Int, order: ByteOrder): Long {
        var buf = buf
        buf = buf.order(order)
        val lengthBytes = ByteArray(length)
        buf.getBytes(offset, lengthBytes)
        val s = String(lengthBytes, CharsetUtil.US_ASCII)
        return s.toLong()
    }
}
