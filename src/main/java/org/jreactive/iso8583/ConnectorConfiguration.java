package org.jreactive.iso8583;

public abstract class ConnectorConfiguration {

    /**
     * Default read/write idle timeout in seconds (ping interval) = 30 sec.
     *
     * @see #setIdleTimeout(int)
     */
    public static final int DEFAULT_IDLE_TIMEOUT = 30;

    /**
     * Default {@link #maxFrameLength} (max message length) = 8192
     *
     * @see #setMaxFrameLength(int)
     */
    public static final int DEFAULT_MAX_FRAME_LENGTH = 8192;

    private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;

    private int idleTimeout = DEFAULT_IDLE_TIMEOUT;
    private boolean replyOnError = false;

    private boolean addLoggingHandler = true;
    private boolean logSensitiveData = true;
    private boolean logFieldDescription = true;
    private int[] sensitiveDataFields;
    private boolean addEchoMessageListener = true;
    
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
     */
    public void setIdleTimeout(int idleTimeoutSeconds) {
        this.idleTimeout = idleTimeoutSeconds;
    }

    public int getMaxFrameLength() {
        return maxFrameLength;
    }

    public void setMaxFrameLength(int maxFrameLength) {
        this.maxFrameLength = maxFrameLength;
    }

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

    public void setLogSensitiveData(boolean logSensitiveData) {
        this.logSensitiveData = logSensitiveData;
    }

    public boolean logFieldDescription() {
        return logFieldDescription;
    }

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

    public void setSensitiveDataFields(int[] sensitiveDataFields) {
        this.sensitiveDataFields = sensitiveDataFields;
    }

    /**
     * Returns true is {@link org.jreactive.iso8583.netty.pipeline.EchoMessageListener}
     * <p>Allows to disable adding default network management or echo message listener to {@link org.jreactive.iso8583.AbstractIso8583Connector}.</p>
     *
     * @return true if {@link org.jreactive.iso8583.netty.pipeline.EchoMessageListener} should be added.
     */
		public boolean addEchoMessageListener() {
			return addEchoMessageListener;
		}

		/**
		 * see {@link #addEchoMessageListener()}
		 * @param addEchoMessageListener the addEchoMessageListener to set
		 */
		public void addEchoMessageListener(boolean addEchoMessageListener) {
			this.addEchoMessageListener = addEchoMessageListener;
		}
}
