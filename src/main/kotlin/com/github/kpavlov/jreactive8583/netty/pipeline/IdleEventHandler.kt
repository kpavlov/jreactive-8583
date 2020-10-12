package com.github.kpavlov.jreactive8583.netty.pipeline

import com.github.kpavlov.jreactive8583.iso.MessageClass
import com.github.kpavlov.jreactive8583.iso.MessageFactory
import com.github.kpavlov.jreactive8583.iso.MessageFunction
import com.github.kpavlov.jreactive8583.iso.MessageOrigin
import com.solab.iso8583.IsoMessage
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

internal class IdleEventHandler(
    private val isoMessageFactory: MessageFactory<IsoMessage>
) : ChannelInboundHandlerAdapter() {

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            if (evt.state() == IdleState.READER_IDLE || evt.state() == IdleState.ALL_IDLE) {
                val echoMessage = createEchoMessage()
                ctx.write(echoMessage)
                ctx.flush()
            }
        }
    }

    private fun createEchoMessage(): IsoMessage {
        return isoMessageFactory.newMessage(
            MessageClass.NETWORK_MANAGEMENT,
            MessageFunction.REQUEST,
            MessageOrigin.ACQUIRER
        )
    }
}
