package com.github.kpavlov.jreactive8583.it;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Tag("reconnect")
public class ClientReconnectIT extends AbstractIT {

    @Test
    public void clientShouldReconnectWhenConnectionLost() throws Exception {
        //when
        server.shutdown();
        await().alias("client was disconnected").until(() -> (!client.isConnected()));

        //then
        TimeUnit.SECONDS.sleep(3);
        server.init();
        server.start();
        await().alias("server started").until(server::isStarted);
        await().alias("client connected").until(client::isConnected);
    }
}
