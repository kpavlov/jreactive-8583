package com.github.kpavlov.jreactive8583.client;

import com.github.kpavlov.jreactive8583.AbstractIso8583Connector;
import com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer;
import com.github.kpavlov.jreactive8583.netty.pipeline.ReconnectOnCloseListener;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class Iso8583Client<T extends IsoMessage> extends AbstractIso8583Connector<ClientConfiguration, Bootstrap, T> {

    private ReconnectOnCloseListener reconnectOnCloseListener;

    public Iso8583Client(SocketAddress socketAddress, ClientConfiguration config, MessageFactory<T> isoMessageFactory) {
        super(config, isoMessageFactory);
        setSocketAddress(socketAddress);
    }

    public Iso8583Client(SocketAddress socketAddress, MessageFactory<T> isoMessageFactory) {
        this(socketAddress, ClientConfiguration.getDefault(), isoMessageFactory);
    }

    /**
     * @deprecated Use {@link #Iso8583Client(SocketAddress, ClientConfiguration, MessageFactory)}
     *
     * @param isoMessageFactory message factory
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
        final Channel channel = connectAsync().sync().channel();
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
        final Bootstrap b = getBootstrap();
        reconnectOnCloseListener.requestReconnect();
        final ChannelFuture connectFuture = b.connect();
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
        final Bootstrap b = new Bootstrap();
        b.group(getBossEventLoopGroup())
                .channel(NioSocketChannel.class)
                .remoteAddress(getSocketAddress())

                .handler(new Iso8583ChannelInitializer<>(
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

    public ChannelFuture disconnectAsync() {
        reconnectOnCloseListener.requestDisconnect();
        final Channel channel = getChannel();
        if (channel != null) {
            final SocketAddress socketAddress = getSocketAddress();
            logger.info("Closing connection to {}", socketAddress);
            return channel.close();
        } else {
            return null;
        }
    }

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

    public void send(IsoMessage isoMessage) throws InterruptedException {
        sendAsync(isoMessage).sync().await();
    }

    public void send(IsoMessage isoMessage, long timeout, TimeUnit timeUnit) throws InterruptedException {
        sendAsync(isoMessage).sync().await(timeout, timeUnit);
    }

    public boolean isConnected() {
        Channel channel = getChannel();
        return channel != null && channel.isActive();
    }
}
