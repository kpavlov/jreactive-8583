package org.jreactive.iso8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.channel.ChannelHandlerContext;
import org.jreactive.iso8583.IsoMessageHandler;

public class EchoMessageHandler implements IsoMessageHandler {

    private final MessageFactory isoMessageFactory;

    public EchoMessageHandler(MessageFactory isoMessageFactory) {
        this.isoMessageFactory = isoMessageFactory;
    }

    @Override
    public int getType() {
        return 0x800;
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
        final IsoMessage echoResponse = isoMessageFactory.createResponse(isoMessage);
        ctx.writeAndFlush(echoResponse);
    }
}
