package com.github.kpavlov.jreactive8583.client;

import com.github.kpavlov.jreactive8583.AbstractIso8583Connector;
import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer;
import com.github.kpavlov.jreactive8583.netty.pipeline.ReconnectOnCloseListener;
import com.solab.iso8583.IsoMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class Iso8583Client<T extends IsoMessage> extends AbstractIso8583Connector<ClientConfiguration, Bootstrap, T> {

    @Nullable
    private ReconnectOnCloseListener reconnectOnCloseListener;

    public Iso8583Client(SocketAddress socketAddress, ClientConfiguration config, MessageFactory<T> isoMessageFactory) {
        super(config, isoMessageFactory);
        setSocketAddress(socketAddress);
    }

    public Iso8583Client(SocketAddress socketAddress, MessageFactory<T> isoMessageFactory) {
        this(socketAddress, ClientConfiguration.getDefault(), isoMessageFactory);
    }

    /**
     * @param isoMessageFactory message factory
     * @deprecated Use {@link #Iso8583Client(SocketAddress, ClientConfiguration, MessageFactory)}
     */
    @Deprecated
    public Iso8583Client(MessageFactory<T> isoMessageFactory) {
        super(ClientConfiguration.getDefault(), isoMessageFactory);
    }

    /**
     * Connects synchronously to remote address.
     *
     * @return Returns the {@link ChannelFuture} which will be notified when this
     * channel is closed.
     * @throws InterruptedException if connection process was interrupted
     * @see #setSocketAddress(SocketAddress)
     */
    public ChannelFuture connect() throws InterruptedException {
        final var channel = connectAsync().sync().channel();
        assert (channel != null) : "Channel must be set";
        setChannel(channel);
        return channel.closeFuture();
    }

    /**
     * Connect synchronously to  specified host and port.
     *
     * @param host A server host to connect to
     * @param port A server port to connect to
     * @return {@link ChannelFuture} which will be notified when connection is established.
     * @throws InterruptedException if connection process was interrupted
     */
    @SuppressWarnings("unused")
    public ChannelFuture connect(String host, int port) throws InterruptedException {
        return connect(new InetSocketAddress(host, port));
    }

    /**
     * Connects synchronously to specified remote address.
     *
     * @param serverAddress A server address to connect to
     * @return {@link ChannelFuture} which will be notified when connection is established.
     * @throws InterruptedException if connection process was interrupted
     */
    public ChannelFuture connect(SocketAddress serverAddress) throws InterruptedException {
        setSocketAddress(serverAddress);
        return connect().sync();
    }

    /**
     * Connects asynchronously to remote address.
     *
     * @return Returns the {@link ChannelFuture} which will be notified when this
     * channel is active.
     */
    public ChannelFuture connectAsync() {
        logger.debug("Connecting to {}", getSocketAddress());
        final var b = getBootstrap();
        if (reconnectOnCloseListener != null) {
            reconnectOnCloseListener.requestReconnect();
        }
        final var connectFuture = b.connect();
        connectFuture.addListener(connFuture -> {
            if (!connectFuture.isSuccess()) {
                reconnectOnCloseListener.scheduleReconnect();
                return;
            }
            Channel channel = connectFuture.channel();
            logger.debug("Client is connected to {}", channel.remoteAddress());
            setChannel(channel);
            channel.closeFuture().addListener(reconnectOnCloseListener);
        });

        return connectFuture;
    }

    @Override
    protected Bootstrap createBootstrap() {
        final var b = new Bootstrap();

        final boolean tcpNoDelay = Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true"));

        b.group(getBossEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, tcpNoDelay)
                .remoteAddress(getSocketAddress())

                .handler(new Iso8583ChannelInitializer(
                        getConfiguration(),
                        getConfigurer(),
                        getWorkerEventLoopGroup(),
                        getIsoMessageFactory(),
                        getMessageHandler()
                ));

        configureBootstrap(b);

        b.validate();

        reconnectOnCloseListener = new ReconnectOnCloseListener(this,
                getConfiguration().getReconnectInterval(),
                getBossEventLoopGroup()
        );

        return b;
    }

    @Nullable
    public ChannelFuture disconnectAsync() {
        if (reconnectOnCloseListener != null) {
            reconnectOnCloseListener.requestDisconnect();
        }
        final Channel channel = getChannel();
        if (channel != null) {
            final SocketAddress socketAddress = getSocketAddress();
            logger.info("Closing connection to {}", socketAddress);
            return channel.close();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public void disconnect() throws InterruptedException {
        final ChannelFuture disconnectFuture = disconnectAsync();
        if (disconnectFuture != null) {
            disconnectFuture.await();
        }
    }

    /**
     * Sends asynchronously and returns a {@link ChannelFuture}
     *
     * @param isoMessage A message to send
     * @return ChannelFuture which will be notified when message is sent
     */
    public ChannelFuture sendAsync(IsoMessage isoMessage) {
        Channel channel = getChannel();
        if (channel == null) {
            throw new IllegalStateException("Channel is not opened");
        }
        if (!channel.isWritable()) {
            throw new IllegalStateException("Channel is not writable");
        }
        return channel.writeAndFlush(isoMessage);
    }

    /**
     * Sends message synchronously
     */
    @SuppressWarnings("unused")
    public void send(IsoMessage isoMessage) throws InterruptedException {
        sendAsync(isoMessage).sync().await();
    }

    /**
     * Sends message synchronously with timeout
     */
    @SuppressWarnings("unused")
    public void send(IsoMessage isoMessage, long timeout, TimeUnit timeUnit) throws InterruptedException {
        sendAsync(isoMessage).sync().await(timeout, timeUnit);
    }

    public boolean isConnected() {
        Channel channel = getChannel();
        return channel != null && channel.isActive();
    }

    @Override
    public void shutdown() {
        final ChannelFuture future = disconnectAsync();
        if (future != null) {
            future.syncUninterruptibly();
        }
        super.shutdown();
    }
}
