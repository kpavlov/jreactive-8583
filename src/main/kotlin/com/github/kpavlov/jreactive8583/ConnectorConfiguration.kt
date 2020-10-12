package com.github.kpavlov.jreactive8583

/**
 * Default read/write idle timeout in seconds (ping interval) = 30 sec.
 *
 * @see .getIdleTimeout
 */
private const val DEFAULT_IDLE_TIMEOUT_SECONDS = 30

/**
 * Default [.maxFrameLength] (max message length) = 8192
 *
 * @see .getMaxFrameLength
 */
private const val DEFAULT_MAX_FRAME_LENGTH = 8192

/**
 * Default [.frameLengthFieldLength] (length of TCP Frame length) = 2
 *
 * @see .getFrameLengthFieldLength
 */
private const val DEFAULT_FRAME_LENGTH_FIELD_LENGTH = 2

/**
 * Default [.frameLengthFieldAdjust] (compensation value to add to the value of the length field) = 0
 *
 * @see .getFrameLengthFieldAdjust
 */
private const val DEFAULT_FRAME_LENGTH_FIELD_ADJUST = 0

/**
 * Default [.frameLengthFieldOffset] (the offset of the length field) = 0
 *
 * @see .getFrameLengthFieldOffset
 */
private const val DEFAULT_FRAME_LENGTH_FIELD_OFFSET = 0

public abstract class ConnectorConfiguration protected constructor(b: Builder<*>) {

    public val addEchoMessageListener: Boolean

    /**
     * The maximum length of the frame.
     */
    public val maxFrameLength: Int

    /**
     * Set channel read/write idle timeout in seconds.
     *
     * If no message was received/sent during specified time interval then `Echo` message will be sent.
     *
     * @return timeout in seconds
     */
    public val idleTimeout: Int

    /**
     * Returns number of threads in worker [EventLoopGroup].
     *
     *
     * Default value is `Runtime.getRuntime().availableProcessors() * 16`.
     *
     * @return Number of Netty worker threads
     */
    public val workerThreadsCount: Int

    public val replyOnError: Boolean

    public val addLoggingHandler: Boolean

    public val logSensitiveData: Boolean

    /**
     * Returns field numbers to be treated as sensitive data.
     * Use `null` to use default ones
     *
     * Array of ISO8583 sensitive field numbers to be masked, or `null` to use default fields.
     * @see IsoMessageLoggingHandler
     */
    public val sensitiveDataFields: IntArray

    public val logFieldDescription: Boolean

    /**
     * Returns length of TCP frame length field.
     *
     *
     * Default value is `2`.
     *
     * @return Length of TCP frame length field.
     * @see LengthFieldBasedFrameDecoder
     */
    public val frameLengthFieldLength: Int

    /**
     * Returns the offset of the length field.
     *
     * Default value is `0`.
     * @see LengthFieldBasedFrameDecoder
     *
     * @return The offset of the length field.
     */
    public val frameLengthFieldOffset: Int

    /**
     * Returns the compensation value to add to the value of the length field.
     *
     *
     * Default value is `0`.
     *
     * @return The compensation value to add to the value of the length field
     * @see LengthFieldBasedFrameDecoder
     */
    public val frameLengthFieldAdjust: Int

    /**
     * If <code>true</code> then the length header is to be encoded as a String, as opposed to the default binary
     */
    public val encodeFrameLengthAsString: Boolean

    /**
     * Allows to add default echo message listener to [AbstractIso8583Connector].
     *
     * @return true if [EchoMessageListener] should be added to [CompositeIsoMessageHandler]
     */
    public fun shouldAddEchoMessageListener(): Boolean {
        return addEchoMessageListener
    }

    /**
     * Returns true is [IsoMessageLoggingHandler]
     *
     * Allows to disable adding default logging handler to [ChannelPipeline].
     *
     * @return true if [IsoMessageLoggingHandler] should be added.
     */
    public fun addLoggingHandler(): Boolean {
        return addLoggingHandler
    }

    /**
     * Whether to reply with administrative message in case of message syntax errors. Default value is `false.`
     *
     * @return true if reply message should be sent in case of error parsing the message.
     */
    public fun replyOnError(): Boolean {
        return replyOnError
    }

    /**
     * Returns `true` if sensitive information like PAN, CVV/CVV2, and Track2 should be printed to log.
     *
     *
     * Default value is `true` (sensitive data is printed).
     *
     *
     * @return `true` if sensitive data should be printed to log
     */
    public fun logSensitiveData(): Boolean {
        return logSensitiveData
    }

    public fun logFieldDescription(): Boolean {
        return logFieldDescription
    }

