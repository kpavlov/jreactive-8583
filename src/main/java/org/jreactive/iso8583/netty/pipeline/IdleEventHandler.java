package org.jreactive.iso8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleEventHandler extends ChannelHandlerAdapter {

    private final MessageFactory isoMessageFactory;

    public IdleEventHandler(MessageFactory isoMessageFactory) {
        this.isoMessageFactory = isoMessageFactory;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE || e.state() == IdleState.ALL_IDLE) {
                final IsoMessage echoMessage = createEchoMessage();
                ctx.write(echoMessage);
                ctx.flush();
            }
        }
    }

    private IsoMessage createEchoMessage() {
        return isoMessageFactory.newMessage(0x800);
    }
}
