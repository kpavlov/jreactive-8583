@file:JvmName("MessageOrigin")

package com.github.kpavlov.jreactive8583.iso

@Suppress("unused")
public enum class MessageOrigin(internal val value: Int) {

    /**
     * xxx0	Acquirer
     */
    ACQUIRER(0x0000),

    /**
     * xxx1	Acquirer repeat
     */
    ACQUIRER_REPEAT(0x0001),

    /**
     * xxx2	Issuer
     */
    ISSUER(0x0002),

    /**
     * xxx3	Issuer repeat
     */
    ISSUER_REPEAT(0x0003),

    /**
     * xxx4	Other
     */
    OTHER(0x0004),

    /**
     * xxx5	Other repeat
     */
    OTHER_REPEAT(0x0005);
}
