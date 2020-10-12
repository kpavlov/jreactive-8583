package com.github.kpavlov.jreactive8583.netty.pipeline

import com.solab.iso8583.IsoMessage
import com.solab.iso8583.IsoValue
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import java.io.IOException
import java.io.InputStream
import java.lang.Integer.parseInt

/**
 * ChannelHandler responsible for logging messages.
 *
 * According to PCI DSS, sensitive cardholder data, like PAN and track data,
 * should not be exposed. When running in secure mode,
 * sensitive cardholder data will be printed masked.
 */
@Sharable
internal class IsoMessageLoggingHandler(
    level: LogLevel,
    private val printSensitiveData: Boolean,
    private val printFieldDescriptions: Boolean,
    private val maskedFields: IntArray = DEFAULT_MASKED_FIELDS
) : LoggingHandler(level) {

    companion object {

        private const val MASK_CHAR = '*'
        private val MASKED_VALUE = "***".toCharArray()

        @JvmField
        val DEFAULT_MASKED_FIELDS = intArrayOf(
            34, // PAN extended
            35, // track 2
            36, // track 3
            45 // track 1
        )

        private val FIELD_NAMES = arrayOfNulls<String>(256)
        private const val FIELD_PROPERTIES = "iso8583fields.properties"
        private fun loadProperties() {
            try {
                propertiesStream.use { stream ->
                    val properties = java.util.Properties()
                    properties.load(stream)
                    properties.forEach { key: Any, value: Any? ->
                        val field = parseInt(key.toString())
                        FIELD_NAMES[field - 1] =
                            value as String
                    }
                }
            } catch (e: IOException) {
                throw IllegalStateException("Unable to load ISO8583 field descriptions", e)
            } catch (e: NumberFormatException) {
                throw IllegalStateException("Unable to load ISO8583 field descriptions", e)
            }
        }

        private val propertiesStream: InputStream
            get() {
                var stream =
                    Thread.currentThread().contextClassLoader
                        .getResourceAsStream("/$FIELD_PROPERTIES")
                if (stream == null) {
                    stream = IsoMessageLoggingHandler::class.java.getResourceAsStream(
                        "/com/github/kpavlov/jreactive8583/$FIELD_PROPERTIES"
                    )
                }
                return stream!!
            }

        init {
            loadProperties()
        }
    }

    public override fun format(
        ctx: ChannelHandlerContext,
        eventName: String,
        arg: Any
    ): String {
        return if (arg is IsoMessage) {
            super.format(ctx, eventName, formatIsoMessage(arg))
        } else {
            super.format(ctx, eventName, arg)
        }
    }

    private fun formatIsoMessage(m: IsoMessage): String {
        val sb = StringBuilder()
        if (printSensitiveData) {
            sb.append("Message: ").append(m.debugString()).append("\n")
        }
        sb.append("MTI: 0x").append(String.format("%04x", m.type))
        for (i in 2..127) {
            if (m.hasField(i)) {
                val field = m.getField<Any>(i)
                sb.append("\n  ").append(i).append(": [")
                if (printFieldDescriptions) {
                    sb.append(FIELD_NAMES[i - 1]).append(':')
                }
                val formattedValue: CharArray
                formattedValue = getFormattedValue(field, i)
                sb.append(field.type).append('(').append(field.length)
                    .append(")] = '").append(formattedValue).append('\'')
            }
        }
        return sb.toString()
    }

    private fun getFormattedValue(field: IsoValue<Any>, i: Int): CharArray {
        return if (printSensitiveData) {
            field.toString().toCharArray()
        } else {
            when {
                i == 2 -> maskPAN(field.toString())
                maskedFields.contains(i) -> MASKED_VALUE
                else -> field.toString().toCharArray()
            }
        }
    }

    private fun maskPAN(fullPan: String): CharArray {
        val maskedPan = fullPan.toCharArray()
        for (i in 6 until maskedPan.size - 4) {
            maskedPan[i] = MASK_CHAR
        }
        return maskedPan
    }
}
