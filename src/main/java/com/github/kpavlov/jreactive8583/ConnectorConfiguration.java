package com.github.kpavlov.jreactive8583;

import com.github.kpavlov.jreactive8583.netty.pipeline.CompositeIsoMessageHandler;
import com.github.kpavlov.jreactive8583.netty.pipeline.EchoMessageListener;
import com.github.kpavlov.jreactive8583.netty.pipeline.IsoMessageLoggingHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public abstract class ConnectorConfiguration {

    /**
     * Default read/write idle timeout in seconds (ping interval) = 30 sec.
     *
     * @see #getIdleTimeout()
     */
    private static final int DEFAULT_IDLE_TIMEOUT_SECONDS = 30;

    /**
     * Default {@link #maxFrameLength} (max message length) = 8192
     *
     * @see #getMaxFrameLength()
     */
    private static final int DEFAULT_MAX_FRAME_LENGTH = 8192;

    /**
     * Default {@link #frameLengthFieldLength} (length of TCP Frame length) = 2
     *
     * @see #getFrameLengthFieldLength()
     */
    private static final int DEFAULT_FRAME_LENGTH_FIELD_LENGTH = 2;

    /**
     * Default {@link #frameLengthFieldAdjust} (compensation value to add to the value of the length field) = 0
     *
     * @see #getFrameLengthFieldAdjust()
     */
    private static final int DEFAULT_FRAME_LENGTH_FIELD_ADJUST = 0;

    /**
     * Default {@link #frameLengthFieldOffset} (the offset of the length field) = 0
     *
     * @see #getFrameLengthFieldOffset()
     */
    private static final int DEFAULT_FRAME_LENGTH_FIELD_OFFSET = 0;

    /**
     * Default list of data fields containing sensitive data
     */
    public static final int[] DEFAULT_SENSITIVE_DATA_FIELDS = IsoMessageLoggingHandler.DEFAULT_MASKED_FIELDS;

    private final boolean addEchoMessageListener;
    private final int maxFrameLength;
    private final int idleTimeout;
    private final int workerThreadsCount;
    private final boolean replyOnError;
    private final boolean addLoggingHandler;
    private final boolean logSensitiveData;
    private final boolean encodeFrameLengthAsString;
    private final int[] sensitiveDataFields;
    private final boolean logFieldDescription;
    private final int frameLengthFieldLength;
    private final int frameLengthFieldOffset;
    private final int frameLengthFieldAdjust;

    protected ConnectorConfiguration(final Builder<?> builder) {
        addLoggingHandler = builder.addLoggingHandler;
        idleTimeout = builder.idleTimeout;
        logFieldDescription = builder.logFieldDescription;
        logSensitiveData = builder.logSensitiveData;
        maxFrameLength = builder.maxFrameLength;
        replyOnError = builder.replyOnError;
        sensitiveDataFields = Objects.requireNonNull(builder.sensitiveDataFields, "sensitiveDataFields");
        addEchoMessageListener = builder.addEchoMessageListener;
        workerThreadsCount = builder.workerThreadsCount;
        frameLengthFieldLength = builder.frameLengthFieldLength;
        frameLengthFieldAdjust = builder.frameLengthFieldAdjust;
        frameLengthFieldOffset = builder.frameLengthFieldOffset;
        encodeFrameLengthAsString = builder.encodeFrameLengthAsString;
    }

    /**
     * Allows to add default echo message listener to {@link AbstractIso8583Connector}.
     *
     * @return true if {@link EchoMessageListener} should be added to {@link CompositeIsoMessageHandler}
     */

    public boolean shouldAddEchoMessageListener() {
        return addEchoMessageListener;
    }

    /**
     * Channel read/write idle timeout in seconds.
     * <p>
     * If no message was received/sent during specified time interval then `Echo` message will be sent.</p>
     *
     * @return timeout in seconds
     */
    public int getIdleTimeout() {
        return idleTimeout;
    }

    public int getMaxFrameLength() {
        return maxFrameLength;
    }

    /**
     * Returns true is {@link IsoMessageLoggingHandler}
     * <p>Allows to disable adding default logging handler to {@link ChannelPipeline}.</p>
     *
     * @return true if {@link IsoMessageLoggingHandler} should be added.
     */
    public boolean addLoggingHandler() {
        return addLoggingHandler;
    }

    /**
     * Whether to reply with administrative message in case of message syntax errors. Default value is <code>false.</code>
     *
     * @return true if reply message should be sent in case of error parsing the message.
     */
    public boolean replyOnError() {
        return replyOnError;
    }

    /**
     * Returns <code>true</code> if sensitive information like PAN, CVV/CVV2, and Track2 should be printed to log.
     * <p>
     * Default value is <code>true</code> (sensitive data is printed).
     * </p>
     *
     * @return <code>true</code> if sensitive data should be printed to log
     */
    public boolean logSensitiveData() {
        return logSensitiveData;
    }

    /**
     * Returns <code>true</code> if the length header is to be encoded as a String, as opposed to the default binary
     * <p>
     * Default value is <code>false</code> (frame length header is binary encoded).
     * </p>
     * <p>
     * Used with @link frameLengthFieldLength, @link frameLengthFieldOffset and @link frameLengthFieldAdjust
     * </p>
     *
     * @return <code>true</code> if frame length header is binary encoded
     */
    public boolean encodeFrameLengthAsString() {
        return encodeFrameLengthAsString;
    }

    public boolean logFieldDescription() {
        return logFieldDescription;
    }

    /**
     * Returns field numbers to be treated as sensitive data.
     * Use <code>null</code> to use default ones
     *
     * @return array of ISO8583 sensitive field numbers to be masked,
     * or <code>null</code> to use default fields.
     * @see IsoMessageLoggingHandler
     */
    public int[] getSensitiveDataFields() {
        return sensitiveDataFields;
    }

    /**
     * Returns number of threads in worker {@link EventLoopGroup}.
     * <p>
     * Default value is <code>Runtime.getRuntime().availableProcessors() * 16</code>.
     *
     * @return Number of Netty worker threads
     */
    public int getWorkerThreadsCount() {
        return workerThreadsCount;
    }

    /**
     * Returns length of TCP frame length field.
     * <p>
     * Default value is <code>2</code>.
     *
     * @return Length of TCP frame length field.
     * @see LengthFieldBasedFrameDecoder
     */
    public int getFrameLengthFieldLength() {
        return frameLengthFieldLength;
    }

    /**
     * Returns the offset of the length field.
     *
     * Default value is <code>0</code>.
     * @see LengthFieldBasedFrameDecoder
     *
     * @return The offset of the length field.
     */
    public int getFrameLengthFieldOffset() {
        return frameLengthFieldOffset;
    }

    /**
     * Returns the compensation value to add to the value of the length field.
     * <p>
     * Default value is <code>0</code>.
     *
     * @return The compensation value to add to the value of the length field
     * @see LengthFieldBasedFrameDecoder
     */
    public int getFrameLengthFieldAdjust() {
        return frameLengthFieldAdjust;
    }

    @SuppressWarnings({"unchecked", "WeakerAccess", "UnusedReturnValue", "unused"})
    protected abstract static class Builder<B extends Builder<B>> {
        private boolean addLoggingHandler = false;
        private boolean addEchoMessageListener = false;
        private boolean logFieldDescription = true;
        private boolean logSensitiveData = true;
        private boolean replyOnError = false;
        private boolean encodeFrameLengthAsString = false;
        private int idleTimeout = DEFAULT_IDLE_TIMEOUT_SECONDS;
        private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;
        private int workerThreadsCount = 0; // use netty default
        private int[] sensitiveDataFields = DEFAULT_SENSITIVE_DATA_FIELDS;
        private int frameLengthFieldLength = DEFAULT_FRAME_LENGTH_FIELD_LENGTH;
        private int frameLengthFieldOffset = DEFAULT_FRAME_LENGTH_FIELD_OFFSET;
        private int frameLengthFieldAdjust = DEFAULT_FRAME_LENGTH_FIELD_ADJUST;

        public B addEchoMessageListener() {
            return addEchoMessageListener(true);
        }

        /**
         * @param shouldAddEchoMessageListener <code>true</code> to add echo message handler.
         * @return The same {@link Builder}
         */
        public B addEchoMessageListener(final boolean shouldAddEchoMessageListener) {
            this.addEchoMessageListener = shouldAddEchoMessageListener;
            return (B) this;
        }

        /**
         * @param length Maximum frame length.
         * @return The same {@link Builder}
         */
        public B maxFrameLength(final int length) {
            this.maxFrameLength = length;
            return (B) this;
        }

        /**
         * Specify idle timeout in seconds
         *
         * @param timeoutSeconds in seconds
         * @return The same {@link Builder}
         */
        public B idleTimeout(final int timeoutSeconds) {
            this.idleTimeout = timeoutSeconds;
            return (B) this;
        }

        /**
         * @param doReply <code>true</code> if server should reply in case of error.
         * @return The same {@link Builder}
         */
        public B replyOnError(final boolean doReply) {
            this.replyOnError = doReply;
            return (B) this;
        }

        public B addLoggingHandler() {
            this.addLoggingHandler = true;
            return (B) this;
        }

        /**
         * @param useLoggingHandler <code>true</code> if {@link IsoMessageLoggingHandler}
         *                          should be added to Netty pipeline.
         * @return The same {@link Builder}
         */
        public B addLoggingHandler(final boolean useLoggingHandler) {
            this.addLoggingHandler = useLoggingHandler;
            return (B) this;
        }

        /**
         * Should log sensitive data (unmasked) or not.
         * <p>
         * <strong>Don't use on production!</strong>
         *
         * @param logSensitiveData <code>true</code> to log sensitive data via logger
         * @return The same {@link Builder}
         */
        public B logSensitiveData(final boolean logSensitiveData) {
            this.logSensitiveData = logSensitiveData;
            return (B) this;
        }

        /**
         * Should encode frame length header as string, as opposed to default binary encoding.
         * <p>
         * Used with @link frameLengthFieldLength, @link frameLengthFieldOffset and @link frameLengthFieldAdjust
         *
         * @param encodeFrameLengthAsString <code>true</code> to encode frame length header as String
         * @return The same {@link Builder}
         */
        public B encodeFrameLengthAsString(final Boolean encodeFrameLengthAsString) {
            this.encodeFrameLengthAsString = encodeFrameLengthAsString;
            return (B) this;
        }

        /**
         * Print ISO field descriptions in the log
         *
         * @return The same {@link Builder}
         */
        public B describeFieldsInLog() {
            this.logFieldDescription = true;
            return (B) this;
        }

        /**
         * @param logFieldDescription <code>true</code> to print ISO field descriptions in the log
         * @return The same {@link Builder}
         */
        public B describeFieldsInLog(final boolean logFieldDescription) {
            this.logFieldDescription = logFieldDescription;
            return (B) this;
        }

        /**
         * Provide list of sensitive data fields that should be masked in logs.
         * If not specified then {@link #DEFAULT_SENSITIVE_DATA_FIELDS} are used.
         *
         * @param sensitiveDataFields Array of sensitive data fields
         * @return The same {@link Builder}
         * @see #DEFAULT_SENSITIVE_DATA_FIELDS
         */
        public B sensitiveDataFields(final int... sensitiveDataFields) {
            this.sensitiveDataFields = sensitiveDataFields;
            return (B) this;
        }

        public B frameLengthFieldLength(final int frameLengthFieldLength) {
            this.frameLengthFieldLength = frameLengthFieldLength;
            return (B) this;
        }

        public B frameLengthFieldOffset(final int frameLengthFieldOffset) {
            this.frameLengthFieldOffset = frameLengthFieldOffset;
            return (B) this;
        }

        public B frameLengthFieldAdjust(final int frameLengthFieldAdjust) {
            this.frameLengthFieldAdjust = frameLengthFieldAdjust;
            return (B) this;
        }

        public B workerThreadsCount(final int numberOfThreads) {
            this.workerThreadsCount = numberOfThreads;
            return (B) this;
        }
    }
}
