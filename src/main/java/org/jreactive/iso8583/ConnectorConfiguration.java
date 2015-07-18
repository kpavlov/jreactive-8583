package org.jreactive.iso8583;

public abstract class ConnectorConfiguration {

    /**
     *
     */
    public static final int DEFAULT_IDLE_TIMEOUT = 30;

    /**
     * Default {@link #maxFrameLength} (max message length) = 4096
     */
    public static final int DEFAULT_MAX_FRAME_LENGTH = 4096;

    private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;

    private int idleTimeout = DEFAULT_IDLE_TIMEOUT;
    private boolean replyOnError = false;

    private boolean addLoggingHandler = true;

    /**
     * Channel read/write idle timeout in seconds.
     * <p>
     * If no message was received/sent during specified time interval then `Echo` message will be sent.
     *
     * @return timeout in seconds
     */
    public int getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * Set Idle Timeout in seconds
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
     * <p>
     * Allows to disable adding default logging handler to {@link io.netty.channel.ChannelPipeline}.
     *
     * @return true if {@link org.jreactive.iso8583.netty.pipeline.IsoMessageLoggingHandler} should be added.
     */
    public boolean addLoggingHandler() {
        return addLoggingHandler;
    }

    /**
     * Whether to reply with administrative message in case of message syntax errors. Default value is <code>false.</code>
     */
    public boolean replyOnError() {
        return replyOnError;
    }
}
