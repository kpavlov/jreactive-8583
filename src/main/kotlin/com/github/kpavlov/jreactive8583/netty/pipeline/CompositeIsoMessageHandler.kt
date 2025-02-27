package com.github.kpavlov.jreactive8583.netty.pipeline

import com.github.kpavlov.jreactive8583.IsoMessageListener
import com.solab.iso8583.IsoMessage
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList

/** Handles [IsoMessage] s with chain of [IsoMessageListener]s. */
@Sharable
public open class CompositeIsoMessageHandler<T : IsoMessage>
    @JvmOverloads
    constructor(
        private val failOnError: Boolean = true,
    ) : ChannelInboundHandlerAdapter() {
        private val logger = LoggerFactory.getLogger(CompositeIsoMessageHandler::class.java)

        private val messageListeners: MutableList<IsoMessageListener<T>> = CopyOnWriteArrayList()

        @Throws(Exception::class)
        public override fun channelRead(
            ctx: ChannelHandlerContext,
            msg: Any,
        ) {
            val isoMessage =
                try {
                    msg as? T
                } catch (e: ClassCastException) {
                    logger.debug(
                        "IsoMessage subclass {} is not supported by {}. Doing nothing.",
                        msg.javaClass,
                        javaClass,
                        e,
                    )
                    return
                }
            isoMessage?.let { doHandleMessage(ctx, it) }
            super.channelRead(ctx, msg)
        }

        protected fun doHandleMessage(
            ctx: ChannelHandlerContext,
            isoMessage: T,
        ) {
            var applyNextListener = true
            val size = messageListeners.size
            var i = 0
            while (applyNextListener && i < size) {
                val messageListener = messageListeners[i]
                applyNextListener =
                    handleWithMessageListener(
                        messageListener,
                        isoMessage,
                        ctx,
                    )
                if (!applyNextListener) {
                    logger.trace(
                        "Stopping further procession of message {} after handler {}",
                        isoMessage,
                        messageListener,
                    )
                }
                i++
            }
        }

        @Suppress("TooGenericExceptionCaught")
        protected fun handleWithMessageListener(
            messageListener: IsoMessageListener<T>,
            isoMessage: T,
            ctx: ChannelHandlerContext,
        ): Boolean {
            try {
                if (messageListener.applies(isoMessage)) {
                    logger.debug(
                        "Handling IsoMessage[@type=0x{}] with {}",
                        "%04X".format(isoMessage.type),
                        messageListener,
                    )
                    return messageListener.onMessage(ctx, isoMessage)
                }
            } catch (e: Exception) {
                logger.debug(
                    "Can't evaluate {}.apply({})",
                    messageListener,
                    isoMessage.javaClass,
                    e,
                )
                if (failOnError) {
                    throw e
                }
            }
            return true
        }

        public fun addListener(listener: IsoMessageListener<T>) {
            messageListeners.add(listener)
        }

        @SafeVarargs
        public fun addListeners(vararg listeners: IsoMessageListener<T>) {
            for (listener in listeners) {
                addListener(listener)
            }
        }

        public fun removeListener(listener: IsoMessageListener<T>) {
            messageListeners.remove(listener)
        }
    }
