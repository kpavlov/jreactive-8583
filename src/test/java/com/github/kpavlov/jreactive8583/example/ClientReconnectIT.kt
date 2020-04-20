package com.github.kpavlov.jreactive8583.example

import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class ClientReconnectIT : AbstractIT() {

    @Test
    @Throws(Exception::class)
    fun clientShouldReconnectWhenConnectionLost() {
        //given
        Awaitility.await().alias("server started").until(server::isStarted)
        Awaitility.await().alias("client connected").until(client::isConnected)

        //when
        server.shutdown()
        Awaitility.await().alias("client was disconnected").until { !client.isConnected }

        //then
        TimeUnit.SECONDS.sleep(7)
        server.init()
        server.start()
        Awaitility.await().alias("server started").until(server::isStarted)
        Awaitility.await().alias("client connected").until(client::isConnected)
    }
}
