package com.github.kpavlov.jreactive8583.it;

import com.github.kpavlov.jreactive8583.ConnectorConfigurer;
import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.github.kpavlov.jreactive8583.server.Iso8583Server;
import com.github.kpavlov.jreactive8583.server.ServerConfiguration;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.awaitility.Awaitility.await;

@NotThreadSafe
public class EchoFromClientIT extends AbstractIT {

    private final List<IsoMessage> capturedRequests = Collections.synchronizedList(new LinkedList<>());

    @Override
    protected void configureServer(final Iso8583Server<IsoMessage> server) {
        server.setConfigurer(new ConnectorConfigurer<>() {

            @Override
            public void configurePipeline(final ChannelPipeline pipeline, final ServerConfiguration configuration) {
                pipeline.addBefore("idleEventHandler", "connectListenerHandler", new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
                        super.channelActive(ctx);
                        final var message = server.getIsoMessageFactory().newMessage(0x800);
                        ctx.writeAndFlush(message);
                    }
                });
            }
        });
    }

    @Override
    protected void configureClient(final Iso8583Client<IsoMessage> client) {
        client.addMessageListener(new IsoMessageListener<>() {
            @Override
            public boolean applies(final IsoMessage isoMessage) {
                return isoMessage.getType() == 0x800;
            }

            @Override
            public boolean onMessage(final ChannelHandlerContext ctx, final IsoMessage isoMessage) {
                capturedRequests.add(isoMessage);
                final var response = server.getIsoMessageFactory().createResponse(isoMessage);
                response.setField(39, IsoType.ALPHA.value("01", 2));
                ctx.writeAndFlush(response);
                return false;
            }
        });
    }

    @Test
    public void shouldHandleEchoFromServer() {
        await()
                .alias("infoMessage expected")
                .until(() -> capturedRequests.stream().anyMatch(m -> m.getType() == 0x800));
    }


}
