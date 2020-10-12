@file:JvmName("MTI")

package com.github.kpavlov.jreactive8583.iso

public object MTI {

    @JvmStatic
    public fun mtiValue(
        iso8583Version: ISO8583Version,
        messageClass: MessageClass,
        messageFunction: MessageFunction,
        messageOrigin: MessageOrigin
    ): Int = (
        iso8583Version.value +
            messageClass.value +
            messageFunction.value +
            messageOrigin.value
        )
}
