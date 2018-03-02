package com.github.kpavlov.jreactive8583;

import com.github.kpavlov.jreactive8583.netty.pipeline.CompositeIsoMessageHandler;
import com.github.kpavlov.jreactive8583.netty.pipeline.EchoMessageListener;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractIso8583Connector<
        C extends ConnectorConfiguration,
        B extends AbstractBootstrap,
        M extends IsoMessage> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final CompositeIsoMessageHandler<M> messageHandler;
    private final MessageFactory<M> isoMessageFactory;
    private final AtomicReference<Channel> channelRef = new AtomicReference<>();
    private final C configuration;
    private ConnectorConfigurer<C, B> configurer;
    private SocketAddress socketAddress;
    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;
    private B bootstrap;

    protected AbstractIso8583Connector(C configuration,
                                       MessageFactory<M> isoMessageFactory,
                                       CompositeIsoMessageHandler<M> messageHandler) {
        assert (configuration != null) : "Configuration must be provided";
        Objects.requireNonNull(isoMessageFactory, "MessageFactory must be provided");
        this.configuration = configuration;
        this.isoMessageFactory = isoMessageFactory;
        this.messageHandler = messageHandler;
        if (configuration.shouldAddEchoMessageListener()) {
            messageHandler.addListener(new EchoMessageListener<>(isoMessageFactory));
        }
    }

    // @VisibleForTest
    protected AbstractIso8583Connector(C configuration, MessageFactory<M> isoMessageFactory) {
        this(configuration, isoMessageFactory, new CompositeIsoMessageHandler<>());
    }

    public void addMessageListener(IsoMessageListener<M> handler) {
        messageHandler.addListener(handler);
    }

    public void removeMessageListener(IsoMessageListener<M> handler) {
        messageHandler.removeListener(handler);
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

    protected void configureBootstrap(B bootstrap) {
        bootstrap.option(ChannelOption.TCP_NODELAY,
                Boolean.parseBoolean(System.getProperty(
                        "nfs.rpc.tcp.nodelay", "true")))
                .option(ChannelOption.AUTO_READ, true);

        if (configurer != null) {
            configurer.configureBootstrap(bootstrap, configuration);
        }
    }

    protected ConnectorConfigurer<C, B> getConfigurer() {
        return configurer;
    }

    public void setConfigurer(ConnectorConfigurer<C, B> connectorConfigurer) {
        this.configurer = connectorConfigurer;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public MessageFactory<M> getIsoMessageFactory() {
        return isoMessageFactory;
    }

    public CompositeIsoMessageHandler<M> getMessageHandler() {
        return messageHandler;
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
        logger.debug("Creating worker EventLoopGroup with thread pool of {} threads", configuration.getWorkerThreadsCount());
        return new NioEventLoopGroup(configuration.getWorkerThreadsCount());
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

    public C getConfiguration() {
        return configuration;
    }
}
