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

abstract class ConnectorConfiguration protected constructor(b: Builder<*>) {

    val addEchoMessageListener: Boolean

    /**
     * The maximum length of the frame.
     */
    val maxFrameLength: Int

    /**
     * Set channel read/write idle timeout in seconds.
     *
     * If no message was received/sent during specified time interval then `Echo` message will be sent.
     *
     * @return timeout in seconds
     */
    val idleTimeout: Int

    /**
     * Returns number of threads in worker [EventLoopGroup].
     *
     *
     * Default value is `Runtime.getRuntime().availableProcessors() * 16`.
     *
     * @return Number of Netty worker threads
     */
    val workerThreadsCount: Int

    val replyOnError: Boolean

    val addLoggingHandler: Boolean

    val logSensitiveData: Boolean

    /**
     * Returns field numbers to be treated as sensitive data.
     * Use `null` to use default ones
     *
     * Array of ISO8583 sensitive field numbers to be masked, or `null` to use default fields.
     * @see IsoMessageLoggingHandler
     */
    val sensitiveDataFields: IntArray

    val logFieldDescription: Boolean

    /**
     * Returns length of TCP frame length field.
     *
     *
     * Default value is `2`.
     *
     * @return Length of TCP frame length field.
     * @see LengthFieldBasedFrameDecoder
     */
    val frameLengthFieldLength: Int

    /**
     * Returns the offset of the length field.
     *
     * Default value is `0`.
     * @see LengthFieldBasedFrameDecoder
     *
     * @return The offset of the length field.
     */
    val frameLengthFieldOffset: Int

    /**
     * Returns the compensation value to add to the value of the length field.
     *
     *
     * Default value is `0`.
     *
     * @return The compensation value to add to the value of the length field
     * @see LengthFieldBasedFrameDecoder
     */
    val frameLengthFieldAdjust: Int

    /**
     * If <code>true</code> then the length header is to be encoded as a String, as opposed to the default binary
     */
    val encodeFrameLengthAsString: Boolean

    /**
     * Allows to add default echo message listener to [AbstractIso8583Connector].
     *
     * @return true if [EchoMessageListener] should be added to [CompositeIsoMessageHandler]
     */
    fun shouldAddEchoMessageListener(): Boolean {
        return addEchoMessageListener
    }

    /**
     * Returns true is [IsoMessageLoggingHandler]
     *
     * Allows to disable adding default logging handler to [ChannelPipeline].
     *
     * @return true if [IsoMessageLoggingHandler] should be added.
     */
    fun addLoggingHandler(): Boolean {
        return addLoggingHandler
    }

    /**
     * Whether to reply with administrative message in case of message syntax errors. Default value is `false.`
     *
     * @return true if reply message should be sent in case of error parsing the message.
     */
    fun replyOnError(): Boolean {
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
    fun logSensitiveData(): Boolean {
        return logSensitiveData
    }

    fun logFieldDescription(): Boolean {
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
    fun encodeFrameLengthAsString() = this.encodeFrameLengthAsString

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
    protected open class Builder<B : Builder<B>> {
        var addLoggingHandler = false
        var addEchoMessageListener = false
        var logFieldDescription = true
        var logSensitiveData = true
        var replyOnError = false
        var idleTimeout = DEFAULT_IDLE_TIMEOUT_SECONDS
        var maxFrameLength = DEFAULT_MAX_FRAME_LENGTH
        var workerThreadsCount = 0 // use netty default
        var sensitiveDataFields: IntArray = IntArray(0)
        var frameLengthFieldLength = DEFAULT_FRAME_LENGTH_FIELD_LENGTH
        var frameLengthFieldOffset = DEFAULT_FRAME_LENGTH_FIELD_OFFSET
        var frameLengthFieldAdjust = DEFAULT_FRAME_LENGTH_FIELD_ADJUST
        var encodeFrameLengthAsString = false

        /**
         * @param shouldAddEchoMessageListener `true` to add echo message handler.
         */
        fun addEchoMessageListener(shouldAddEchoMessageListener: Boolean = true) = apply {
            addEchoMessageListener = shouldAddEchoMessageListener
        } as B

        /**
         * @param length Maximum frame length.
         */
        fun maxFrameLength(length: Int) = apply {
            maxFrameLength = length
        } as B

        fun idleTimeout(timeout: Int) = apply {
            idleTimeout = timeout
        } as B

        fun replyOnError(doReply: Boolean = true) = apply {
            replyOnError = doReply
        } as B

        /**
         * @param addLoggingHandler `true` if [IsoMessageLoggingHandler] should be added to Netty pipeline.
         */
        fun addLoggingHandler(value: Boolean = true) = apply {
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
        fun logSensitiveData(logSensitiveData: Boolean = true) = apply {
            this.logSensitiveData = logSensitiveData
        } as B

        /**
         * @param logFieldDescription `true` to print ISO field descriptions in the log
         */
        fun describeFieldsInLog(shouldDescribe: Boolean = true) = apply {
            logFieldDescription = shouldDescribe
        } as B

        /**
         * @param sensitiveDataFields Array of sensitive fields
         */
        fun sensitiveDataFields(vararg sensitiveDataFields: Int) = apply {
            this.sensitiveDataFields = sensitiveDataFields
        } as B

        fun frameLengthFieldLength(frameLengthFieldLength: Int) = apply {
            this.frameLengthFieldLength = frameLengthFieldLength
        } as B

        fun frameLengthFieldOffset(frameLengthFieldOffset: Int) = apply {
            this.frameLengthFieldOffset = frameLengthFieldOffset
        } as B

        fun frameLengthFieldAdjust(frameLengthFieldAdjust: Int) = apply {
            this.frameLengthFieldAdjust = frameLengthFieldAdjust
        } as B

        fun encodeFrameLengthAsString(encodeFrameLengthAsString: Boolean) = apply {
            this.encodeFrameLengthAsString = encodeFrameLengthAsString
        } as B

        fun workerThreadsCount(numberOfThreads: Int) = apply {
            workerThreadsCount = numberOfThreads
        } as B
    }
}
