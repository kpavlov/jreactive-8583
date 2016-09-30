package org.jreactive.iso8583.netty.pipeline;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.local.LocalChannel;
import io.netty.handler.logging.LogLevel;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
    public void setUp() throws Exception {

        when(ctx.channel()).thenReturn(new LocalChannel());

        MessageFactory messageFactory = ConfigParser.createDefault();
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
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG, false, true, 34, 35, 36, 45, 112);

        final String result = handler.format(ctx, "someEvent", message);

        assertThat(result, not(CoreMatchers.containsString(pan)));
        assertThat(result, not(CoreMatchers.containsString(cvv)));
        assertThat(result, not(CoreMatchers.containsString(track1)));
        assertThat(result, not(CoreMatchers.containsString(track2)));
        assertThat(result, not(CoreMatchers.containsString(track3)));
    }

    @Test
    public void testMaskDefaultSensitiveData() {
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG, false, true);

        final String result = handler.format(ctx, "someEvent", message);

        assertThat(result, not(CoreMatchers.containsString(pan)));
        assertThat("track1",result, not(CoreMatchers.containsString(track1)));
        assertThat("track2",result, not(CoreMatchers.containsString(track2)));
        assertThat("track3",result, not(CoreMatchers.containsString(track3)));
        // there is no standard field for CVV, so it's not masked by default
        assertThat(result, CoreMatchers.containsString(cvv));
    }

    @Test
    public void testPrintSensitiveData() {
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG);

        final String result = handler.format(ctx, "someEvent", message);

        assertThat(result, CoreMatchers.containsString(pan));
        assertThat(result, CoreMatchers.containsString(cvv));
        assertThat(result, CoreMatchers.containsString(track1));
        assertThat(result, CoreMatchers.containsString(track2));
        assertThat(result, CoreMatchers.containsString(track3));
    }

    @Test
    public void testHideFieldDescriptions() {
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG, false, false);

        final String result = handler.format(ctx, "someEvent", message);

        assertThat(result, not(CoreMatchers.containsString("Primary account number (PAN)")));
    }

    @Test
    public void testShowFieldDescriptions() {
        handler = new IsoMessageLoggingHandler(LogLevel.DEBUG, false, true);

        final String result = handler.format(ctx, "someEvent", message);

        assertThat(result, CoreMatchers.containsString("Primary account number (PAN)"));
    }
}