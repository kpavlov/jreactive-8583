package com.github.kpavlov.jreactive8583.example;


import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.github.kpavlov.jreactive8583.server.Iso8583Server;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.slf4j.LoggerFactory.getLogger;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class LockDetectionIT {

    private static final int NUM_CLIENTS = 20;
    private static final int NUM_MESSAGES = 100;
    private static final Logger logger = getLogger(LockDetectionIT.class);
    private static final CountDownLatch latch = new CountDownLatch(NUM_CLIENTS * NUM_MESSAGES);
    private static ThreadMXBean threadMXBean;
    private static Thread monitoringThread;
    private static final AtomicInteger monitorDeadlockedCount = new AtomicInteger();
    private static final AtomicInteger deadlockedCount = new AtomicInteger();
    @Autowired
    private Iso8583Server<IsoMessage> server;
    @Autowired
    private ApplicationContext applicationContext;
    private final Iso8583Client[] clients = new Iso8583Client[NUM_CLIENTS];

    @BeforeAll
    public static void beforeAll() {
        threadMXBean = ManagementFactory.getThreadMXBean();
        assertThat(threadMXBean.isThreadContentionMonitoringSupported());
        threadMXBean.setThreadContentionMonitoringEnabled(true);

        monitoringThread = new Thread(() -> {
            while (latch.getCount() > 0) {
                detectThreadLocks();
            }
        });
    }

    private static void detectThreadLocks() {
        try {
            final long[] monitorDeadlockedThreads = threadMXBean.findMonitorDeadlockedThreads();
            final long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();

            if (monitorDeadlockedThreads != null) {
                monitorDeadlockedCount.addAndGet(monitorDeadlockedThreads.length);
                for (long threadId : monitorDeadlockedThreads) {
                    logger.warn("MonitorDeadlocked: {}", threadMXBean.getThreadInfo(threadId));
                }
            }

            if (deadlockedThreads != null) {
                deadlockedCount.addAndGet(deadlockedThreads.length);
                for (long threadId : deadlockedThreads) {
                    logger.warn("Deadlocked: {}", threadMXBean.getThreadInfo(threadId));
                }
            }

            Thread.sleep(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void before() throws Exception {

        configureServer(server);
        server.start();

        await().alias("server started").until(server::isStarted);

        for (int i = 0; i < NUM_CLIENTS; i++) {
            final Iso8583Client client = applicationContext.getBean(Iso8583Client.class);
            configureClient(client);
            clients[i] = client;
        }
        monitoringThread.start();
    }

    @AfterEach
    public void shutdownServer() {
        server.shutdown();
    }

    @Test
    public void shouldProcessRequestsFromMultipleClientsWithoutDeadlocks() throws Exception {

        for (Iso8583Client client : clients) {
            new Thread(() -> {
                try {

                    client.connect();
                    await().alias("client connected").until(client::isConnected);

                    for (int i = 0; i < NUM_MESSAGES; i++) {
                        final IsoMessage isoMessage = createRequest(client);
                        client.sendAsync(isoMessage);
                    }

                } catch (InterruptedException e) {
                    //ok
                }
            }).start();
        }

        latch.await();
        monitoringThread.join();

        assertThat(monitorDeadlockedCount.get()).as("Monitor Deadlock Count").isEqualTo(0);
        assertThat(deadlockedCount.get()).as("Deadlock Count").isEqualTo(0);
    }

    private void configureClient(Iso8583Client<IsoMessage> client) {
        client.addMessageListener(new IsoMessageListener<IsoMessage>() {
            @Override
            public boolean applies(IsoMessage isoMessage) {
                return isoMessage.getType() == 0x210;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
                latch.countDown();
                final long count = latch.getCount();
                if (count % 100 == 0 || (count < 10 && count % 10 == 0)) {
                    logger.info("Responses left to process {}", count);
                }
                return false;
            }
        });
        client.init();
    }

    private void configureServer(Iso8583Server<IsoMessage> server) {

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
                try {
                    Thread.sleep(5);// to make it slow
                } catch (InterruptedException e) {
                    //
                }
                return false;
            }
        });
        server.init();
    }

    private IsoMessage createRequest(Iso8583Client client) {
        final IsoMessage finMessage = client.getIsoMessageFactory().newMessage(0x0200);
        finMessage.setField(60, IsoType.LLLVAR.value("foo", 3));
        return finMessage;
    }
}
