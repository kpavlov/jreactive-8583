package com.github.kpavlov.jreactive8583.netty.pipeline

import com.github.kpavlov.jreactive8583.IsoMessageListener
import com.github.kpavlov.jreactive8583.iso.MessageClass
import com.github.kpavlov.jreactive8583.iso.MessageFactory
import com.solab.iso8583.IsoMessage
import io.netty.channel.ChannelHandlerContext

internal class EchoMessageListener<T : IsoMessage>(
    private val isoMessageFactory: MessageFactory<T>
) : IsoMessageListener<T> {

    override fun applies(isoMessage: T): Boolean {
        return isoMessage.type and MessageClass.NETWORK_MANAGEMENT.value != 0
    }

    /**
     * Sends EchoResponse message. Always returns `false`.
     *
     * @param isoMessage a message to handle
     * @return `false` - message should not be handled by any other handler.
     */
    override fun onMessage(ctx: ChannelHandlerContext, isoMessage: T): Boolean {
        val echoResponse: IsoMessage = isoMessageFactory.createResponse(isoMessage)
        ctx.writeAndFlush(echoResponse)
        return false
    }
}
