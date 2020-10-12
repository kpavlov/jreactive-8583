@file:JvmName("MessageFactory")

package com.github.kpavlov.jreactive8583.iso

import java.io.UnsupportedEncodingException
import java.text.ParseException

public interface MessageFactory<T> {
    public fun newMessage(type: Int): T

    public fun newMessage(
        messageClass: MessageClass,
        messageFunction: MessageFunction,
        messageOrigin: MessageOrigin
    ): T

    public fun createResponse(requestMessage: T): T
    public fun createResponse(request: T, copyAllFields: Boolean): T

    @Throws(ParseException::class, UnsupportedEncodingException::class)
    public fun parseMessage(buf: ByteArray, isoHeaderLength: Int, binaryIsoHeader: Boolean): T

    @Throws(UnsupportedEncodingException::class, ParseException::class)
    public fun parseMessage(buf: ByteArray, isoHeaderLength: Int): T
}
