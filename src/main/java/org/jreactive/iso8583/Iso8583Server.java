package org.jreactive.iso8583;

import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.jreactive.iso8583.netty.pipeline.Iso8583AcceptorChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class Iso8583Server extends AbstractIso8583Connector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iso8583Server.class);
    private Channel channel;
    private MultithreadEventLoopGroup bossGroup;
    private MultithreadEventLoopGroup workerGroup;

    public Iso8583Server(int port, MessageFactory messageFactory) {
        super(messageFactory);
        setSocketAddress(new InetSocketAddress(port));
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        final ServerBootstrap bootstrap = new ServerBootstrap();
        final Iso8583AcceptorChannelInitializer<SocketChannel> channelInitializer = new Iso8583AcceptorChannelInitializer<>(
                workerGroup,
                getIsoMessageFactory(),
                getIsoMessageDispatcher()
        );

        configureBootstrap(bootstrap);

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(getSocketAddress())
                .childHandler(channelInitializer);

        bootstrap.validate();

        ChannelFuture f = bootstrap.bind().sync();
        LOGGER.info("Server is started and listening at {}", f.channel().localAddress());
        channel = f.channel();
    }

    public void shutdown() {
        if (channel == null) {
            throw new IllegalStateException("Server is not started.");
        }
        LOGGER.info("Stopping the Server");
        try {
            channel.close().sync();
            channel = null;
        } catch (InterruptedException e) {
            LOGGER.error("Error while stopping the server", e);
        } finally {
            bossGroup.shutdownGracefully();
            bossGroup = null;
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
    }

    /**
     * @return True if server is ready to accept connections.
     */
    public boolean isStarted() {
        return channel != null && channel.isOpen();
    }
}
