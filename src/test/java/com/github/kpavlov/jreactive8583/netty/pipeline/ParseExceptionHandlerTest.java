package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.IsoValue;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ParseExceptionHandlerTest {

    private static MessageFactory messageFactory;
    private ParseExceptionHandler handler;
    @Mock
    private ChannelHandlerContext ctx;
    @Captor
    private ArgumentCaptor<IsoMessage> messageCaptor;

    @BeforeClass
    public static void beforeClass() throws Exception {
        messageFactory = ConfigParser.createDefault();
    }

    @Before
    public void setUp() {
        handler = new ParseExceptionHandler(messageFactory, true);
    }

    @Test
    public void testExceptionCaught() throws Exception {
        String errorMessage = UUID.randomUUID().toString();

        handler.exceptionCaught(ctx, new ParseException(errorMessage, 0));

        verify(ctx).writeAndFlush(messageCaptor.capture());
        final IsoMessage message = messageCaptor.getValue();

        assertThat(message.getType()).isEqualTo(0x1644);

        //field 24
        final IsoValue<Object> field24 = message.getAt(24);
        assertThat(field24).isInstanceOf(IsoValue.class).as("field24");
        assertThat(field24.getType()).isEqualTo(IsoType.NUMERIC).as("field24.type");
        assertThat(field24.getLength()).isEqualTo(3).as("field24.length");
        assertThat(field24.getValue()).isEqualTo(650).as("field24.value");

        final IsoValue<Object> field44 = message.getAt(44);
        assertThat(field44).isInstanceOf(IsoValue.class).as("field44");
        assertThat(field44.getType()).isEqualTo(IsoType.LLVAR).as("field44.type");
        assertThat(field44.getLength()).isEqualTo(25).as("field44.length");
        assertThat(field44.getValue()).isEqualToComparingFieldByField(errorMessage.substring(0, 22) + "...").as("field44.value");

    }
}