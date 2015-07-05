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
import io.netty.channel.EventLoopGroup;

public class Iso8583AcceptorChannelInitializer<C extends Channel> extends AbstractIso8583ChannelInitializer<C> {

    public Iso8583AcceptorChannelInitializer(EventLoopGroup workerGroup,
                                             MessageFactory isoMessageFactory,
                                             DispatchingMessageHandler messageListener) {
        super(workerGroup, isoMessageFactory, messageListener);
    }
}


