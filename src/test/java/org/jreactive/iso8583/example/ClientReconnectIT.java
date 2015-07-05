package org.jreactive.iso8583.example;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ClientReconnectIT extends AbstractIT {

    @Test
    public void testClientReconnect() throws Exception {
        assertThat(server.isStarted(), is(true));
        assertThat(client.isConnected(), is(true));
        server.shutdown();
        Thread.sleep(100L);
        assertThat(client.isConnected(), is(false));
        server.start();
        assertThat(server.isStarted(), is(true));
        Thread.sleep(1000L);
        assertThat(client.isConnected(), is(true));
    }
}
