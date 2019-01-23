package com.github.kpavlov.jreactive8583.netty.codec;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.util.HexCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;

import java.nio.ByteBuffer;

import static org.slf4j.LoggerFactory.getLogger;

@ChannelHandler.Sharable
public class Iso8583Encoder extends MessageToByteEncoder<IsoMessage> {

    private final int lengthHeaderLength;
    private final Logger logger = getLogger(Iso8583Encoder.class);

    public Iso8583Encoder(int lengthHeaderLength) {
        this.lengthHeaderLength = lengthHeaderLength;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IsoMessage isoMessage, ByteBuf out) {
        if (lengthHeaderLength == 0) {
            byte[] bytes = isoMessage.writeData();
            logger.debug("Sending hex encoded bytes: {}", HexCodec.hexEncode(bytes, 0, bytes.length));
            out.writeBytes(bytes);
        } else {
            ByteBuffer byteBuffer = isoMessage.writeToBuffer(lengthHeaderLength);
            logger.debug("Sending hex encoded bytes: {}", HexCodec.hexEncode(byteBuffer.array(), 0, byteBuffer.array().length));
            out.writeBytes(byteBuffer);
        }
    }
}
