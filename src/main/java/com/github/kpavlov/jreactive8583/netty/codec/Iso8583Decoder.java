package com.github.kpavlov.jreactive8583.netty.codec;

import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.solab.iso8583.IsoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.text.ParseException;
import java.util.List;

public class Iso8583Decoder extends ByteToMessageDecoder {

    private final MessageFactory<IsoMessage> messageFactory;

    public Iso8583Decoder(final MessageFactory<IsoMessage> messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * Decodes ISO8583 message from {@link ByteBuf}.
     * <p>
     * Message body starts immediately, no length header,
     * see <code>"lengthFieldFrameDecoder"</code> in
     * {@link com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer#initChannel(Channel)}
     * </p>
     */
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf byteBuf, final List out) throws Exception {
        if (!byteBuf.isReadable()) {
            return;
        }
        final var bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        final var isoMessage = messageFactory.parseMessage(bytes, 0);
        if (isoMessage != null) {
            //noinspection unchecked
            out.add(isoMessage);
        } else {
            throw new ParseException("Can't parse ISO8583 message", 0);
        }
    }
}
