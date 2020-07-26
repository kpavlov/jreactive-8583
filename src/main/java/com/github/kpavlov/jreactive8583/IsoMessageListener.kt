@file:JvmName("IsoMessageListener")

package com.github.kpavlov.jreactive8583

import com.solab.iso8583.IsoMessage
import io.netty.channel.ChannelHandlerContext

interface IsoMessageListener<T : IsoMessage> {
    /**
     * Returns `true` if given message can be handled
     * by [.onMessage].
     *
     * @param isoMessage ISO message to check. Not null.
     * @return true if message should be handled.
     */
    fun applies(isoMessage: T): Boolean

    /**
     * Handles the message. If message should not be processed by any other handlers
     * then this method should return `false`.
     *
     * @param ctx        current [ChannelHandlerContext]
     * @param isoMessage received isoMessage. Not null.
     * @return true if message should be handled by subsequent message listeners
     */
    fun onMessage(ctx: ChannelHandlerContext, isoMessage: T): Boolean
}
