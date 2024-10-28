package com.github.kpavlov.jreactive8583.netty.pipeline

import com.github.kpavlov.jreactive8583.iso.MessageClass
import com.github.kpavlov.jreactive8583.iso.MessageFactory
import com.github.kpavlov.jreactive8583.iso.MessageFunction
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent

/**
 * IdleEventHandler sends heartbeats (administrative messages) when channel becomes idle,
 * i.e. `IdleStateEvent` is received.
 */
public open class IdleEventHandler<T>(
    private val isoMessageFactory: MessageFactory<T>,
) : ChannelInboundHandlerAdapter() {
    public override fun userEventTriggered(
        ctx: ChannelHandlerContext,
        evt: Any,
    ) {
        if (evt is IdleStateEvent &&
            (evt.state() == IdleState.READER_IDLE || evt.state() == IdleState.ALL_IDLE)
        ) {
            val heartbeatMessage = createHeartbeatMessage()
            ctx.write(heartbeatMessage)
            ctx.flush()
        }
    }

    protected fun createHeartbeatMessage(): T =
        isoMessageFactory.newMessage(
            MessageClass.NETWORK_MANAGEMENT,
            MessageFunction.REQUEST,
        )
}
