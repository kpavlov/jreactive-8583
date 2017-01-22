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
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
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
    public void setUp() throws Exception {
        handler = new ParseExceptionHandler(messageFactory, true);
    }

    @Test
    public void testExceptionCaught() throws Exception {
        String errorMessage = UUID.randomUUID().toString();

        handler.exceptionCaught(ctx, new ParseException(errorMessage, 0));

        verify(ctx).writeAndFlush(messageCaptor.capture());
        final IsoMessage message = messageCaptor.getValue();

        assertThat(message.getType(), is(0x1644));

        //field 24
        final IsoValue<Object> field24 = message.getAt(24);
        assertThat("field24", field24, notNullValue());
        assertThat("field24.type", field24.getType(), is(IsoType.NUMERIC));
        assertThat("field24.length", field24.getLength(), is(3));
        assertThat("field24.value", field24.getValue(), is(650));

        final IsoValue<Object> field44 = message.getAt(44);
        assertThat("field44", field44, notNullValue());
        assertThat("field44.type", field44.getType(), is(IsoType.LLVAR));
        assertThat("field44.length", field44.getLength(), is(25));
        assertThat("field44.value", field44.getValue(), is(errorMessage.substring(0, 22) + "..."));

    }
}