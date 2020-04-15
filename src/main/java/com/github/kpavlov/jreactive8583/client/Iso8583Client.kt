@file:JvmName("Iso8583Client")

package com.github.kpavlov.jreactive8583.client

import com.github.kpavlov.jreactive8583.AbstractIso8583Connector
import com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer
import com.github.kpavlov.jreactive8583.netty.pipeline.ReconnectOnCloseListener
import com.solab.iso8583.IsoMessage
import com.solab.iso8583.MessageFactory
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.concurrent.TimeUnit

class Iso8583Client<T : IsoMessage> : AbstractIso8583Connector<ClientConfiguration, Bootstrap, T> {
    private lateinit var reconnectOnCloseListener: ReconnectOnCloseListener

    constructor(socketAddress: SocketAddress, config: ClientConfiguration, isoMessageFactory: MessageFactory<T>)
            : super(config, isoMessageFactory) {
        this.socketAddress = socketAddress
    }

    constructor(socketAddress: SocketAddress, isoMessageFactory: MessageFactory<T>)
            : this(socketAddress, ClientConfiguration.getDefault(), isoMessageFactory)

    /**
     * Connects synchronously to remote address.
     *
     * @return Returns the [ChannelFuture] which will be notified when this
     * channel is closed.
     * @throws InterruptedException if connection process was interrupted
     * @see .setSocketAddress
     */
    @Throws(InterruptedException::class)
    fun connect(): ChannelFuture {
        val channel = connectAsync().sync().channel() ?: error("Channel must be set")
        return channel.closeFuture()
    }

    /**
     * Connect synchronously to  specified host and port.
     *
     * @param host A server host to connect to
     * @param port A server port to connect to
     * @return [ChannelFuture] which will be notified when connection is established.
     * @throws InterruptedException if connection process was interrupted
     */
    @Throws(InterruptedException::class)
    fun connect(host: String, port: Int): ChannelFuture {
        return connect(InetSocketAddress(host, port))
    }

    /**
     * Connects synchronously to specified remote address.
     *
     * @param serverAddress A server address to connect to
     * @return [ChannelFuture] which will be notified when connection is established.
     * @throws InterruptedException if connection process was interrupted
     */
    @Throws(InterruptedException::class)
    fun connect(serverAddress: SocketAddress): ChannelFuture {
        socketAddress = serverAddress
        return connect().sync()
    }

    /**
     * Connects asynchronously to remote address.
     *
     * @return Returns the [ChannelFuture] which will be notified when this
     * channel is active.
     */
    fun connectAsync(): ChannelFuture {
        logger.debug("Connecting to {}", socketAddress)
        val b = bootstrap
        reconnectOnCloseListener.requestReconnect()
        val connectFuture = b.connect()
        connectFuture.addListener {
            if (!connectFuture.isSuccess) {
                reconnectOnCloseListener.scheduleReconnect()
                return@addListener
            }
            channel = with(connectFuture.channel()!!) {
                logger.debug("Client is connected to {}", this.remoteAddress())
                this.closeFuture().addListener(reconnectOnCloseListener)
                this
            }

        }
        return connectFuture
    }

    override fun createBootstrap(): Bootstrap {
        val b = Bootstrap()
        b.group(bossEventLoopGroup)
                .channel(NioSocketChannel::class.java)
                .remoteAddress(socketAddress)
                .handler(Iso8583ChannelInitializer<Channel, Bootstrap, ClientConfiguration>(
                        configuration,
                        configurer,
                        workerEventLoopGroup,
                        isoMessageFactory,
                        messageHandler
                ))
        configureBootstrap(b)
        b.validate()
        reconnectOnCloseListener = ReconnectOnCloseListener(this,
                configuration.reconnectInterval,
                bossEventLoopGroup
        )
        return b
    }

    fun disconnectAsync(): ChannelFuture {
        reconnectOnCloseListener.requestDisconnect()
        val channel = channel
        val socketAddress = socketAddress
        logger.info("Closing connection to {}", socketAddress)
        return channel.close()
    }

    @Throws(InterruptedException::class)
    fun disconnect() {
        val disconnectFuture = disconnectAsync()
        disconnectFuture.await()
    }

    /**
     * Sends asynchronously and returns a [ChannelFuture]
     *
     * @param isoMessage A message to send
     * @return ChannelFuture which will be notified when message is sent
     */
    fun sendAsync(isoMessage: IsoMessage?): ChannelFuture {
        val channel = channel
        check(channel.isWritable) { "Channel is not writable" }
        return channel.writeAndFlush(isoMessage)
    }

    /**
     * Sends message synchronously
     */
    @Throws(InterruptedException::class)
    fun send(isoMessage: IsoMessage?) {
        sendAsync(isoMessage).sync().await()
    }

    /**
     * Sends message synchronously with timeout
     */
    @Throws(InterruptedException::class)
    fun send(isoMessage: IsoMessage?, timeout: Long, timeUnit: TimeUnit?) {
        sendAsync(isoMessage).sync().await(timeout, timeUnit)
    }

    val isConnected: Boolean
        get() {
            val channel = channel
            return channel != null && channel.isActive
        }
}
