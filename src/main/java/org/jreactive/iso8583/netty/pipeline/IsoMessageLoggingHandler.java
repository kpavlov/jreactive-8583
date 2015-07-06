package org.jreactive.iso8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoValue;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@ChannelHandler.Sharable
public class IsoMessageLoggingHandler extends LoggingHandler {

    public IsoMessageLoggingHandler(LogLevel level) {
        super(level);
    }

    public static String formatIsoMessage(IsoMessage m) {
        StringBuilder sb = new StringBuilder();
        sb.append("Message: ").append(m.debugString()).append("\n");
        sb.append("TYPE: ").append(m.getType());
        for (int i = 2; i < 128; i++) {
            if (m.hasField(i)) {
                final IsoValue<Object> field = m.getField(i);
                final Object objectValue = m.getObjectValue(i);
                sb.append("F: ").append(i)
                        .append("(").append(field.getType()).append("):")
                        .append(String.valueOf(objectValue))
                        .append(" -> '").append(field.toString()).append("'\n");

            }
        }
        return sb.toString();
    }

    @Override
    protected String format(ChannelHandlerContext ctx, String eventName, Object arg) {
        if (arg instanceof IsoMessage) {
            return formatIsoMessage((IsoMessage) arg);
        } else {
            return "";
        }
    }
}
