package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoValue;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * ChannelHandler responsible for logging messages.
 * <p>
 * According to PCI DSS, sensitive cardholder data, like PAN and track data, should not be exposed. When running in secure mode, sensitive cardholder data will be printed masked. </p>
 */
@ChannelHandler.Sharable
public class IsoMessageLoggingHandler extends LoggingHandler {

    private static final char MASK_CHAR = '*';
    private static final int[] DEFAULT_MASKED_FIELDS = {
            34,// PAN extended
            35,// track 2
            36,// track 3
            45// track 1
    };
    private static char[] MASKED_VALUE = "***".toCharArray();
    private static String[] FIELD_NAMES = new String[128];

    static {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/github/kpavlov/jreactive8583/iso8583fields.properties")) {
            final Properties properties = new Properties();
            properties.load(stream);
            properties.forEach((key, value) -> {
                int field = Integer.parseInt((String) key);
                FIELD_NAMES[field - 1] = (String) value;
            });
        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException("Unable to load ISO8583 field descriptions", e);
        }
    }

    private final boolean printSensitiveData;
    private final boolean printFieldDescriptions;
    private final int[] maskedFields;

    public IsoMessageLoggingHandler(LogLevel level,
                                    boolean printSensitiveData,
                                    boolean printFieldDescriptions,
                                    int... maskedFields) {
        super(level);
        this.printSensitiveData = printSensitiveData;
        this.printFieldDescriptions = printFieldDescriptions;
        this.maskedFields = (maskedFields != null && maskedFields.length > 0) ? maskedFields : DEFAULT_MASKED_FIELDS;
    }

    public IsoMessageLoggingHandler(LogLevel level) {
        this(level, true, true);
    }

    private static char[] maskPAN(String fullPan) {
        char[] maskedPan = fullPan.toCharArray();
        for (int i = 6; i < maskedPan.length - 4; i++) {
            maskedPan[i] = MASK_CHAR;
        }
        return maskedPan;
    }

    @Override
    protected String format(ChannelHandlerContext ctx, String eventName, Object arg) {
        if (arg instanceof IsoMessage) {
            return super.format(ctx, eventName, formatIsoMessage((IsoMessage) arg));
        } else {
            return super.format(ctx, eventName, arg);
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
                sb.append("\n  ").append(i)
                        .append(": [");

                if (printFieldDescriptions) {
                    sb.append(FIELD_NAMES[i - 1]).append(':');
                }

                char[] formattedValue;
                if (printSensitiveData) {
                    formattedValue = field.toString().toCharArray();
                } else {
                    if (i == 2) {
                        formattedValue = maskPAN(field.toString());
                    } else if (Arrays.binarySearch(maskedFields, i) >= 0) {
                        formattedValue = MASKED_VALUE;
                    } else {
                        formattedValue = field.toString().toCharArray();
                    }

                }
                sb.append(field.getType()).append('(').append(field.getLength())
                        .append(")] = '").append(formattedValue).append('\'');

            }
        }
        return sb.toString();
    }
}
