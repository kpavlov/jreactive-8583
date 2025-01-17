@file:JvmName("Iso8583Decoder")

package com.github.kpavlov.jreactive8583.netty.codec

import com.github.kpavlov.jreactive8583.iso.MessageFactory
import com.solab.iso8583.IsoMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * A decoder for ISO8583 messages.
 *
 * This class extends `ByteToMessageDecoder` and is responsible for decoding ISO8583 messages
 * from a Netty `ByteBuf` and outputting `IsoMessage` instances.
 *
 * @param messageFactory A factory for creating and parsing `IsoMessage` instances.
 */
public class Iso8583Decoder(
    private val messageFactory: MessageFactory<IsoMessage>,
) : ByteToMessageDecoder() {
    /**
     * Decodes ISO8583 message from [ByteBuf].
     *
     *
     * Message body starts immediately, no length header,
     * see `"lengthFieldFrameDecoder"` in
     * [com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer.initChannel]
     */
    @Throws(Exception::class)
    public override fun decode(
        ctx: ChannelHandlerContext,
        byteBuf: ByteBuf,
        out: MutableList<Any>,
    ) {
        if (!byteBuf.isReadable) {
            return
        }
        val bytes = ByteArray(byteBuf.readableBytes())
        byteBuf.readBytes(bytes)
        val isoMessage = messageFactory.parseMessage(bytes, 0)
        out.add(isoMessage)
    }
}
