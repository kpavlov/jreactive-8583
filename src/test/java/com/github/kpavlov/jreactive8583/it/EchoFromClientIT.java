package com.github.kpavlov.jreactive8583.it;

import com.github.kpavlov.jreactive8583.ConnectorConfigurer;
import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.github.kpavlov.jreactive8583.server.Iso8583Server;
import com.github.kpavlov.jreactive8583.server.ServerConfiguration;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@NotThreadSafe
public class EchoFromClientIT extends AbstractIT {

    private final List<IsoMessage> capturedRequests = Collections.synchronizedList(new ArrayList<>(10));
    private final CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void configureServer(Iso8583Server<IsoMessage> server) {
        server.setConfigurer(new ConnectorConfigurer<ServerConfiguration, ServerBootstrap>() {

            @Override
            public void configurePipeline(ChannelPipeline pipeline, ServerConfiguration configuration) {
                pipeline.addBefore("idleEventHandler", "connectListenerHandler", new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        super.channelActive(ctx);
                        final IsoMessage message = server.getIsoMessageFactory().newMessage(0x800);
                        ctx.writeAndFlush(message);
                    }
                });
            }
        });
    }

    @Override
    protected void configureClient(Iso8583Client<IsoMessage> client) {
        client.addMessageListener(new IsoMessageListener<IsoMessage>() {
            @Override
            public boolean applies(IsoMessage isoMessage) {
                return isoMessage.getType() == 0x800;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext ctx, IsoMessage isoMessage) {
                capturedRequests.add(isoMessage);
                latch.countDown();
                final IsoMessage response = server.getIsoMessageFactory().createResponse(isoMessage);
                response.setField(39, IsoType.ALPHA.value("01", 2));
                ctx.writeAndFlush(response);
                return false;
            }
        });
    }

    @Test
    public void shouldHandleEchoFromServer() throws Exception {
        latch.await(5, TimeUnit.SECONDS);

        assertTrue("infoMessage expected", capturedRequests.stream().anyMatch(
                m -> m.getType() == 0x800
        ));
    }


}
