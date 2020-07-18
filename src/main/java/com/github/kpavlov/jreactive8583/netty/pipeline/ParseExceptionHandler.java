package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.annotation.Nonnull;
import java.text.ParseException;

/**
 * Handles {@link ParseException}s and responds with administrative message
 *
 * @see <a href="http://stackoverflow.com/questions/28275677/how-to-answer-an-invalid-iso8583-message">StackOverflow: How to answer an invalid ISO8583 message</a>
 */
@ChannelHandler.Sharable
public class ParseExceptionHandler extends ChannelInboundHandlerAdapter {

    private final MessageFactory<IsoMessage> isoMessageFactory;

    private final boolean includeErrorDetails;

    public ParseExceptionHandler(@Nonnull MessageFactory<IsoMessage> isoMessageFactory,
                                 boolean includeErrorDetails) {
        this.isoMessageFactory = isoMessageFactory;
        this.includeErrorDetails = includeErrorDetails;
    }

    @Override
    public void exceptionCaught(@Nonnull ChannelHandlerContext ctx,
                                @Nonnull Throwable cause) throws Exception {
        if (cause instanceof ParseException) {
            final IsoMessage message = createErrorResponseMessage((ParseException) cause);
            ctx.writeAndFlush(message);
        }
        super.exceptionCaught(ctx, cause);
    }

    protected IsoMessage createErrorResponseMessage(ParseException cause) {
        final IsoMessage message = isoMessageFactory.newMessage(0x1644);
        message.setValue(24, 650, IsoType.NUMERIC, 3); //650 (Unable to parse message)
        if (includeErrorDetails) {
            String details = cause.getMessage();
            if (details.length() > 25) {
                details = details.substring(0, 22) + "...";
            }
            message.setValue(44, details, IsoType.LLVAR, 25);
        }
        return message;
    }
}
