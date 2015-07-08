package org.jreactive.iso8583;

import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.jreactive.iso8583.netty.pipeline.DispatchingMessageHandler;
import org.jreactive.iso8583.netty.pipeline.EchoMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractIso8583Connector<B extends AbstractBootstrap> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final MessageFactory isoMessageFactory;
    private final DispatchingMessageHandler messageListener;
    private final AtomicReference<Channel> channelRef = new AtomicReference<>();
    private SocketAddress socketAddress;
    private int idleTimeout;
    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;
    private B bootstrap;

    protected AbstractIso8583Connector(MessageFactory isoMessageFactory) {
        this.isoMessageFactory = isoMessageFactory;
        messageListener = new DispatchingMessageHandler();
        messageListener.addIsoMessageHandler(new EchoMessageHandler(isoMessageFactory));
    }


    public void addMessageListener(IsoMessageHandler handler) {
        messageListener.addIsoMessageHandler(handler);
    }


    /**
     * Making connector ready to create a connection / bind to port.
     * Creates a Bootstrap
     *
     * @see AbstractBootstrap
     */
    public void init() {
        logger.info("Initializing");
        bossEventLoopGroup = createBossEventLoopGroup();
        workerEventLoopGroup = createWorkerEventLoopGroup();
        bootstrap = createBootstrap();
    }

    public void shutdown() {
        if (workerEventLoopGroup != null) {
            workerEventLoopGroup.shutdownGracefully();
            workerEventLoopGroup = null;
        }
        if (bossEventLoopGroup != null) {
            bossEventLoopGroup.shutdownGracefully();
            bossEventLoopGroup = null;
        }
    }

    protected void configureBootstrap(AbstractBootstrap bootstrap) {
        bootstrap.option(ChannelOption.TCP_NODELAY,
                Boolean.parseBoolean(System.getProperty(
                        "nfs.rpc.tcp.nodelay", "true")))
                .option(ChannelOption.AUTO_READ, true);
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public MessageFactory getIsoMessageFactory() {
        return isoMessageFactory;
    }

    protected DispatchingMessageHandler getIsoMessageDispatcher() {
        return messageListener;
    }

    public void setIdleTimeout(int heartbeatInterval) {
        this.idleTimeout = heartbeatInterval;
    }

    protected abstract B createBootstrap();

    protected B getBootstrap() {
        return bootstrap;
    }

    protected EventLoopGroup createBossEventLoopGroup() {
        return new NioEventLoopGroup();
    }

    protected EventLoopGroup getBossEventLoopGroup() {
        return bossEventLoopGroup;
    }

    protected EventLoopGroup createWorkerEventLoopGroup() {
        return new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
    }

    protected EventLoopGroup getWorkerEventLoopGroup() {
        return workerEventLoopGroup;
    }

    protected Channel getChannel() {
        return channelRef.get();
    }

    protected void setChannel(Channel channel) {
        this.channelRef.set(channel);
    }
}
