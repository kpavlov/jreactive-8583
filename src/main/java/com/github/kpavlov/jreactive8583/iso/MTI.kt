@file:JvmName("MTI")

package com.github.kpavlov.jreactive8583.iso

object MTI {

    @JvmStatic
    fun mtiValue(iso8583Version: ISO8583Version,
                 messageClass: MessageClass,
                 messageFunction: MessageFunction,
                 messageOrigin: MessageOrigin) = iso8583Version.value() + messageClass.value() + messageFunction.value() + messageOrigin.value()

}
