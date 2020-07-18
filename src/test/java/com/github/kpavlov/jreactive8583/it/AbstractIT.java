package com.github.kpavlov.jreactive8583.it;

import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.github.kpavlov.jreactive8583.example.TestConfig;
import com.github.kpavlov.jreactive8583.server.Iso8583Server;
import com.solab.iso8583.IsoMessage;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@NotThreadSafe
public abstract class AbstractIT {

    @Autowired
    protected Iso8583Client<IsoMessage> client;

    @Autowired
    protected Iso8583Server<IsoMessage> server;

    @BeforeEach
    public final void startServerAndConnectClient() throws Exception {
        configureServer(server);
        server.init();
        server.start();

        configureClient(client);
        client.init();
        client.connect();

        await().alias("server started").until(server::isStarted);
        await().alias("client connected").until(client::isConnected);
    }

    @SuppressWarnings("EmptyMethod")
    protected void configureClient(final Iso8583Client<IsoMessage> client) {
        // to be overridden in tests
    }

    protected void configureServer(final Iso8583Server<IsoMessage> server) {
        // to be overridden in tests
    }

    @AfterEach
    public void after() {
        client.shutdown();
        server.shutdown();
    }
}
