/*
 * Copyright 2014 The FIX.io Project
 *
 * The FIX.io Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.jreactive.iso8583.netty.pipeline;

import com.solab.iso8583.MessageFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateHandler;
import org.jreactive.iso8583.ConnectorConfiguration;
import org.jreactive.iso8583.ConnectorConfigurer;
import org.jreactive.iso8583.netty.codec.Iso8583Decoder;
import org.jreactive.iso8583.netty.codec.Iso8583Encoder;

public class Iso8583ChannelInitializer<C extends Channel, B extends AbstractBootstrap, G extends ConnectorConfiguration> extends ChannelInitializer<C> {

    public static final int DEFAULT_LENGTH_HEADER_LENGTH = 2;

    private final G configuration;
    private final ConnectorConfigurer<G, B> configurer;
    private final EventLoopGroup workerGroup;
    private final MessageFactory isoMessageFactory;
    private final ChannelHandler[] customChannelHandlers;
    private final Iso8583Encoder isoMessageEncoder;
    private ChannelHandler loggingHandler;
    private int headerLength = DEFAULT_LENGTH_HEADER_LENGTH;

    public Iso8583ChannelInitializer(
            G configuration,
            ConnectorConfigurer<G, B> configurer,
            EventLoopGroup workerGroup,
            MessageFactory isoMessageFactory,
            ChannelHandler... customChannelHandlers) {
        this.configuration = configuration;
        this.configurer = configurer;
        this.workerGroup = workerGroup;
        this.isoMessageFactory = isoMessageFactory;
        this.customChannelHandlers = customChannelHandlers;

        this.isoMessageEncoder = createIso8583Encoder(headerLength);
        this.loggingHandler = createLoggingHandler(configuration);
    }

    @Override
    public void initChannel(C ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("lengthFieldFameDecoder", new LengthFieldBasedFrameDecoder(
                configuration.getMaxFrameLength(), 0, headerLength, 0, headerLength));
        pipeline.addLast("iso8583Decoder", createIso8583Decoder(isoMessageFactory));

        pipeline.addLast("iso8583Encoder", isoMessageEncoder);

        if (configuration.addLoggingHandler()) {
            pipeline.addLast(workerGroup, "logging", loggingHandler);
        }

        if (configuration.replyOnError()) {
            pipeline.addLast(workerGroup, "replyOnError", loggingHandler);
        }

        pipeline.addLast("idleState", new IdleStateHandler(0, 0, configuration.getIdleTimeout()));
        pipeline.addLast("idleEventHandler", new IdleEventHandler(isoMessageFactory));
        if (customChannelHandlers != null) {
            pipeline.addLast(workerGroup, customChannelHandlers);
        }

        if (configurer != null) {
            configurer.configurePipeline(pipeline, configuration);
        }
    }

    protected Iso8583Encoder createIso8583Encoder(int lengthHeaderLength) {
        return new Iso8583Encoder(lengthHeaderLength);
    }

    protected Iso8583Decoder createIso8583Decoder(final MessageFactory messageFactory) {
        return new Iso8583Decoder(messageFactory);
    }

    protected ChannelHandler createLoggingHandler(G configuration) {
        return new IsoMessageLoggingHandler(LogLevel.DEBUG,
                configuration.logSensitiveData(),
                configuration.logFieldDescription(),
                configuration.getSensitiveDataFields());
    }


}
