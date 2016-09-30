package org.jreactive.iso8583;

public abstract class ConnectorConfiguration {

    /**
     * Default read/write idle timeout in seconds (ping interval) = 30 sec.
     *
     * @see #setIdleTimeout(int)
     */
    public static final int DEFAULT_IDLE_TIMEOUT_SECONDS = 30;

    /**
     * Default {@link #maxFrameLength} (max message length) = 8192
     *
     * @see #setMaxFrameLength(int)
     */
    public static final int DEFAULT_MAX_FRAME_LENGTH = 8192;
    private final boolean addEchoMessageListener;
    private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;
    private int idleTimeout = DEFAULT_IDLE_TIMEOUT_SECONDS;
    private boolean replyOnError = false;
    private boolean addLoggingHandler = true;
    private boolean logSensitiveData = true;
    private boolean logFieldDescription = true;
    private int[] sensitiveDataFields;


    protected ConnectorConfiguration(Builder builder) {
        addLoggingHandler = builder.addLoggingHandler;
        idleTimeout = builder.idleTimeout;
        logFieldDescription = builder.logFieldDescription;
        logSensitiveData = builder.logSensitiveData;
        maxFrameLength = builder.maxFrameLength;
        replyOnError = builder.replyOnError;
        sensitiveDataFields = builder.sensitiveDataFields;
        this.addEchoMessageListener = builder.addEchoMessageListener;
    }

    /**
     * Allows to add default echo message listener to {@link org.jreactive.iso8583.AbstractIso8583Connector}.
     *
     * @return true if  {@link org.jreactive.iso8583.netty.pipeline.EchoMessageListener} should be added to {@link org.jreactive.iso8583.netty.pipeline.CompositeIsoMessageHandler}
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
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setMaxFrameLength(int maxFrameLength) {
        this.maxFrameLength = maxFrameLength;
    }

    /**
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setAddLoggingHandler(boolean addLoggingHandler) {
        this.addLoggingHandler = addLoggingHandler;
    }

    /**
     * Returns true is {@link org.jreactive.iso8583.netty.pipeline.IsoMessageLoggingHandler}
     * <p>Allows to disable adding default logging handler to {@link io.netty.channel.ChannelPipeline}.</p>
     *
     * @return true if {@link org.jreactive.iso8583.netty.pipeline.IsoMessageLoggingHandler} should be added.
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
     * @see org.jreactive.iso8583.netty.pipeline.IsoMessageLoggingHandler
     * @see org.jreactive.iso8583.netty.pipeline.IsoMessageLoggingHandler#DEFAULT_MASKED_FIELDS
     */
    public int[] getSensitiveDataFields() {
        return sensitiveDataFields;
    }

    /**
     * @deprecated Use {@link Builder}
     */
    @Deprecated
    public void setSensitiveDataFields(int[] sensitiveDataFields) {
        this.sensitiveDataFields = sensitiveDataFields;
    }


    @SuppressWarnings("unchecked")
    protected abstract static class Builder<B extends Builder> {
        private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;

        private int idleTimeout = DEFAULT_IDLE_TIMEOUT_SECONDS;
        private boolean replyOnError = false;

        private boolean addLoggingHandler = true;
        private boolean logSensitiveData = true;
        private boolean logFieldDescription = true;
        private int[] sensitiveDataFields;
        private boolean addEchoMessageListener;

        public B withEchoMessageListener(boolean shouldAddEchoMessageListener) {
            this.addEchoMessageListener = shouldAddEchoMessageListener;
            return (B) this;
        }

        public B withMaxFrameLength(int maxFrameLength) {
            this.maxFrameLength = maxFrameLength;
            return (B) this;
        }

        public B withIdleTimeout(int idleTimeout) {
            this.idleTimeout = idleTimeout;
            return (B) this;
        }

        public B withReplyOnError(boolean replyOnError) {
            this.replyOnError = replyOnError;
            return (B) this;
        }

        public B withAddLoggingHandler(boolean addLoggingHandler) {
            this.addLoggingHandler = addLoggingHandler;
            return (B) this;
        }

        public B withLogSensitiveData(boolean logSensitiveData) {
            this.logSensitiveData = logSensitiveData;
            return (B) this;
        }

        public B withLogFieldDescription(boolean logFieldDescription) {
            this.logFieldDescription = logFieldDescription;
            return (B) this;
        }

        public B withSensitiveDataFields(int... sensitiveDataFields) {
            this.sensitiveDataFields = sensitiveDataFields;
            return (B) this;
        }


    }
}
