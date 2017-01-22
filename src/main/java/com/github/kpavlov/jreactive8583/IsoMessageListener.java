package com.github.kpavlov.jreactive8583;

import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;

public interface IsoMessageListener<T extends IsoMessage> {

    /**
     * Returns <code>true</code> if given message can be handled by {@link #onMessage(ChannelHandlerContext, IsoMessage)}.
     *
     * @param isoMessage ISO message to check. Not null.
     * @return true if message should be handled.
     */
    boolean applies(T isoMessage);

    /**
     * Handles the message. If message should not be processed by any other handlers then this method should return <code>false</code>.
     *
     * @param ctx        current {@link ChannelHandlerContext}
     * @param isoMessage received isoMessage. Not null.
     * @return true if message should be handled by subsequent message listeners
     */
    boolean onMessage(ChannelHandlerContext ctx, T isoMessage);
}
