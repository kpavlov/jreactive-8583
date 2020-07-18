package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

/**
 * ChannelHandler responsible for logging messages.
 * <p>
 * According to PCI DSS, sensitive cardholder data, like PAN and track data,
 * should not be exposed. When running in secure mode, sensitive cardholder data will be printed masked. </p>
 */
@ChannelHandler.Sharable
public class IsoMessageLoggingHandler extends LoggingHandler {

    private static final char MASK_CHAR = '*';
    public static final int[] DEFAULT_MASKED_FIELDS = {
            34,// PAN extended
            35,// track 2
            36,// track 3
            45// track 1
    };
    private static final char[] MASKED_VALUE = "***".toCharArray();
    private static final String[] FIELD_NAMES = new String[128];
    private static final String FIELD_PROPERTIES = "iso8583fields.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (final var stream = getPropertiesStream()) {
            final var properties = new Properties();
            properties.load(stream);
            properties.forEach((key, value) -> {
                final var field = Integer.parseInt((String) key);
                FIELD_NAMES[field - 1] = (String) value;
            });
        } catch (final IOException | NumberFormatException e) {
            throw new IllegalStateException("Unable to load ISO8583 field descriptions", e);
        }
    }

    private static InputStream getPropertiesStream() {
        var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/" + FIELD_PROPERTIES);
        if (stream == null) {
            stream = IsoMessageLoggingHandler.class.getResourceAsStream(
                    "/com/github/kpavlov/jreactive8583/" + FIELD_PROPERTIES
            );
        }
        return Objects.requireNonNull(stream, "Can't load " + FIELD_PROPERTIES);
    }

    private final boolean printSensitiveData;
    private final boolean printFieldDescriptions;
    private final int[] maskedFields;

    public IsoMessageLoggingHandler(final LogLevel level,
                                    final boolean printSensitiveData,
                                    final boolean printFieldDescriptions,
                                    final int[] maskedFields) {
        super(level);
        this.printSensitiveData = printSensitiveData;
        this.printFieldDescriptions = printFieldDescriptions;
        this.maskedFields = Objects.requireNonNull(maskedFields, "maskedFields");
    }

    private static char[] maskPAN(final String fullPan) {
        final var maskedPan = fullPan.toCharArray();
        for (var i = 6; i < maskedPan.length - 4; i++) {
            maskedPan[i] = MASK_CHAR;
        }
        return maskedPan;
    }

    @Override
    protected String format(final ChannelHandlerContext ctx, final String eventName, final Object arg) {
        if (arg instanceof IsoMessage) {
            return super.format(ctx, eventName, formatIsoMessage((IsoMessage) arg));
        } else {
            return super.format(ctx, eventName, arg);
        }
    }

    private String formatIsoMessage(final IsoMessage m) {
        final var sb = new StringBuilder();
        if (printSensitiveData) {
            sb.append("Message: ").append(m.debugString()).append("\n");
        }
        sb.append("MTI: 0x").append(String.format("%04x", m.getType()));
        for (var i = 2; i < 128; i++) {
            if (m.hasField(i)) {
                final var field = m.getField(i);
                sb.append("\n  ").append(i)
                        .append(": [");

                if (printFieldDescriptions) {
                    sb.append(FIELD_NAMES[i - 1]).append(':');
                }

                final char[] formattedValue;
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
