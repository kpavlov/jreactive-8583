package org.jreactive.iso8583;

import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import org.jreactive.iso8583.netty.pipeline.DispatchingMessageHandler;
import org.jreactive.iso8583.netty.pipeline.EchoMessageHandler;

import java.net.SocketAddress;

public abstract class AbstractIso8583Connector {

    private final MessageFactory isoMessageFactory;
    private final DispatchingMessageHandler messageListener;
    private SocketAddress socketAddress;
    private int idleTimeout;

    protected AbstractIso8583Connector(MessageFactory isoMessageFactory) {
        this.isoMessageFactory = isoMessageFactory;
        messageListener = new DispatchingMessageHandler();
        messageListener.addIsoMessageHandler(new EchoMessageHandler(isoMessageFactory));
    }

    public void addMessageListener(IsoMessageHandler handler) {
        messageListener.addIsoMessageHandler(handler);
    }

    protected void configureBootstrap(AbstractBootstrap bootstrap) {
        bootstrap.option(ChannelOption.TCP_NODELAY,
                Boolean.parseBoolean(System.getProperty(
                        "nfs.rpc.tcp.nodelay", "true")))
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true));
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    protected MessageFactory getIsoMessageFactory() {
        return isoMessageFactory;
    }

    protected DispatchingMessageHandler getIsoMessageDispatcher() {
        return messageListener;
    }

    public void setIdleTimeout(int heartbeatInterval) {
        this.idleTimeout = heartbeatInterval;
    }
}
