package org.jreactive.iso8583;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jreactive.iso8583.netty.pipeline.Iso8583InitiatorChannelInitializer;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.slf4j.LoggerFactory.getLogger;

public class Netty8583Client extends AbstractIso8583Connector {

    private static final int RECONNECT_TIMEOUT = 100;

    private final Logger logger = getLogger(Netty8583Client.class);
    private final AtomicBoolean disconnectRequested = new AtomicBoolean(false);

    private MultithreadEventLoopGroup bossEventLoopGroup;
    private MultithreadEventLoopGroup workerEventLoopGroup;
    private Channel channel;

    public Netty8583Client(SocketAddress socketAddress, MessageFactory isoMessageFactory) {
        super(isoMessageFactory);
        setSocketAddress(socketAddress);
    }

    public Netty8583Client(MessageFactory isoMessageFactory) {
        super(isoMessageFactory);
    }

    public ChannelFuture connect() throws InterruptedException {
        final Channel channel = connectAsync().sync().await().channel();
        assert (channel != null) : "Channel must be set";
        logger.info("Client is started and connected to {}", channel.remoteAddress());
        final ChannelFuture closeFuture = channel.closeFuture();
        return channel.closeFuture();
    }

    /**
     * Connect and start FIX session to specified host and port.
     */

    public ChannelFuture connect(String host, int port) throws InterruptedException {
        setSocketAddress(new InetSocketAddress(host, port));
        return connect();
    }

    public ChannelFuture connect(SocketAddress serverAddress) throws InterruptedException {
        setSocketAddress(serverAddress);
        return connect();
    }

    public ChannelFuture connectAsync() {
        logger.info("Client is starting");
        final Bootstrap b = createBootstrap();

        final ChannelFuture connectFuture = b.connect();

        connectFuture.channel().closeFuture().addListener(
                future -> {
                    if (!disconnectRequested.get() && !isConnected()) {
                        Thread.sleep(RECONNECT_TIMEOUT);
                        try {
                            connect();
                        } catch (Exception e) {
                            logger.trace("Failed to reconnect. Will try in {} msec", RECONNECT_TIMEOUT);
                        }
                    }
                }
        );
        return connectFuture.addListener(future -> channel = connectFuture.channel());
    }

    private Bootstrap createBootstrap() {
        bossEventLoopGroup = new NioEventLoopGroup();
        workerEventLoopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        final Bootstrap b = new Bootstrap();
        b.group(bossEventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(getSocketAddress())

                .handler(new Iso8583InitiatorChannelInitializer<>(
                        workerEventLoopGroup,
                        getIsoMessageFactory(),
                        getIsoMessageDispatcher()
                ));

        configureBootstrap(b);

        b.validate();
        return b;
    }

    public ChannelFuture disconnectAsync() {
        disconnectRequested.set(true);
        logger.info("Closing connection to {}", channel.remoteAddress());
        return channel.close().addListener(future -> {
            if (workerEventLoopGroup != null)
                workerEventLoopGroup.shutdownGracefully();
            if (bossEventLoopGroup != null)
                bossEventLoopGroup.shutdownGracefully();
            bossEventLoopGroup = null;
            workerEventLoopGroup = null;
            logger.info("Connection to {} was closed.", channel.remoteAddress());
        });
    }

    public void disconnect() throws InterruptedException {
        disconnectAsync().await();
    }

    public void send(IsoMessage isoMessage) {
        channel.writeAndFlush(isoMessage);
    }


    public boolean isConnected() {
        return channel != null && channel.isActive();
    }
}
