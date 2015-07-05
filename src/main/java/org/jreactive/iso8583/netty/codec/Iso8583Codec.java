package org.jreactive.iso8583.netty.codec;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class Iso8583Codec extends ByteToMessageCodec<IsoMessage> {

    private final MessageFactory messageFactory;

    public Iso8583Codec(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List list) throws Exception {
        //message body starts immediately, no length header
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        final IsoMessage isoMessage = messageFactory.parseMessage(bytes, 0);
        if (isoMessage != null) {
            //noinspection unchecked
            list.add(isoMessage);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IsoMessage isoMessage, ByteBuf out) throws Exception {
        final byte[] bytes = isoMessage.writeData();
        out.writeBytes(bytes);
        ctx.flush();
    }
}
