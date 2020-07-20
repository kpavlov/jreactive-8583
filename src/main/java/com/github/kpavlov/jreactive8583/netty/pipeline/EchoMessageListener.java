package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.iso.MessageClass;
import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 */
public class EchoMessageListener<T extends IsoMessage> implements IsoMessageListener<T> {

    private final MessageFactory<T> isoMessageFactory;

    public EchoMessageListener(final MessageFactory<T> isoMessageFactory) {
        this.isoMessageFactory = isoMessageFactory;
    }

    @Override
    public boolean applies(final IsoMessage isoMessage) {
        return isoMessage != null && (isoMessage.getType() & MessageClass.NETWORK_MANAGEMENT.value()) != 0;
    }

    /**
     * Sends EchoResponse message. Always returns <code>false</code>.
     *
     * @param isoMessage a message to handle
     * @return <code>false</code> - message should not be handled by any other handler.
     */
    @Override
    public boolean onMessage(final ChannelHandlerContext ctx, final T isoMessage) {
        final IsoMessage echoResponse = isoMessageFactory.createResponse(isoMessage);
        ctx.writeAndFlush(echoResponse);
        return false;
    }
}
