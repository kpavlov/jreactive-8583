@file:JvmName("Iso8583Encoder")

package com.github.kpavlov.jreactive8583.netty.codec

import com.solab.iso8583.IsoMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.util.CharsetUtil

@Sharable
internal class Iso8583Encoder(
    private val lengthHeaderLength: Int,
    private val encodeLengthHeaderAsString: Boolean
) :
    MessageToByteEncoder<IsoMessage>() {
    public override fun encode(ctx: ChannelHandlerContext, isoMessage: IsoMessage, out: ByteBuf) {
        when {
            lengthHeaderLength == 0 -> {
                val bytes = isoMessage.writeData()
                out.writeBytes(bytes)
            }
            encodeLengthHeaderAsString -> {
                val bytes = isoMessage.writeData()
                val lengthHeader = String.format("%0" + lengthHeaderLength + "d", bytes.size)
                out.writeBytes(lengthHeader.toByteArray(CharsetUtil.US_ASCII))
                out.writeBytes(bytes)
            }
            else -> {
                val byteBuffer = isoMessage.writeToBuffer(lengthHeaderLength)
                out.writeBytes(byteBuffer)
            }
        }
    }
}
