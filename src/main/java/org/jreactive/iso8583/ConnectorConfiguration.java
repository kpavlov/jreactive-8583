package org.jreactive.iso8583;

public abstract class ConnectorConfiguration {

    public static final int DEFAULT_IDLE_TIMEOUT = 30;
    public static final int DEFAULT_MAX_FRAME_LENGTH = 32768;

    private int maxFrameLength = DEFAULT_MAX_FRAME_LENGTH;

    private int idleTimeout = DEFAULT_IDLE_TIMEOUT;

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
}
