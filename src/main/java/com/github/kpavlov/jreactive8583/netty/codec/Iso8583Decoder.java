package com.github.kpavlov.jreactive8583.netty.codec;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.util.HexCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;

import java.text.ParseException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class Iso8583Decoder extends ByteToMessageDecoder {

    private final MessageFactory messageFactory;
    private final Logger logger = getLogger(Iso8583Decoder.class);

    public Iso8583Decoder(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    /**
     * @implNote Message body starts immediately, no length header,
     * see <code>"lengthFieldFameDecoder"</code> in
     * {@link com.github.kpavlov.jreactive8583.netty.pipeline.Iso8583ChannelInitializer#initChannel}
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List out) throws Exception {
        if (!byteBuf.isReadable()) {
            return;
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        logger.debug("Received hex encoded bytes: {}", HexCodec.hexEncode(bytes, 0, bytes.length));

        final IsoMessage isoMessage = messageFactory.parseMessage(bytes, 0);
        if (isoMessage != null) {
            //noinspection unchecked
            out.add(isoMessage);
        } else {
            throw new ParseException("Can't parse ISO8583 message", 0);
        }
    }
}
