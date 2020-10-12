@file:JvmName("AbstractIso8583Connector")

package com.github.kpavlov.jreactive8583

import com.github.kpavlov.jreactive8583.iso.MessageFactory
import com.github.kpavlov.jreactive8583.netty.pipeline.CompositeIsoMessageHandler
import com.github.kpavlov.jreactive8583.netty.pipeline.EchoMessageListener
import com.solab.iso8583.IsoMessage
import io.netty.bootstrap.AbstractBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Boolean.parseBoolean
import java.util.concurrent.atomic.AtomicReference

public abstract class AbstractIso8583Connector<
    C : ConnectorConfiguration,
    B : AbstractBootstrap<B, *>,
    M : IsoMessage>
internal constructor(
    configuration: C,
    isoMessageFactory: MessageFactory<M>,
    messageHandler: CompositeIsoMessageHandler<M> = CompositeIsoMessageHandler()
) {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    internal val messageHandler: CompositeIsoMessageHandler<M>
    public val isoMessageFactory: MessageFactory<M> = isoMessageFactory
    private val channelRef = AtomicReference<Channel>()
    protected val configuration: C = configuration
    public var configurer: ConnectorConfigurer<C, B>? = null
    protected lateinit var bossEventLoopGroup: EventLoopGroup
        private set
    protected lateinit var workerEventLoopGroup: EventLoopGroup
    protected lateinit var bootstrap: B

    public fun addMessageListener(handler: IsoMessageListener<M>) {
        messageHandler.addListener(handler)
    }

    public fun removeMessageListener(handler: IsoMessageListener<M>) {
        messageHandler.removeListener(handler)
    }

    /**
     * Making connector ready to create a connection / bind to port.
     * Creates a Bootstrap
     *
     * @see AbstractBootstrap
     */
    public fun init() {
        logger.info("Initializing")
        bossEventLoopGroup = createBossEventLoopGroup()
        workerEventLoopGroup = createWorkerEventLoopGroup()
        bootstrap = createBootstrap()
    }

    public open fun shutdown() {
        workerEventLoopGroup.shutdownGracefully()
        bossEventLoopGroup.shutdownGracefully()
    }

    protected fun configureBootstrap(bootstrap: B) {
        bootstrap.option(
            ChannelOption.TCP_NODELAY,
            parseBoolean(
                System.getProperty(
                    "nfs.rpc.tcp.nodelay", "true"
                )
            )
        )
            .option(ChannelOption.AUTO_READ, true)
        configurer?.configureBootstrap(bootstrap, configuration)
    }

    protected abstract fun createBootstrap(): B

    protected fun createBossEventLoopGroup(): EventLoopGroup {
        return NioEventLoopGroup()
    }

    protected fun createWorkerEventLoopGroup(): EventLoopGroup {
        val group = NioEventLoopGroup(configuration.workerThreadsCount)
        logger.debug(
            "Created worker EventLoopGroup with {} executor threads",
            group.executorCount()
        )
        return group
    }

    protected var channel: Channel?
        get() = channelRef.get()
        protected set(channel) {
            channelRef.set(channel)
        }

    // @VisibleForTest
    init {
        this.messageHandler = messageHandler
        if (configuration.shouldAddEchoMessageListener()) {
            messageHandler.addListener(EchoMessageListener(isoMessageFactory))
        }
    }
}