    /**
     * Returns <code>true</code> if the length header is to be encoded as a String,
     * as opposed to the default binary
     *
     * Default value is <code>false</code> (frame length header is binary encoded).
     *
     * Used with @link frameLengthFieldLength, [#frameLengthFieldOffset]
     * and [#frameLengthFieldAdjust]
     *
     * @return <code>true</code> if frame length header is string-encoded
     * @return Number of Netty worker threads
     */
    public fun encodeFrameLengthAsString(): Boolean = this.encodeFrameLengthAsString

    init {
        this.addEchoMessageListener = b.addEchoMessageListener
        this.addLoggingHandler = b.addLoggingHandler
        this.encodeFrameLengthAsString = b.encodeFrameLengthAsString
        this.frameLengthFieldAdjust = b.frameLengthFieldAdjust
        this.frameLengthFieldLength = b.frameLengthFieldLength
        this.frameLengthFieldOffset = b.frameLengthFieldOffset
        this.idleTimeout = b.idleTimeout
        this.logFieldDescription = b.logFieldDescription
        this.logSensitiveData = b.logSensitiveData
        this.maxFrameLength = b.maxFrameLength
        this.replyOnError = b.replyOnError
        this.sensitiveDataFields = b.sensitiveDataFields
        this.workerThreadsCount = b.workerThreadsCount
    }

    @Suppress("UNCHECKED_CAST")
    public open class Builder<B : Builder<B>> {
        internal var addLoggingHandler = false
        internal var addEchoMessageListener = false
        internal var logFieldDescription = true
        internal var logSensitiveData = true
        internal var replyOnError = false
        internal var idleTimeout = DEFAULT_IDLE_TIMEOUT_SECONDS
        internal var maxFrameLength = DEFAULT_MAX_FRAME_LENGTH
        internal var workerThreadsCount = 0 // use netty default
        internal var sensitiveDataFields: IntArray = IntArray(0)
        internal var frameLengthFieldLength = DEFAULT_FRAME_LENGTH_FIELD_LENGTH
        internal var frameLengthFieldOffset = DEFAULT_FRAME_LENGTH_FIELD_OFFSET
        internal var frameLengthFieldAdjust = DEFAULT_FRAME_LENGTH_FIELD_ADJUST
        internal var encodeFrameLengthAsString = false

        /**
         * @param shouldAddEchoMessageListener `true` to add echo message handler.
         */
        public fun addEchoMessageListener(shouldAddEchoMessageListener: Boolean = true): B = apply {
            addEchoMessageListener = shouldAddEchoMessageListener
        } as B

        /**
         * @param length Maximum frame length.
         */
        public fun maxFrameLength(length: Int): B = apply {
            maxFrameLength = length
        } as B

        public fun idleTimeout(timeout: Int): B = apply {
            idleTimeout = timeout
        } as B

        public fun replyOnError(doReply: Boolean = true): B = apply {
            replyOnError = doReply
        } as B

        /**
         * @param addLoggingHandler `true` if [IsoMessageLoggingHandler] should be added to Netty pipeline.
         */
        public fun addLoggingHandler(value: Boolean = true): B = apply {
            addLoggingHandler = value
        } as B

        /**
         * Should log sensitive data (unmasked) or not.
         *
         *
         * **Don't use on production!**
         *
         * @param logSensitiveData `true` to log sensitive data via logger
         */
        public fun logSensitiveData(logSensitiveData: Boolean = true): B = apply {
            this.logSensitiveData = logSensitiveData
        } as B

        /**
         * @param logFieldDescription `true` to print ISO field descriptions in the log
         */
        public fun describeFieldsInLog(shouldDescribe: Boolean = true): B = apply {
            logFieldDescription = shouldDescribe
        } as B

        /**
         * @param sensitiveDataFields Array of sensitive fields
         */
        public fun sensitiveDataFields(vararg sensitiveDataFields: Int): B = apply {
            this.sensitiveDataFields = sensitiveDataFields
        } as B

        public fun frameLengthFieldLength(frameLengthFieldLength: Int): B = apply {
            this.frameLengthFieldLength = frameLengthFieldLength
        } as B

        public fun frameLengthFieldOffset(frameLengthFieldOffset: Int): B = apply {
            this.frameLengthFieldOffset = frameLengthFieldOffset
        } as B

        public fun frameLengthFieldAdjust(frameLengthFieldAdjust: Int): B = apply {
            this.frameLengthFieldAdjust = frameLengthFieldAdjust
        } as B

        public fun encodeFrameLengthAsString(encodeFrameLengthAsString: Boolean): B = apply {
            this.encodeFrameLengthAsString = encodeFrameLengthAsString
        } as B

        public fun workerThreadsCount(numberOfThreads: Int): B = apply {
            workerThreadsCount = numberOfThreads
        } as B
    }
}
