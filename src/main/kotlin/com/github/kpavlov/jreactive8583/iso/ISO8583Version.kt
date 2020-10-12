@file:JvmName("ISO8583Version")

package com.github.kpavlov.jreactive8583.iso

/**
 * ISO 8583 version
 *
 * The placements of fields in different versions of the standard varies;
 * for example, the currency elements of the 1987 and 1993 versions of the standard
 * are no longer used in the 2003 version, which holds currency as a sub-element
 * of any financial amount element.
 *
 *
 * As of June 2017, however ISO 8583:2003 has yet to achieve wide acceptance.
 *
 * @see [](https://en.wikipedia.org/wiki/ISO_8583.Message_type_indicator_)
) */
@Suppress("unused")
public enum class ISO8583Version

/**
 * @param value A first digit in Message type indicator
 */
(internal val value: Int) {
    /**
     * ISO 8583:1987
     */
    V1987(0x0000),

    /**
     * ISO 8583:1993
     */
    V1993(0x1000),

    /**
     * ISO 8583:2003
     */
    V2003(0x2000),

    /**
     * National use
     */
    NATIONAL(0x8000),

    /**
     * Private use
     */
    PRIVATE(0x9000);
}
