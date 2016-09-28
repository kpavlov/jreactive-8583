package org.jreactive.iso8583.server;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import org.jreactive.iso8583.AbstractIso8583Connector;
import org.jreactive.iso8583.netty.pipeline.Iso8583ChannelInitializer;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class Iso8583Server<T extends IsoMessage>  extends AbstractIso8583Connector<ServerConfiguration, ServerBootstrap, T> {

		public Iso8583Server(int port, MessageFactory<T> messageFactory, ServerConfiguration serverConfiguration) {
	    super(serverConfiguration, messageFactory);
	    setSocketAddress(new InetSocketAddress(port));
		}
		
    public Iso8583Server(int port, MessageFactory<T> messageFactory) {
        super(new ServerConfiguration(), messageFactory);
        setSocketAddress(new InetSocketAddress(port));
    }

    public void start() throws InterruptedException {

        getBootstrap().bind().addListener(
                new GenericFutureListener<ChannelFuture>() {

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        final Channel channel = future.channel();
                        setChannel(channel);
                        logger.info("Server is started and listening at {}", channel.localAddress());
                    }
                }
        ).sync().await();
    }

    @Override
    protected ServerBootstrap createBootstrap() {

        final ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(getBossEventLoopGroup(), getWorkerEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .localAddress(getSocketAddress())
                .childHandler(new Iso8583ChannelInitializer<>(
                        getConfiguration(),
                        getConfigurer(),
                        getWorkerEventLoopGroup(),
                        getIsoMessageFactory(),
                        getMessageHandler()
                ));

        configureBootstrap(bootstrap);

        bootstrap.validate();

        return bootstrap;
    }

    public void shutdown() {
        stop();
        super.shutdown();
    }

    /**
     * @return True if server is ready to accept connections.
     */
    public boolean isStarted() {
        final Channel channel = getChannel();
        return channel != null && channel.isOpen();
    }

    public void stop() {
        final Channel channel = getChannel();
        if (channel == null) {
            throw new IllegalStateException("Server is not started.");
        }
        logger.info("Stopping the Server...");
        try {
            channel.deregister();
            channel.close().sync().await(10, TimeUnit.SECONDS);
            logger.info("Server was Stopped.");
        } catch (InterruptedException e) {
            logger.error("Error while stopping the server", e);
        }

    }
}
