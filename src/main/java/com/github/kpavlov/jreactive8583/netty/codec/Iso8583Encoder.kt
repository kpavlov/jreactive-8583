package com.github.kpavlov.jreactive8583.netty.codec

import com.solab.iso8583.IsoMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import io.netty.util.CharsetUtil

@Sharable
class Iso8583Encoder(private val lengthHeaderLength: Int, private val encodeLengthHeaderAsString: Boolean) : MessageToByteEncoder<IsoMessage>() {
    override fun encode(ctx: ChannelHandlerContext, isoMessage: IsoMessage, out: ByteBuf) {
        if (lengthHeaderLength == 0) {
            val bytes = isoMessage.writeData()
            out.writeBytes(bytes)
        } else if (encodeLengthHeaderAsString) {
            val data = isoMessage.writeData()
            val lengthHeader = String.format("%0" + lengthHeaderLength + "d", data.size)
            out.writeBytes(lengthHeader.toByteArray(CharsetUtil.US_ASCII))
            out.writeBytes(data)
        } else {
            val byteBuffer = isoMessage.writeToBuffer(lengthHeaderLength)
            out.writeBytes(byteBuffer)
        }
    }

}
