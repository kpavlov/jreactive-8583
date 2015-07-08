package org.jreactive.iso8583.netty.codec;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class Iso8583Decoder extends ByteToMessageDecoder {

    private final MessageFactory messageFactory;

    public Iso8583Decoder(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List out) throws Exception {
        //message body starts immediately, no length header
        if (!byteBuf.isReadable()) {
            return;
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        final IsoMessage isoMessage = messageFactory.parseMessage(bytes, 0);
        if (isoMessage != null) {
            //noinspection unchecked
            out.add(isoMessage);
        }
    }
}
