package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Handles {@link IsoMessage} s with chain of {@link IsoMessageListener}s.
 */
@ChannelHandler.Sharable
public class CompositeIsoMessageHandler<T extends IsoMessage> extends ChannelInboundHandlerAdapter {

    private final Logger logger = getLogger(CompositeIsoMessageHandler.class);

    private final List<IsoMessageListener<T>> messageListeners = new CopyOnWriteArrayList<>();
    private final boolean failOnError;

    public CompositeIsoMessageHandler(final boolean failOnError) {
        this.failOnError = failOnError;
    }

    public CompositeIsoMessageHandler() {
        this(true);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof IsoMessage) {
            doHandleMessage(ctx, msg);
        }
        super.channelRead(ctx, msg);
    }

    private void doHandleMessage(final ChannelHandlerContext ctx, final Object msg) {
        final T isoMessage;
        try {
            //noinspection unchecked
            isoMessage = (T) msg;
        } catch (final ClassCastException e) {
            logger.debug("IsoMessage subclass {} is not supported by {}. Doing nothing.", msg.getClass(), getClass());
            return;
        }

        var applyNextListener = true;
        final var size = messageListeners.size();
        for (var i = 0; applyNextListener && i < size; i++) {
            final var messageListener = messageListeners.get(i);
            try {
                if (messageListener.applies(isoMessage)) {
                    logger.debug(
                            "Handling IsoMessage[@type=0x{}] with {}",
                            String.format("%04X", isoMessage.getType()), messageListener);
                    applyNextListener = messageListener.onMessage(ctx, isoMessage);
                    if (!applyNextListener) {
                        logger.trace("Stopping further procession of message {} after handler {}", isoMessage, messageListener);
                    }
                }
            } catch (final Exception e) {
                logger.debug("Can't evaluate {}.apply({})", messageListener, isoMessage.getClass(), e);
                if (failOnError) {
                    throw e;
                }
            }
        }
    }

    public void addListener(final IsoMessageListener<T> listener) {
        Objects.requireNonNull(listener, "IsoMessageListener is required");
        messageListeners.add(listener);
    }

    @SuppressWarnings("WeakerAccess")
    @SafeVarargs
    public final void addListeners(final IsoMessageListener<T>... listeners) {
        Objects.requireNonNull(listeners, "IsoMessageListeners must not be null");
        for (final var listener : listeners) {
            addListener(listener);
        }
    }

    public void removeListener(final IsoMessageListener<T> listener) {
        messageListeners.remove(listener);
    }
}
