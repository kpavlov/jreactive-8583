package com.github.kpavlov.jreactive8583;

import com.github.kpavlov.jreactive8583.netty.pipeline.CompositeIsoMessageHandler;
import com.github.kpavlov.jreactive8583.netty.pipeline.EchoMessageListener;
import com.github.kpavlov.jreactive8583.netty.pipeline.IsoMessageLoggingHandler;
import io.netty.channel.EventLoopGroup;

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

    private final boolean addEchoMessageListener;
    private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;
    private int idleTimeout = DEFAULT_IDLE_TIMEOUT_SECONDS;
    private final int workerThreadsCount;
    private boolean replyOnError;
    private boolean addLoggingHandler;
    private boolean logSensitiveData;
    private int[] sensitiveDataFields;
    private boolean logFieldDescription;

    protected ConnectorConfiguration(Builder builder) {
        addLoggingHandler = builder.addLoggingHandler;
        idleTimeout = builder.idleTimeout;
        logFieldDescription = builder.logFieldDescription;
        logSensitiveData = builder.logSensitiveData;
        maxFrameLength = builder.maxFrameLength;
        replyOnError = builder.replyOnError;
        sensitiveDataFields = builder.sensitiveDataFields;
        addEchoMessageListener = builder.addEchoMessageListener;
        workerThreadsCount = builder.workerThreadsCount;
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
     * <p>Allows to disable adding default logging handler to {@link io.netty.channel.ChannelPipeline}.</p>
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
     * @see IsoMessageLoggingHandler#DEFAULT_MASKED_FIELDS
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
     *
     * @implNote Default value is <code>Runtime.getRuntime().availableProcessors() * 16</code>
     */
    public int getWorkerThreadsCount() {
        return workerThreadsCount;
    }

    @SuppressWarnings({"unchecked", "unused"})
    protected abstract static class Builder<B extends Builder> {
        private boolean addLoggingHandler = true;
        private boolean addEchoMessageListener = false;
        private boolean logFieldDescription = true;
        private boolean logSensitiveData = true;
        private boolean replyOnError = false;
        private int idleTimeout = DEFAULT_IDLE_TIMEOUT_SECONDS;
        private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;
        private int workerThreadsCount = Runtime.getRuntime().availableProcessors() * 16;
        private int[] sensitiveDataFields;

        public B addEchoMessageListener() {
            this.addEchoMessageListener = true;
            return (B) this;
        }

        /**
         * @deprecated Use {@link #addEchoMessageListener()} instead
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

        /**
         * @deprecated Use {@link #addLoggingHandler()} instead
         */
        public B withAddLoggingHandler(boolean addLoggingHandler) {
            this.addLoggingHandler = addLoggingHandler;
            return (B) this;
        }

        /**
         * Should log sensitive data (unmasked) or not.
         * <p>
         * Don't use on production!
         */
        public B logSensitiveData(boolean logSensitiveData) {
            this.logSensitiveData = logSensitiveData;
            return (B) this;
        }

        /**
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

        /**
         * @deprecated Use {@link #sensitiveDataFields(int...)} instead
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
