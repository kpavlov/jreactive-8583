package com.github.kpavlov.jreactive8583.iso;

public enum MessageOrigin {
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

    private final int value;

    MessageOrigin(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
