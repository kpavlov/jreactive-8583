@file:JvmName("MessageFactory")

package com.github.kpavlov.jreactive8583.iso

import java.io.UnsupportedEncodingException
import java.text.ParseException

interface MessageFactory<T> {
    fun newMessage(type: Int): T

    fun newMessage(
        messageClass: MessageClass,
        messageFunction: MessageFunction,
        messageOrigin: MessageOrigin
    ): T

    fun createResponse(requestMessage: T): T
    fun createResponse(request: T, copyAllFields: Boolean): T

    @Throws(ParseException::class, UnsupportedEncodingException::class)
    fun parseMessage(buf: ByteArray, isoHeaderLength: Int, binaryIsoHeader: Boolean): T

    @Throws(UnsupportedEncodingException::class, ParseException::class)
    fun parseMessage(buf: ByteArray, isoHeaderLength: Int): T
}
