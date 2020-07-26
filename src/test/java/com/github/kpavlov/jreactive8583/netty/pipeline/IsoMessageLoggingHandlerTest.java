package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.local.LocalChannel;
import io.netty.handler.logging.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IsoMessageLoggingHandlerTest {

    private IsoMessageLoggingHandler handler;

    private String pan;
    private String cvv;
    private String track1;
    private String track2;
    private String track3;

    private IsoMessage message;

    @Mock
    private ChannelHandlerContext ctx;

    @BeforeEach
    public void setUp() throws Exception {

        when(ctx.channel()).thenReturn(new LocalChannel());

        final MessageFactory<?> messageFactory = ConfigParser.createDefault();
        message = messageFactory.newMessage(0x0200);

        pan = randomNumeric(19);
        cvv = randomAlphanumeric(3);
        track1 = randomAlphanumeric(10);
        track2 = randomAlphanumeric(20);
        track3 = randomAlphanumeric(30);

        message.setValue(2, pan, IsoType.NUMERIC, pan.length());
        message.setValue(112, cvv, IsoType.NUMERIC, 3);
        message.setValue(35, track2, IsoType.LLLVAR, 37);
        message.setValue(36, track3, IsoType.LLLVAR, 106);
        message.setValue(45, track1, IsoType.LLLVAR, 76);
    }

    @Test
    public void testMaskSensitiveData() {
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG, false, true, new int[]{34, 35, 36, 45, 112});

        final var result = handler.format(ctx, "someEvent", message);

        assertThat(result).doesNotContain(pan);
        assertThat(result).doesNotContain(cvv);
        assertThat(result).doesNotContain(track1);
        assertThat(result).doesNotContain(track2);
        assertThat(result).doesNotContain(track3);
    }

    @Test
    public void testMaskDefaultSensitiveData() {
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG, false, true,
            IsoMessageLoggingHandler.DEFAULT_MASKED_FIELDS);

        final var result = handler.format(ctx, "someEvent", message);

        assertThat(result).doesNotContain(pan);
        assertThat(result).doesNotContain(track1).as("track1");
        assertThat(result).doesNotContain(track2).as("track2");
        assertThat(result).doesNotContain(track3).as("track3");
        // there is no standard field for CVV, so it's not masked by default
        assertThat(result).contains(cvv);
    }

    @Test
    public void testPrintSensitiveData() {
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG, true, true, IsoMessageLoggingHandler.DEFAULT_MASKED_FIELDS);

        final var result = handler.format(ctx, "someEvent", message);

        assertThat(result).contains(pan);
        assertThat(result).contains(cvv);
        assertThat(result).contains(track1);
        assertThat(result).contains(track2);
        assertThat(result).contains(track3);
    }

    @Test
    public void testHideFieldDescriptions() {
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG, false, false, IsoMessageLoggingHandler.DEFAULT_MASKED_FIELDS);

        final var result = handler.format(ctx, "someEvent", message);

        assertThat(result).doesNotContain("Primary account number (PAN)");
    }

    @Test
    public void testShowFieldDescriptions() {
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG, false, true, IsoMessageLoggingHandler.DEFAULT_MASKED_FIELDS);

        final var result = handler.format(ctx, "someEvent", message);

        assertThat(result).contains("Primary account number (PAN)");
    }
}
