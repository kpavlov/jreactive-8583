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
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.IdleStateHandler;
import org.jreactive.iso8583.netty.codec.Iso8583Codec;

public abstract class AbstractIso8583ChannelInitializer<C extends Channel> extends ChannelInitializer<C> {

    public static final int DEFAULT_LENGTH_HEADER_LENGTH = 2;
    public static final int DEFAULT_IDLE_TIMEOUT = 30;
    public static final int DEFAULT_MAX_FRAME_LENGTH = 32768;

    private final EventLoopGroup workerGroup;
    private final MessageFactory isoMessageFactory;
    private final DispatchingMessageHandler messageListener;
    private int headerLength = DEFAULT_LENGTH_HEADER_LENGTH;
    private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;
    private int idleTimeoutSeconds = DEFAULT_IDLE_TIMEOUT;

    protected AbstractIso8583ChannelInitializer(EventLoopGroup workerGroup,
                                                MessageFactory isoMessageFactory,
                                                DispatchingMessageHandler messageListener) {
        this.workerGroup = workerGroup;
        this.isoMessageFactory = isoMessageFactory;
        this.messageListener = messageListener;
    }

    @Override
    public void initChannel(C ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("lengthFieldFameDecoder", new LengthFieldBasedFrameDecoder(maxFrameLength, 0, headerLength, 0, headerLength, true));
        pipeline.addLast("lengthFieldPrepender", new LengthFieldPrepender(headerLength));
        pipeline.addLast("iso8583Codec", new Iso8583Codec(isoMessageFactory));
        pipeline.addLast("logging", new IsoMessageLoggingHandler(LogLevel.DEBUG));
        pipeline.addLast("idleState", new IdleStateHandler(0, 0, idleTimeoutSeconds));
        pipeline.addLast("idleEventHandler", new IdleEventHandler(isoMessageFactory));
        pipeline.addLast(workerGroup, "isoMessageHandler", messageListener);

        configure(pipeline);
    }

    /**
     * Implement this method in subclass to customize {@link ChannelPipeline}
     * <p>
     * Base implementation was intentionally left blank.
     */
    protected void configure(ChannelPipeline pipeline) {
        //implementation was intentionally left blank
    }

}
