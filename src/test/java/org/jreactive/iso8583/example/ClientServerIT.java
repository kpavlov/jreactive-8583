package org.jreactive.iso8583.example;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import io.netty.channel.ChannelHandlerContext;
import org.jreactive.iso8583.IsoMessageListener;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ClientServerIT extends AbstractIT {

    volatile IsoMessage capturedRequest;

    @Before
    public void beforeTest() {
        server.addMessageListener(new IsoMessageListener<IsoMessage>() {

            @Override
            public boolean applies(IsoMessage isoMessage) {
                return isoMessage.getType() ==  0x200;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
                capturedRequest = isoMessage;
                final IsoMessage response = server.getIsoMessageFactory().createResponse(isoMessage);
                response.setField(39, IsoType.ALPHA.value("00", 2));
                response.setField(60, IsoType.LLLVAR.value("XXX", 3));
                ctx.writeAndFlush(response);
                return false;
            }
        });
    }

    @Test
    public void testConnected() throws Exception {
        TestUtil.waitFor("server started", server::isStarted);
        TestUtil.waitFor("client connected", client::isConnected);

        final IsoMessage finMessage = client.getIsoMessageFactory().newMessage(0x0200);
        finMessage.setValue(2, "4000000000000002", IsoType.NUMERIC, 19);
        finMessage.setField(60, IsoType.LLLVAR.value("foo", 3));
        client.send(finMessage);

        TestUtil.waitFor("capture request received", ()->(capturedRequest != null));

        assertThat("fin request", capturedRequest, notNullValue());
        assertThat("fin request", capturedRequest.debugString(), equalTo(finMessage.debugString()));
    }


}
