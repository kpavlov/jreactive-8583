package org.jreactive.iso8583.netty.pipeline;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ReconnectHandler extends ChannelHandlerAdapter {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.connect(ctx.channel().remoteAddress());
    }


}
