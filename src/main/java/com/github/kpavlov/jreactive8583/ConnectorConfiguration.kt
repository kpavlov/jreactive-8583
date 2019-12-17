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
     * @param maxFrameLength the maximum length of the frame.
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

    private var replyOnError: Boolean
    private var addLoggingHandler: Boolean
    private var logSensitiveData: Boolean
    /**
     * Returns field numbers to be treated as sensitive data.
     * Use `null` to use default ones
     *
     * @return array of ISO8583 sensitive field numbers to be masked, or `null` to use default fields.
     * @see IsoMessageLoggingHandler
     */
    /**
     * @param sensitiveDataFields which fields may contain sensitive data
     */
    @set:Deprecated("Use {@link Builder}")
    var sensitiveDataFields: IntArray
    private var logFieldDescription: Boolean
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
     * Allows to add default echo message listener to [AbstractIso8583Connector].
     *
     * @return true if [EchoMessageListener] should be added to [CompositeIsoMessageHandler]
     */
    fun shouldAddEchoMessageListener(): Boolean {
        return addEchoMessageListener
    }

    /**
     * @param addLoggingHandler should logging handler be added to pipeline
     */
    @Deprecated("Use {@link Builder}")
    fun setAddLoggingHandler(addLoggingHandler: Boolean) {
        this.addLoggingHandler = addLoggingHandler
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
     * @param replyOnError should reply on error
     */
    @Deprecated("Use {@link Builder}")
    fun setReplyOnError(replyOnError: Boolean) {
        this.replyOnError = replyOnError
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

    /**
     * @param logSensitiveData should log sensitive data
     */
    @Deprecated("Use {@link Builder}")
    fun setLogSensitiveData(logSensitiveData: Boolean) {
        this.logSensitiveData = logSensitiveData
    }

    fun logFieldDescription(): Boolean {
        return logFieldDescription
    }

    /**
     * @param logFieldDescription Should field descriptions be printed in log. Useful for when testing system integration.
     */
    @Deprecated("Use {@link Builder}")
    fun setLogFieldDescription(logFieldDescription: Boolean) {
        this.logFieldDescription = logFieldDescription
    }

    init {
        this.addEchoMessageListener = b.addEchoMessageListener
        this.maxFrameLength = b.maxFrameLength
        this.idleTimeout = b.idleTimeout
        this.logFieldDescription = b.logFieldDescription
        this.addLoggingHandler = b.addLoggingHandler
        this.frameLengthFieldAdjust = b.frameLengthFieldAdjust
        this.workerThreadsCount = b.workerThreadsCount
        this.frameLengthFieldLength = b.frameLengthFieldLength
        this.frameLengthFieldOffset = b.frameLengthFieldOffset
        this.logSensitiveData = b.logSensitiveData
        this.replyOnError = b.replyOnError
        this.sensitiveDataFields = b.sensitiveDataFields
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


        fun workerThreadsCount(numberOfThreads: Int) = apply {
            workerThreadsCount = numberOfThreads
        } as B
    }
}
