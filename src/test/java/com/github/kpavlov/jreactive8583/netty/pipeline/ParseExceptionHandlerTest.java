package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.iso.ISO8583Version;
import com.github.kpavlov.jreactive8583.iso.J8583MessageFactory;
import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ParseExceptionHandlerTest {

    private static MessageFactory<IsoMessage> messageFactory;
    private ParseExceptionHandler handler;
    @Mock
    private ChannelHandlerContext ctx;
    @Captor
    private ArgumentCaptor<IsoMessage> messageCaptor;

    @BeforeAll
    public static void beforeClass() {
        messageFactory = new J8583MessageFactory<>(ISO8583Version.V1993);
    }

    @BeforeEach
    public void setUp() {
        handler = new ParseExceptionHandler(messageFactory, true);
    }

    @Test
    public void testExceptionCaught() throws Exception {
        final var errorMessage = UUID.randomUUID().toString();

        final var exception = new ParseException(errorMessage, 0);
        handler.exceptionCaught(ctx, exception);

        verify(ctx).writeAndFlush(messageCaptor.capture());
        verify(ctx).fireExceptionCaught(exception);
        final var message = messageCaptor.getValue();

        assertThat(message.getType()).isEqualTo(0x1644);

        //field 24
        final var field24 = message.getAt(24);
        assertThat(field24).as("field24").isInstanceOf(IsoValue.class);
        assertThat(field24.getType()).as("field24.type").isEqualTo(IsoType.NUMERIC);
        assertThat(field24.getLength()).as("field24.length").isEqualTo(3);
        assertThat(field24.getValue()).as("field24.value").isEqualTo(650);

        final var field44 = message.getAt(44);
        assertThat(field44).as("field44").isInstanceOf(IsoValue.class);
        assertThat(field44.getType()).as("field44.type").isEqualTo(IsoType.LLVAR);
        assertThat(field44.getLength()).as("field44.length").isEqualTo(25);
        assertThat(field44.getValue()).as("field44.value")
                .isEqualToComparingFieldByField(errorMessage.substring(0, 22) + "...");

    }
}
