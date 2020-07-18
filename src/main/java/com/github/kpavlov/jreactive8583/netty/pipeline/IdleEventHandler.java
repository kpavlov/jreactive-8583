package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleEventHandler extends ChannelInboundHandlerAdapter {

    private final MessageFactory<IsoMessage> isoMessageFactory;

    public IdleEventHandler(final MessageFactory<IsoMessage> isoMessageFactory) {
        this.isoMessageFactory = isoMessageFactory;
    }

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) {
        if (evt instanceof IdleStateEvent) {
            final var e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE || e.state() == IdleState.ALL_IDLE) {
                final var echoMessage = createEchoMessage();
                ctx.write(echoMessage);
                ctx.flush();
            }
        }
    }

    private IsoMessage createEchoMessage() {
        return isoMessageFactory.newMessage(0x800);
    }
}
