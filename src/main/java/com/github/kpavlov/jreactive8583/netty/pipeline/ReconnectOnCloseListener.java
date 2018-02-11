package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.slf4j.LoggerFactory.getLogger;

public class ReconnectOnCloseListener implements ChannelFutureListener {

    private final Logger logger = getLogger(ReconnectOnCloseListener.class);

    private final Iso8583Client client;
    private final int reconnectInterval;
    private final AtomicBoolean disconnectRequested = new AtomicBoolean(false);
    private final ScheduledExecutorService executorService;

    public ReconnectOnCloseListener(Iso8583Client client, int reconnectInterval, ScheduledExecutorService executorService) {
        this.client = client;
        this.reconnectInterval = reconnectInterval;
        this.executorService = executorService;
    }

    public void requestReconnect() {
        disconnectRequested.set(false);
    }

    public void requestDisconnect() {
        disconnectRequested.set(true);
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        final Channel channel = future.channel();
        logger.debug("Client connection was closed to {}", channel.remoteAddress());
        channel.disconnect();
        scheduleReconnect();
    }

    public void scheduleReconnect() {
        if (!disconnectRequested.get()) {
            logger.trace("Failed to connect. Will try again in {} millis", reconnectInterval);
            executorService.schedule(
                    client::connectAsync,
                    reconnectInterval, TimeUnit.MILLISECONDS);
        }
    }
}
