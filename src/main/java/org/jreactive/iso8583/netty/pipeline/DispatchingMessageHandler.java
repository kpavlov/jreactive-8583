package org.jreactive.iso8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.jreactive.iso8583.IsoMessageHandler;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.getLogger;

@ChannelHandler.Sharable
public class DispatchingMessageHandler extends ChannelHandlerAdapter {

    private final Logger logger = getLogger(DispatchingMessageHandler.class);

    private final Map<Integer, IsoMessageHandler> messageListeners = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof IsoMessage) {
            IsoMessage isoMessage = (IsoMessage) msg;
            final int type = isoMessage.getType();
            final IsoMessageHandler isoMessageHandler = messageListeners.get(type);
            if (isoMessageHandler != null) {
                logger.debug("Handling IsoMessage[@type={}] with {}", type, isoMessageHandler);
                isoMessageHandler.onMessage(ctx, isoMessage);
                return;
            } else {
                logger.debug("No handler is registered for IsoMessage[@type={}]", type);
            }
        }

        super.channelRead(ctx, msg);
    }

    public void addIsoMessageHandler(IsoMessageHandler handler) {
        Objects.requireNonNull(handler);
        final int type = handler.getType();
        if (messageListeners.containsKey(type)) {
            throw new IllegalStateException("Can't register two handlers for message type: "+type);
        }
        messageListeners.put(type, handler);
    }
}
