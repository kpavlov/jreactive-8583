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

package com.github.kpavlov.jreactive8583.netty.pipeline;

import com.github.kpavlov.jreactive8583.ConnectorConfiguration;
import com.github.kpavlov.jreactive8583.ConnectorConfigurer;
import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.github.kpavlov.jreactive8583.netty.codec.Iso8583Decoder;
import com.github.kpavlov.jreactive8583.netty.codec.Iso8583Encoder;
import com.github.kpavlov.jreactive8583.netty.codec.StringLengthFieldBasedFrameDecoder;
import com.solab.iso8583.IsoMessage;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateHandler;

@SuppressWarnings("WeakerAccess")
public class Iso8583ChannelInitializer<
        T extends Channel,
        B extends AbstractBootstrap,
        C extends ConnectorConfiguration> extends ChannelInitializer<T> {

    private final C configuration;
    private final ConnectorConfigurer<C, B> configurer;
    private final EventLoopGroup workerGroup;
    private final MessageFactory<IsoMessage> isoMessageFactory;
    private final ChannelHandler[] customChannelHandlers;
    private final Iso8583Encoder isoMessageEncoder;
    private final ChannelHandler loggingHandler;
    private final ChannelHandler parseExceptionHandler;

    public Iso8583ChannelInitializer(
            final C configuration,
            final ConnectorConfigurer<C, B> configurer,
            final EventLoopGroup workerGroup,
            final MessageFactory<IsoMessage> isoMessageFactory,
            final ChannelHandler... customChannelHandlers) {
        this.configuration = configuration;
        this.configurer = configurer;
        this.workerGroup = workerGroup;
        this.isoMessageFactory = isoMessageFactory;
        this.customChannelHandlers = customChannelHandlers;

        this.isoMessageEncoder = createIso8583Encoder(configuration);
        this.loggingHandler = createLoggingHandler(configuration);
        this.parseExceptionHandler = createParseExceptionHandler();
    }

    @Override
    public void initChannel(final T ch) {
        final var pipeline = ch.pipeline();

        pipeline.addLast("lengthFieldFrameDecoder", createLengthFieldBasedFrameDecoder(configuration));
        pipeline.addLast("iso8583Decoder", createIso8583Decoder(isoMessageFactory));

        pipeline.addLast("iso8583Encoder", isoMessageEncoder);

        if (configuration.addLoggingHandler()) {
            pipeline.addLast(workerGroup, "logging", loggingHandler);
        }

        if (configuration.replyOnError()) {
            pipeline.addLast(workerGroup, "replyOnError", parseExceptionHandler);
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

    protected MessageFactory<?> getIsoMessageFactory() {
        return isoMessageFactory;
    }

    protected ChannelHandler createParseExceptionHandler() {
        return new ParseExceptionHandler(isoMessageFactory, true);
    }

    protected Iso8583Encoder createIso8583Encoder(final C configuration) {
        return new Iso8583Encoder(configuration.getFrameLengthFieldLength(),
                configuration.encodeFrameLengthAsString());
    }

    protected Iso8583Decoder createIso8583Decoder(final MessageFactory<IsoMessage> messageFactory) {
        return new Iso8583Decoder(messageFactory);
    }

    protected ChannelHandler createLoggingHandler(final C configuration) {
        return new IsoMessageLoggingHandler(LogLevel.DEBUG,
                configuration.logSensitiveData(),
                configuration.logFieldDescription(),
                configuration.getSensitiveDataFields());
    }

    protected ChannelHandler createLengthFieldBasedFrameDecoder(final C configuration) {
        final var lengthFieldLength = configuration.getFrameLengthFieldLength();
        if (configuration.encodeFrameLengthAsString()) {
            return new StringLengthFieldBasedFrameDecoder(
                    configuration.getMaxFrameLength(), configuration.getFrameLengthFieldOffset(), lengthFieldLength,
                    configuration.getFrameLengthFieldAdjust(), lengthFieldLength);
        } else {
            return new LengthFieldBasedFrameDecoder(
                    configuration.getMaxFrameLength(), configuration.getFrameLengthFieldOffset(), lengthFieldLength,
                    configuration.getFrameLengthFieldAdjust(), lengthFieldLength);
        }
    }
}
