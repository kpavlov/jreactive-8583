package com.github.kpavlov.jreactive8583.netty.codec;

import com.solab.iso8583.IsoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class Iso8583Encoder extends MessageToByteEncoder<IsoMessage> {

    private final int lengthHeaderLength;
    private final boolean encodeLengthHeaderAsString;

    public Iso8583Encoder(final int lengthHeaderLength, final boolean encodeLengthHeaderAsString) {
        this.lengthHeaderLength = lengthHeaderLength;
        this.encodeLengthHeaderAsString = encodeLengthHeaderAsString;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final IsoMessage isoMessage, final ByteBuf out) {
        if (lengthHeaderLength == 0) {
            final var bytes = isoMessage.writeData();
            out.writeBytes(bytes);
        } else if (encodeLengthHeaderAsString) {
            final var bytes = isoMessage.writeData();
            final var lengthHeader = String.format("%0" + lengthHeaderLength + "d", bytes.length);
            out.writeBytes(lengthHeader.getBytes(CharsetUtil.US_ASCII));
            out.writeBytes(bytes);
        } else {
            final var byteBuffer = isoMessage.writeToBuffer(lengthHeaderLength);
            out.writeBytes(byteBuffer);
        }
    }
}
