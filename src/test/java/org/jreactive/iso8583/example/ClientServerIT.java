package org.jreactive.iso8583.example;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ClientServerIT extends AbstractIT {

    @Test
    public void testConnected() throws Exception {
        assertThat(server.isStarted(), is(true));
        assertThat(client.isConnected(), is(true));
        Thread.sleep(5000L);
    }


}
