package com.github.kpavlov.jreactive8583.netty.pipeline

import com.github.kpavlov.jreactive8583.iso.MessageClass
import com.github.kpavlov.jreactive8583.iso.MessageFactory
import com.github.kpavlov.jreactive8583.iso.MessageFunction
import com.github.kpavlov.jreactive8583.iso.MessageOrigin
import com.solab.iso8583.IsoMessage
import com.solab.iso8583.IsoType
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.text.ParseException

/**
 * Handles [ParseException]s and responds with administrative message
 *
 * @see [StackOverflow: How to answer an invalid ISO8583 message](http://stackoverflow.com/questions/28275677/how-to-answer-an-invalid-iso8583-message)
 */
@Sharable
public open class ParseExceptionHandler(
    private val isoMessageFactory: MessageFactory<IsoMessage>,
    private val includeErrorDetails: Boolean,
) : ChannelInboundHandlerAdapter() {
    @Deprecated("Deprecated in Java")
    @Throws(Exception::class)
    public override fun exceptionCaught(
        ctx: ChannelHandlerContext,
        cause: Throwable,
    ) {
        if (cause is ParseException) {
            val message = createErrorResponseMessage(cause)
            ctx.writeAndFlush(message)
        }
        ctx.fireExceptionCaught(cause)
    }

    @Suppress("MagicNumber")
    protected fun createErrorResponseMessage(cause: ParseException): IsoMessage {
        val message =
            isoMessageFactory.newMessage(
                MessageClass.ADMINISTRATIVE,
                MessageFunction.NOTIFICATION,
                MessageOrigin.OTHER,
            )
        // 650 (Unable to parse message)
        message.setValue(24, 650, IsoType.NUMERIC, 3)
        if (includeErrorDetails) {
            var details = cause.message
            if (details != null && details.length > 25) {
                details = details.take(22) + "..."
            }
            message.setValue(44, details, IsoType.LLVAR, 25)
        }
        return message
    }
}
