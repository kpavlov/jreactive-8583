package com.github.kpavlov.jreactive8583.iso

import com.github.kpavlov.jreactive8583.iso.MTI.mtiValue
import com.solab.iso8583.IsoMessage
import com.solab.iso8583.parse.ConfigParser
import java.io.UnsupportedEncodingException
import java.text.ParseException
import javax.annotation.Nonnull

internal open class J8583MessageFactory<T : IsoMessage> @JvmOverloads constructor(
    private val messageFactory: com.solab.iso8583.MessageFactory<T> = defaultMessageFactory(),
    private val isoVersion: ISO8583Version = ISO8583Version.V1987
) : MessageFactory<T> {

    constructor(isoVersion: ISO8583Version) : this(defaultMessageFactory(), isoVersion)

    override fun newMessage(type: Int): T {
        return messageFactory.newMessage(type)
    }

    override fun newMessage(
        @Nonnull messageClass: MessageClass,
        @Nonnull messageFunction: MessageFunction,
        @Nonnull messageOrigin: MessageOrigin
    ): T {
        return newMessage(mtiValue(isoVersion, messageClass, messageFunction, messageOrigin))
    }

    override fun createResponse(requestMessage: T): T {
        return messageFactory.createResponse(requestMessage)
    }

    override fun createResponse(request: T, copyAllFields: Boolean): T {
        return messageFactory.createResponse(request, copyAllFields)
    }

    @Throws(ParseException::class, UnsupportedEncodingException::class)
    override fun parseMessage(
        buf: ByteArray,
        isoHeaderLength: Int,
        binaryIsoHeader: Boolean
    ): T {
        return messageFactory.parseMessage(buf, isoHeaderLength, binaryIsoHeader)
    }

    @Throws(UnsupportedEncodingException::class, ParseException::class)
    override fun parseMessage(buf: ByteArray, isoHeaderLength: Int): T {
        return messageFactory.parseMessage(buf, isoHeaderLength)
    }
}

private fun <T : IsoMessage> defaultMessageFactory() =
    ConfigParser.createDefault() as com.solab.iso8583.MessageFactory<T>
