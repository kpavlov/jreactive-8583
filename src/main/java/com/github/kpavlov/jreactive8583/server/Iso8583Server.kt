@file:JvmName("Iso8583Server")

package com.github.kpavlov.jreactive8583.server

import com.github.kpavlov.jreactive8583.AbstractIso8583Connector
import com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer
import com.solab.iso8583.IsoMessage
import com.solab.iso8583.MessageFactory
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.concurrent.GenericFutureListener
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

class Iso8583Server<T : IsoMessage>(port: Int, config: ServerConfiguration, messageFactory: MessageFactory<T>)
    : AbstractIso8583Connector<ServerConfiguration, ServerBootstrap, T>(config, messageFactory) {

    constructor(port: Int, messageFactory: MessageFactory<T>)
            : this(port, ServerConfiguration.newBuilder().build(), messageFactory)

    @Throws(InterruptedException::class)
    fun start() {
        bootstrap.bind().addListener(
                GenericFutureListener { future: ChannelFuture ->
                    channel = future.channel()
                    logger.info("Server is started and listening at {}", channel?.localAddress())
                }
        ).sync().await()
    }

    override fun createBootstrap(): ServerBootstrap {
        val bootstrap = ServerBootstrap()
        bootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                .channel(NioServerSocketChannel::class.java)
                .localAddress(socketAddress)
                .childHandler(Iso8583ChannelInitializer<Channel, ServerBootstrap, ServerConfiguration>(
                        configuration,
                        configurer,
                        workerEventLoopGroup,
                        isoMessageFactory,
                        messageHandler
                ))
        configureBootstrap(bootstrap)
        bootstrap.validate()
        return bootstrap
    }

    override fun shutdown() {
        stop()
        super.shutdown()
    }

    /**
     * @return True if server is ready to accept connections.
     */
    val isStarted: Boolean
        get() {
            return if (channel != null) channel?.isOpen!! else false
        }

    fun stop() {
        val channel = channel ?: throw IllegalStateException("Server is not started.")
        logger.info("Stopping the Server...")
        try {
            channel.deregister()
            channel.close().sync().await(10, TimeUnit.SECONDS)
            logger.info("Server was Stopped.")
        } catch (e: InterruptedException) {
            logger.error("Error while stopping the server", e)
        }
    }

    init {
        socketAddress = InetSocketAddress(port)
    }
}
