package org.jreactive.iso8583.example;

import org.jreactive.iso8583.Netty8583Client;
import org.jreactive.iso8583.Netty8583Server;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public abstract class AbstractIT {

        @Autowired
        Netty8583Client client;

        @Autowired
        Netty8583Server server;

        @Before
        public void before() throws Exception {
            server.start();
            client.connect();
        }

        @After
        public void after() throws Exception {
            client.disconnect();
            server.shutdown();
        }
}
