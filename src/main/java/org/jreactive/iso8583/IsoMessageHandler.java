package org.jreactive.iso8583;

import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;

public interface IsoMessageHandler {

    int getType();

    void onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage);
}
