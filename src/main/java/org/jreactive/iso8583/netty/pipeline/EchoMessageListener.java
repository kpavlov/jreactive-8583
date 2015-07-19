package org.jreactive.iso8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.channel.ChannelHandlerContext;
import org.jreactive.iso8583.IsoMessageListener;

/**
 *
 */
public class EchoMessageListener<T extends IsoMessage> implements IsoMessageListener<T> {

    private final MessageFactory<T> isoMessageFactory;

    public EchoMessageListener(MessageFactory<T> isoMessageFactory) {
        this.isoMessageFactory = isoMessageFactory;
    }

    @Override
    public boolean applies(IsoMessage isoMessage) {
        return isoMessage != null && isoMessage.getType() == 0x800;
    }

    /**
     * Sends EchoResponse message. Always returns <code>false</code>.
     *
     * @param isoMessage a message to handle
     * @return <code>false</code> - message should not be handled by any other handler.
     */
    @Override
    public boolean onMessage(ChannelHandlerContext ctx, T isoMessage) {
        final IsoMessage echoResponse = isoMessageFactory.createResponse(isoMessage);
        ctx.writeAndFlush(echoResponse);
        return false;
    }
}
