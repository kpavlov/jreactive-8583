package com.github.kpavlov.jreactive8583.netty.pipeline

import com.github.kpavlov.jreactive8583.IsoMessageListener
import com.solab.iso8583.IsoMessage
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Handles [IsoMessage] s with chain of [IsoMessageListener]s.
 */
@Sharable
class CompositeIsoMessageHandler<T : IsoMessage>
@JvmOverloads constructor(private val failOnError: Boolean = true) : ChannelInboundHandlerAdapter() {
    private val logger = LoggerFactory.getLogger(CompositeIsoMessageHandler::class.java)
    private val messageListeners: MutableList<IsoMessageListener<T>> = CopyOnWriteArrayList()

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        (msg as? T)?.let { doHandleMessage(ctx, it) }
        super.channelRead(ctx, msg)
    }

    private fun doHandleMessage(ctx: ChannelHandlerContext, isoMessage: T) {
        var applyNextListener = true
        val size = messageListeners.size
        var i = 0
        while (applyNextListener && i < size) {
            val messageListener = messageListeners[i]
            try {
                if (messageListener.applies(isoMessage)) {
                    logger.debug("Handling IsoMessage[@type=0x{}] with {}", String.format("%04X", isoMessage.type), messageListener)
                    applyNextListener = messageListener.onMessage(ctx, isoMessage)
                    if (!applyNextListener) {
                        logger.trace("Stopping further procession of message {} after handler {}", isoMessage, messageListener)
                    }
                }
            } catch (e: Exception) {
                logger.debug("Can't evaluate {}.apply({})", messageListener, isoMessage.javaClass, e)
                if (failOnError) {
                    throw e
                }
            }
            i++
        }
    }

    fun addListener(listener: IsoMessageListener<T>) {
        Objects.requireNonNull(listener, "IsoMessageListener is required")
        messageListeners.add(listener)
    }

    @SafeVarargs
    fun addListeners(vararg listeners: IsoMessageListener<T>) {
        Objects.requireNonNull(listeners, "IsoMessageListeners must not be null")
        for (listener in listeners) {
            addListener(listener)
        }
    }

    fun removeListener(listener: IsoMessageListener<T>) {
        messageListeners.remove(listener)
    }

}
