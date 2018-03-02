package com.github.kpavlov.jreactive8583.example;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientServerIT extends AbstractIT {

    private final Map<Integer, IsoMessage> receivedMessages = new ConcurrentHashMap<>();

    @Before
    public void beforeTest() {
        server.addMessageListener(new IsoMessageListener<IsoMessage>() {
            @Override
            public boolean applies(IsoMessage isoMessage) {
                return true;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
                if (isoMessage.hasField(11)) {
                    final Integer stan = Integer.valueOf(isoMessage.getObjectValue(11));
                    receivedMessages.put(stan, isoMessage);
                    return true;
                }
                return false;
            }
        });
        server.addMessageListener(new IsoMessageListener<IsoMessage>() {

            @Override
            public boolean applies(IsoMessage isoMessage) {
                return isoMessage.getType() == 0x200;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
                final IsoMessage response = server.getIsoMessageFactory().createResponse(isoMessage);
                response.setField(39, IsoType.ALPHA.value("00", 2));
                response.setField(60, IsoType.LLLVAR.value("XXX", 3));
                ctx.writeAndFlush(response);
                return false;
            }
        });

        TestUtil.waitFor("server started", server::isStarted);
        TestUtil.waitFor("client connected", client::isConnected);
    }

    @Test
    public void shouldSendAsyncCaptureRequest() {
        // given
        final IsoMessage finMessage = client.getIsoMessageFactory().newMessage(0x0200);
        finMessage.setField(60, IsoType.LLLVAR.value("foo", 3));
        final Integer stan = finMessage.getObjectValue(11);
        // when
        client.sendAsync(finMessage);
        // then
        TestUtil.waitFor("capture request received", () -> receivedMessages.containsKey(stan));

        IsoMessage capturedRequest = receivedMessages.remove(stan);
        assertThat(capturedRequest).as("fin request").isNotNull();
        assertThat(capturedRequest.debugString()).as("fin request string").isEqualTo(finMessage.debugString());
    }


}
