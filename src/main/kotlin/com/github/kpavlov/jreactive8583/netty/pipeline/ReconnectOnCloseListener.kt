package com.github.kpavlov.jreactive8583.netty.pipeline

import com.github.kpavlov.jreactive8583.client.Iso8583Client
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

public open class ReconnectOnCloseListener(
    private val client: Iso8583Client<*>,
    private val reconnectInterval: Int,
    private val executorService: ScheduledExecutorService,
) : ChannelFutureListener {
    private val logger = LoggerFactory.getLogger(ReconnectOnCloseListener::class.java)
    private val disconnectRequested = AtomicBoolean(false)

    public fun requestReconnect() {
        disconnectRequested.set(false)
    }

    public fun requestDisconnect() {
        disconnectRequested.set(true)
    }

    public override fun operationComplete(future: ChannelFuture) {
        val channel = future.channel()
        logger.debug("Client connection was closed to {}", channel.remoteAddress())
        channel.disconnect()
        scheduleReconnect()
    }

    public fun scheduleReconnect() {
        if (!disconnectRequested.get()) {
            logger.trace("Failed to connect. Will try again in {} millis", reconnectInterval)
            executorService.schedule(
                Callable { client.connectAsync() },
                reconnectInterval.toLong(),
                TimeUnit.MILLISECONDS,
            )
        }
    }
}
