@file:JvmName("Iso8583Server")

package com.github.kpavlov.jreactive8583.server

import com.github.kpavlov.jreactive8583.AbstractIso8583Connector
import com.github.kpavlov.jreactive8583.iso.MessageFactory
import com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer
import com.solab.iso8583.IsoMessage
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.concurrent.GenericFutureListener
import java.net.InetSocketAddress

public open class Iso8583Server<T : IsoMessage>(
    port: Int,
    config: ServerConfiguration,
    messageFactory: MessageFactory<T>
) : AbstractIso8583Connector<ServerConfiguration, ServerBootstrap, T>(config, messageFactory) {

    private var socketAddress = InetSocketAddress(port)

    @Throws(InterruptedException::class)
    public fun start() {
        bootstrap.bind().addListener(
            GenericFutureListener { future: ChannelFuture ->
                channel = future.channel()
                logger.info("Server is started and listening at {}", channel?.localAddress())
            }
        ).sync().await()
    }

    override fun createBootstrap(): ServerBootstrap {
        val bootstrap = ServerBootstrap()
        val tcpNoDelay =
            java.lang.Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true"))
        bootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
            .channel(NioServerSocketChannel::class.java)
            .childOption(ChannelOption.TCP_NODELAY, tcpNoDelay)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .localAddress(socketAddress)
            .childHandler(
                Iso8583ChannelInitializer<Channel, ServerBootstrap, ServerConfiguration>(
                    configuration,
                    configurer,
                    workerEventLoopGroup,
                    isoMessageFactory as MessageFactory<IsoMessage>,
                    messageHandler
                )
            )
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
    public val isStarted: Boolean
        get() {
            val channel = channel
            return channel != null && channel.isOpen
        }

    public fun stop() {
        val channel = channel
        if (channel == null) {
            logger.info("The Server is not started...")
            return
        }
        logger.info("Stopping the Server...")
        with(channel) {
            try {
                deregister()
                close().syncUninterruptibly()
                logger.info("Server was Stopped.")
            } catch (e: Exception) {
                logger.error("Error while stopping the server", e)
            }
        }
    }
}
