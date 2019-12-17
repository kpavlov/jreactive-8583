package com.github.kpavlov.jreactive8583;

import com.github.kpavlov.jreactive8583.netty.pipeline.CompositeIsoMessageHandler;
import com.github.kpavlov.jreactive8583.netty.pipeline.EchoMessageListener;
import com.github.kpavlov.jreactive8583.netty.pipeline.IsoMessageLoggingHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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

    private final boolean addEchoMessageListener;
    private int maxFrameLength;
    private int idleTimeout;
    private final int workerThreadsCount;
    private boolean replyOnError;
    private boolean addLoggingHandler;
    private boolean logSensitiveData;
    private int[] sensitiveDataFields;
    private boolean logFieldDescription;
    private final int frameLengthFieldLength;
    private final int frameLengthFieldOffset;
    private final int frameLengthFieldAdjust;

    protected ConnectorConfiguration(final Builder builder) {
        addLoggingHandler = builder.addLoggingHandler;
        idleTimeout = builder.idleTimeout;
        logFieldDescription = builder.logFieldDescription;
        logSensitiveData = builder.logSensitiveData;
        maxFrameLength = builder.maxFrameLength;
        replyOnError = builder.replyOnError;
        sensitiveDataFields = builder.sensitiveDataFields;
        addEchoMessageListener = builder.addEchoMessageListener;
        workerThreadsCount = builder.workerThreadsCount;
        frameLengthFieldLength = builder.frameLengthFieldLength;
        frameLengthFieldAdjust = builder.frameLengthFieldAdjust;
        frameLengthFieldOffset = builder.frameLengthFieldOffset;
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

    /**
     * Set Idle Timeout in seconds
     *
     * @param idleTimeoutSeconds Idle timeout in seconds
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setIdleTimeout(int idleTimeoutSeconds) {
        this.idleTimeout = idleTimeoutSeconds;
    }

    public int getMaxFrameLength() {
        return maxFrameLength;
    }

    /**
     * @param maxFrameLength the maximum length of the frame.
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setMaxFrameLength(int maxFrameLength) {
        this.maxFrameLength = maxFrameLength;
    }

    /**
     * @param addLoggingHandler should logging handler be added to pipeline
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setAddLoggingHandler(boolean addLoggingHandler) {
        this.addLoggingHandler = addLoggingHandler;
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
     * @param replyOnError should reply on error
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setReplyOnError(boolean replyOnError) {
        this.replyOnError = replyOnError;
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
     * @param logSensitiveData should log sensitive data
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setLogSensitiveData(boolean logSensitiveData) {
        this.logSensitiveData = logSensitiveData;
    }

    public boolean logFieldDescription() {
        return logFieldDescription;
    }

    /**
     * @param logFieldDescription Should field descriptions be printed in log. Useful for when testing system integration.
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setLogFieldDescription(boolean logFieldDescription) {
        this.logFieldDescription = logFieldDescription;
    }

    /**
     * Returns field numbers to be treated as sensitive data.
     * Use <code>null</code> to use default ones
     *
     * @return array of ISO8583 sensitive field numbers to be masked, or <code>null</code> to use default fields.
     * @see IsoMessageLoggingHandler
     */
    public int[] getSensitiveDataFields() {
        return sensitiveDataFields;
    }

    /**
     * @param sensitiveDataFields which fields may contain sensitive data
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setSensitiveDataFields(int[] sensitiveDataFields) {
        this.sensitiveDataFields = sensitiveDataFields;
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

    @SuppressWarnings({"unchecked", "unused"})
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

    @SuppressWarnings({"unchecked", "WeakerAccess", "UnusedReturnValue"})
    protected abstract static class Builder<B extends Builder<B>> {
        private boolean addLoggingHandler = false;
        private boolean addEchoMessageListener = false;
        private boolean logFieldDescription = true;
        private boolean logSensitiveData = true;
        private boolean replyOnError = false;
        private int idleTimeout = DEFAULT_IDLE_TIMEOUT_SECONDS;
        private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;
        private int workerThreadsCount = 0; // use netty default
        private int[] sensitiveDataFields;
        private int frameLengthFieldLength = DEFAULT_FRAME_LENGTH_FIELD_LENGTH;
        private int frameLengthFieldOffset = DEFAULT_FRAME_LENGTH_FIELD_OFFSET;
        private int frameLengthFieldAdjust = DEFAULT_FRAME_LENGTH_FIELD_ADJUST;

        public B addEchoMessageListener() {
            return addEchoMessageListener(true);
        }

        public B addEchoMessageListener(boolean shouldAddEchoMessageListener) {
            this.addEchoMessageListener = shouldAddEchoMessageListener;
            return (B) this;
        }

        /**
         * @param shouldAddEchoMessageListener <code>true</code> to add echo message handler.
         * @return The same {@link Builder}
         * @deprecated Use {@link #addEchoMessageListener(boolean)} instead
         */
        @Deprecated
        public B withEchoMessageListener(boolean shouldAddEchoMessageListener) {
            this.addEchoMessageListener = shouldAddEchoMessageListener;
            return (B) this;
        }

        public B maxFrameLength(int length) {
            this.maxFrameLength = length;
            return (B) this;
        }

        /**
         * @param length Maximum frame length.
         * @return The same {@link Builder}
         * @deprecated Use {@link #maxFrameLength(int)} instead
         */
        @Deprecated
        public B withMaxFrameLength(int length) {
            return maxFrameLength(length);
        }

        public B idleTimeout(int timeout) {
            this.idleTimeout = timeout;
            return (B) this;
        }

        /**
         * Use {@link #idleTimeout(int)} instead
         *
         * @param timeout in seconds
         * @return The same {@link Builder}
         */
        @Deprecated
        public B withIdleTimeout(int timeout) {
            return idleTimeout(timeout);
        }

        public B replyOnError(boolean doReply) {
            this.replyOnError = doReply;
            return (B) this;
        }

        /**
         * @param doReply <code>true</code> if server should reply in case of error.
         * @return The same {@link Builder}
         * @deprecated Use {@link #replyOnError(boolean)} instead
         */
        @Deprecated
        public B withReplyOnError(boolean doReply) {
            return replyOnError(doReply);
        }

        public B addLoggingHandler() {
            this.addLoggingHandler = true;
            return (B) this;
        }

        public B addLoggingHandler(boolean value) {
            this.addLoggingHandler = value;
            return (B) this;
        }

        /**
         * @param addLoggingHandler <code>true</code> if {@link IsoMessageLoggingHandler} should be added to Netty pipeline.
         * @return The same {@link Builder}
         * @deprecated Use {@link #addLoggingHandler()} instead
         */
        public B withAddLoggingHandler(boolean addLoggingHandler) {
            this.addLoggingHandler = addLoggingHandler;
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
        public B logSensitiveData(boolean logSensitiveData) {
            this.logSensitiveData = logSensitiveData;
            return (B) this;
        }

        /**
         * @param logSensitiveData <code>true</code> to log sensitive data via logger
         * @return The same {@link Builder}
         * @deprecated Use {@link #logSensitiveData(boolean)} instead
         */
        public B withLogSensitiveData(boolean logSensitiveData) {
            this.logSensitiveData = logSensitiveData;
            return (B) this;
        }

        public B describeFieldsInLog() {
            this.logFieldDescription = true;
            return (B) this;
        }

        /**
         * @param logFieldDescription <code>true</code> to print ISO field descriptions in the log
         * @return The same {@link Builder}
         * @deprecated Use {@link #describeFieldsInLog()}
         */
        @Deprecated
        public B withLogFieldDescription(boolean logFieldDescription) {
            this.logFieldDescription = logFieldDescription;
            return (B) this;
        }

        public B sensitiveDataFields(int... sensitiveDataFields) {
            this.sensitiveDataFields = sensitiveDataFields;
            return (B) this;
        }

        public B frameLengthFieldLength(int frameLengthFieldLength) {
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

        /**
         * @deprecated Use {@link #sensitiveDataFields(int...)} instead
         * @param sensitiveDataFields Array of sensitive fields
         * @return The same {@link Builder}
         */
        @Deprecated
        public B withSensitiveDataFields(int... sensitiveDataFields) {
            return sensitiveDataFields(sensitiveDataFields);
        }

        public B workerThreadsCount(int numberOfThreads) {
            this.workerThreadsCount = numberOfThreads;
            return (B) this;
        }
    }
}
