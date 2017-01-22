package com.github.kpavlov.jreactive8583.example;

import org.junit.Test;


public class ClientReconnectIT extends AbstractIT {

    @Test
    public void testClientReconnect() throws Exception {
        TestUtil.waitFor("server started", server::isStarted);
        TestUtil.waitFor("client connected", client::isConnected);
        server.shutdown();
        TestUtil.waitFor("client was disconnected", () -> (!client.isConnected()));
        Thread.sleep(7000);
        server.init();
        server.start();
        TestUtil.waitFor("server started", server::isStarted);
        TestUtil.waitFor("client connected", client::isConnected);
    }
}
