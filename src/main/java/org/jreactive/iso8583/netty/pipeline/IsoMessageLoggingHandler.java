package org.jreactive.iso8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoValue;
import io.netty.channel.ChannelHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ChannelHandler responsible for logging messages.
 * <p>
 * According to PCI DSS, sensitive cardholder data should not be exposed.
 * When running in secure mode, ensitive cardholder data will be printed masked.
 */
@ChannelHandler.Sharable
public class IsoMessageLoggingHandler extends LoggingHandler {

    private static String[] FIELD_NAMES = new String[128];

    static {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/jreactive/iso8583/iso8583fields.properties")) {
            final Properties properties = new Properties();
            properties.load(stream);
            properties.forEach((key, value) -> {
                int field = Integer.parseInt((String) key);
                FIELD_NAMES[field - 1] = (String) value;
            });
        } catch (IOException | NumberFormatException e) {
            throw new Error("Unable to load ISO8583 field descriptions", e);
        }
    }

    private final boolean printSensitiveData;
    private final boolean printFieldDescriptions;


    public IsoMessageLoggingHandler(LogLevel level, boolean printSensitiveData, boolean printFieldDescriptions) {
        super(level);
        this.printSensitiveData = printSensitiveData;
        this.printFieldDescriptions = printFieldDescriptions;
    }

    public IsoMessageLoggingHandler(LogLevel level) {
        this(level, true, true);
    }

    @Override
    protected String formatMessage(String eventName, Object msg) {
        if (msg instanceof IsoMessage) {
            return formatIsoMessage((IsoMessage) msg);
        } else {
            return super.formatMessage(eventName, msg);
        }
    }

    private String formatIsoMessage(IsoMessage m) {
        StringBuilder sb = new StringBuilder();
        if (printSensitiveData) {
            sb.append("Message: ").append(m.debugString()).append("\n");
        }
        sb.append("MTI: 0x").append(String.format("%04x", m.getType()));
        for (int i = 2; i < 128; i++) {
            if (m.hasField(i)) {
                final IsoValue<Object> field = m.getField(i);
                final Object objectValue = m.getObjectValue(i);
                sb.append("\n  ").append(i)
                        .append(": [");

                if (printFieldDescriptions) {
                    sb.append(FIELD_NAMES[i - 1]).append(':');
                }
                sb.append(field.getType()).append('(').append(field.getLength())
                        .append(")] = ")
                        .append(String.valueOf(objectValue))
                        .append(" -> '").append(field.toString()).append('\'');

            }
        }
        return sb.toString();
    }
}
