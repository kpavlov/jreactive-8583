package org.jreactive.iso8583;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jreactive.iso8583.netty.pipeline.Iso8583InitiatorChannelInitializer;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.slf4j.LoggerFactory.getLogger;

public class Iso8583Client extends AbstractIso8583Connector {

    private static final int RECONNECT_TIMEOUT = 100;

    private final Logger logger = getLogger(Iso8583Client.class);
    private final AtomicBoolean disconnectRequested = new AtomicBoolean(false);

    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;

    private volatile Channel channel;

    public Iso8583Client(SocketAddress socketAddress, MessageFactory isoMessageFactory) {
        super(isoMessageFactory);
        setSocketAddress(socketAddress);
    }

    public Iso8583Client(MessageFactory isoMessageFactory) {
        super(isoMessageFactory);
    }

    public ChannelFuture connect() throws InterruptedException {
        connectAsync().sync().await();
        assert (channel != null) : "Channel must be set";
        logger.info("Client is started and connected to {}", channel.remoteAddress());
        final ChannelFuture closeFuture = channel.closeFuture();
        return closeFuture;
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
        connectFuture.addListener(connFuture -> {
            channel = connectFuture.channel();
            channel.closeFuture().addListener(
                    closeFuture -> {
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
        });

        return connectFuture;
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
        final SocketAddress socketAddress = getSocketAddress();
        logger.info("Closing connection to {}", socketAddress);
        return channel.close().addListener(future -> {
            if (workerEventLoopGroup != null) {
                workerEventLoopGroup.shutdownGracefully();
                workerEventLoopGroup = null;
            }
            if (bossEventLoopGroup != null) {
                bossEventLoopGroup.shutdownGracefully();
                bossEventLoopGroup = null;
            }
            logger.info("Connection to {} was closed.", socketAddress);
        });
    }

    public void disconnect() throws InterruptedException {
        disconnectAsync().await();
    }

    /**
     * Sends asynchronously and returns a {@link ChannelFuture}
     */
    public ChannelFuture sendAsync(IsoMessage isoMessage) {
        if (channel == null && !channel.isWritable()) {
            throw new IllegalStateException("Channel is not writable");
        }
        return channel.writeAndFlush(isoMessage);
    }

    public void send(IsoMessage isoMessage) throws InterruptedException {
        sendAsync(isoMessage).await();
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }
}
