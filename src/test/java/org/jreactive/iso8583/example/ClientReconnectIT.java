package org.jreactive.iso8583.example;

import org.junit.Test;


public class ClientReconnectIT extends AbstractIT {

    @Test
    public void testClientReconnect() throws Exception {
        TestUtil.waitFor("server started", server::isStarted);
        TestUtil.waitFor("client connected", client::isConnected);
        server.shutdown();
        TestUtil.waitFor("client was disconnected", () -> (!client.isConnected()));
        server.init();
        server.start();
        TestUtil.waitFor("server started", server::isStarted);
        TestUtil.waitFor("client connected", client::isConnected);
    }
}
