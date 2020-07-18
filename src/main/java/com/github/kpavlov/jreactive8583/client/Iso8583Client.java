package com.github.kpavlov.jreactive8583.client;

import com.github.kpavlov.jreactive8583.AbstractIso8583Connector;
import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer;
import com.github.kpavlov.jreactive8583.netty.pipeline.ReconnectOnCloseListener;
import com.solab.iso8583.IsoMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class Iso8583Client<T extends IsoMessage> extends AbstractIso8583Connector<ClientConfiguration, Bootstrap, T> {

    @Nullable
    private volatile ReconnectOnCloseListener reconnectOnCloseListener;

    public Iso8583Client(final SocketAddress socketAddress, final ClientConfiguration config, final MessageFactory<T> isoMessageFactory) {
        super(config, isoMessageFactory);
        setSocketAddress(socketAddress);
    }

    public Iso8583Client(final SocketAddress socketAddress, final MessageFactory<T> isoMessageFactory) {
        this(socketAddress, ClientConfiguration.getDefault(), isoMessageFactory);
    }

    /**
     * @param isoMessageFactory message factory
     * @deprecated Use {@link #Iso8583Client(SocketAddress, ClientConfiguration, MessageFactory)}
     */
    @Deprecated
    public Iso8583Client(final MessageFactory<T> isoMessageFactory) {
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
    public ChannelFuture connect(final String host, final int port) throws InterruptedException {
        return connect(new InetSocketAddress(host, port));
    }

    /**
     * Connects synchronously to specified remote address.
     *
     * @param serverAddress A server address to connect to
     * @return {@link ChannelFuture} which will be notified when connection is established.
     * @throws InterruptedException if connection process was interrupted
     */
    public ChannelFuture connect(final SocketAddress serverAddress) throws InterruptedException {
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
        final var reconnectListener = getReconnectOnCloseListener();
        if (reconnectListener != null) {
            reconnectListener.requestReconnect();
        }
        final var connectFuture = b.connect();
        connectFuture.addListener(connFuture -> {
            if (!connectFuture.isSuccess() && reconnectListener != null) {
                reconnectListener.scheduleReconnect();
                return;
            }
            final var channel = connectFuture.channel();
            logger.debug("Client is connected to {}", channel.remoteAddress());
            setChannel(channel);
            if (reconnectListener != null) {
                channel.closeFuture().addListener(reconnectListener);
            }
        });

        return connectFuture;
    }

    @Override
    protected Bootstrap createBootstrap() {
        final var b = new Bootstrap();

        final var tcpNoDelay = Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true"));

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
        final var reconnectListener = getReconnectOnCloseListener();
        if (reconnectListener != null) {
            reconnectListener.requestDisconnect();
        }
        final var channel = getChannel();
        if (channel != null) {
            final var socketAddress = getSocketAddress();
            logger.info("Closing connection to {}", socketAddress);
            return channel.close();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public void disconnect() throws InterruptedException {
        final var disconnectFuture = disconnectAsync();
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
    public ChannelFuture sendAsync(final IsoMessage isoMessage) {
        final var channel = getChannel();
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
     *
     * @param isoMessage A message to send
     * @throws InterruptedException When sending was interrupted
     */
    @SuppressWarnings("unused")
    public void send(final IsoMessage isoMessage) throws InterruptedException {
        sendAsync(isoMessage).sync().await();
    }

    /**
     * Sends message synchronously with timeout
     *
     * @param isoMessage A message to send
     * @param timeout    timeout in timeUnits
     * @param timeUnit   a time unit for timeout
     * @throws InterruptedException When sending was interrupted
     */
    @SuppressWarnings("unused")
    public void send(final IsoMessage isoMessage, final long timeout, final TimeUnit timeUnit) throws InterruptedException {
        sendAsync(isoMessage).sync().await(timeout, timeUnit);
    }

    public boolean isConnected() {
        final var channel = getChannel();
        return channel != null && channel.isActive();
    }

    @Override
    public void shutdown() {
        final var future = disconnectAsync();
        if (future != null) {
            future.syncUninterruptibly();
        }
        super.shutdown();
    }

    @Nullable
    protected ReconnectOnCloseListener getReconnectOnCloseListener() {
        return reconnectOnCloseListener;
    }
}
