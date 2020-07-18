package com.github.kpavlov.jreactive8583.iso;

/**
 * ISO 8583 version
 * <p>
 * The placements of fields in different versions of the standard varies;
 * for example, the currency elements of the 1987 and 1993 versions of the standard
 * are no longer used in the 2003 version, which holds currency as a sub-element
 * of any financial amount element.
 * <p>
 * As of June 2017, however ISO 8583:2003 has yet to achieve wide acceptance.
 *
 * @link https://en.wikipedia.org/wiki/ISO_8583#Message_type_indicator_(MTI)
 */
@SuppressWarnings("unused")
public enum ISO8583Version {

    /**
     * ISO 8583:1987
     */
    V1987(0x0),
    /**
     * ISO 8583:1993
     */
    V1993(0x1),
    /**
     * ISO 8583:2003
     */
    V2003(0x2),
    /**
     * Reserved
     */
    RESERVED_3(0x3),
    /**
     * Reserved by ISO
     */
    RESERVED_4(0x4),
    /**
     * Reserved by ISO
     */
    RESERVED_5(0x5),
    /**
     * Reserved by ISO
     */
    RESERVED_6(0x6),
    /**
     * Reserved by ISO
     */
    RESERVED_7(0x7),
    /**
     * National use
     */
    NATIONAL(0x8),
    /**
     * Private use
     */
    PRIVATE(0x9);

    private final int value;

    /**
     * @param value A first digit in Message type indicator
     */
    ISO8583Version(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
