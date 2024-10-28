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
package com.github.kpavlov.jreactive8583.netty.pipeline

import com.github.kpavlov.jreactive8583.ConnectorConfiguration
import com.github.kpavlov.jreactive8583.ConnectorConfigurer
import com.github.kpavlov.jreactive8583.iso.MessageFactory
import com.github.kpavlov.jreactive8583.netty.codec.Iso8583Decoder
import com.github.kpavlov.jreactive8583.netty.codec.Iso8583Encoder
import com.github.kpavlov.jreactive8583.netty.codec.StringLengthFieldBasedFrameDecoder
import com.solab.iso8583.IsoMessage
import io.netty.bootstrap.AbstractBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.timeout.IdleStateHandler

/**
 * Initializes ISO 8583 channels with the necessary handlers.
 *
 * @param T the type of channel being initialized
 * @param B the type of bootstrap to be configured
 * @param C the type of connector configuration providing necessary settings
 * @param configuration the connector configuration that provides necessary settings for initializing the channel
 * @param configurer the configurer to further customize the bootstrap and pipeline configuration
 * @param workerGroup the event loop group used for managing channel handlers
 * @param isoMessageFactory the factory to create and parse ISO 8583 messages
 * @param customChannelHandlers optional custom handlers to be added to the pipeline
 */
public open class Iso8583ChannelInitializer<
    T : Channel,
    B : AbstractBootstrap<*, *>,
    C : ConnectorConfiguration,
>
    public constructor(
        private val configuration: C,
        private val configurer: ConnectorConfigurer<C, B>?,
        private val workerGroup: EventLoopGroup,
        private val isoMessageFactory: MessageFactory<IsoMessage>,
        vararg customChannelHandlers: ChannelHandler,
    ) : ChannelInitializer<T>() {
        @Suppress("SpreadOperator")
        private val customChannelHandlers = arrayOf(*customChannelHandlers)
        private val isoMessageEncoder = createIso8583Encoder(configuration)
        private val loggingHandler = createLoggingHandler(configuration)
        private val parseExceptionHandler = createParseExceptionHandler()

        public override fun initChannel(ch: T) {
            val pipeline = ch.pipeline()
            pipeline.addLast(
                "lengthFieldFrameDecoder",
                createLengthFieldBasedFrameDecoder(configuration),
            )
            pipeline.addLast("iso8583Decoder", createIso8583Decoder(isoMessageFactory))
            pipeline.addLast("iso8583Encoder", isoMessageEncoder)
            if (configuration.addLoggingHandler()) {
                pipeline.addLast(workerGroup, "logging", loggingHandler)
            }
            if (configuration.replyOnError()) {
                pipeline.addLast(workerGroup, "replyOnError", parseExceptionHandler)
            }
            if (configuration.shouldAddEchoMessageListener()) {
                pipeline.addLast(
                    workerGroup,
                    "idleState",
                    IdleStateHandler(0, 0, configuration.idleTimeout),
                )
                pipeline.addLast(
                    workerGroup,
                    "idleEventHandler",
                    IdleEventHandler(isoMessageFactory),
                )
            }
            @Suppress("SpreadOperator")
            pipeline.addLast(workerGroup, *customChannelHandlers)
            configurer?.configurePipeline(pipeline, configuration)
        }

        public fun getIsoMessageFactory(): MessageFactory<*> = isoMessageFactory

        protected fun createParseExceptionHandler(): ChannelHandler =
            ParseExceptionHandler(isoMessageFactory, true)

        protected fun createIso8583Encoder(configuration: C): Iso8583Encoder =
            Iso8583Encoder(
                configuration.frameLengthFieldLength,
                configuration.encodeFrameLengthAsString(),
            )

        protected fun createIso8583Decoder(
            messageFactory: MessageFactory<IsoMessage>,
        ): Iso8583Decoder = Iso8583Decoder(messageFactory)

        protected fun createLoggingHandler(configuration: C): ChannelHandler =
            IsoMessageLoggingHandler(
                LogLevel.DEBUG,
                configuration.logSensitiveData(),
                configuration.logFieldDescription(),
                configuration.sensitiveDataFields,
            )

        protected fun createLengthFieldBasedFrameDecoder(configuration: C): ChannelHandler {
            val lengthFieldLength = configuration.frameLengthFieldLength
            return if (configuration.encodeFrameLengthAsString()) {
                StringLengthFieldBasedFrameDecoder(
                    configuration.maxFrameLength,
                    configuration.frameLengthFieldOffset,
                    lengthFieldLength,
                    configuration.frameLengthFieldAdjust,
                    lengthFieldLength,
                )
            } else {
                LengthFieldBasedFrameDecoder(
                    configuration.maxFrameLength,
                    configuration.frameLengthFieldOffset,
                    lengthFieldLength,
                    configuration.frameLengthFieldAdjust,
                    lengthFieldLength,
                )
            }
        }
    }
