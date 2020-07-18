package com.github.kpavlov.jreactive8583.iso;

/**
 * Position two of the MTI specifies the overall purpose of the message.
 *
 * @link https://en.wikipedia.org/wiki/ISO_8583#Message_type_indicator_(MTI)
 */
public enum MessageClass {

    /**
     * x1xx	Authorization message
     * <p>
     * Determine if funds are available, get an approval but do not post
     * to account for reconciliation. Dual message system (DMS), awaits file exchange
     * for posting to the account.
     */
    AUTHORIZATION(0x0100),

    /**
     * x2xx	Financial messages
     * <p>
     * Determine if funds are available, get an approval and post directly
     * to the account. Single message system (SMS), no file exchange after this.
     */
    FINANCIAL(0x0200),

    /**
     * x3xx	File actions message
     * <p>
     * Used for hot-card, TMS and other exchanges
     */
    FILE_ACTIONS(0x0300),

    /**
     * x4xx	Reversal and chargeback messages
     * <p>
     * - Reversal (x4x0 or x4x1): Reverses the action of a previous authorization.
     * - Chargeback (x4x2 or x4x3): Charges back a previously cleared financial message.
     */
    REVERSAL_CHARGEBACK(0x0400),

    /**
     * x5xx	Reconciliation message
     * Transmits settlement information message.
     */
    RECONCILIATION(0x0500),

    /**
     * x6xx	Administrative message
     * <p>
     * Transmits administrative advice. Often used for failure messages
     * (e.g., message reject or failure to apply).
     */
    ADMINISTRATIVE(0x0600),

    /**
     * x7xx	Fee collection messages
     */
    FEE_COLLECTION(0x0700),
    /**
     * x8xx	Network management message
     * Used for secure key exchange, logon, echo test and other network functions.
     */
    NETWORK_MANAGEMENT(0x0800);

    private final int value;

    MessageClass(int value) {

        this.value = value;
    }

    public int value() {
        return value;
    }

}
