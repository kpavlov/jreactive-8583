package com.github.kpavlov.jreactive8583.netty.pipeline

import com.github.kpavlov.jreactive8583.IsoMessageListener
import com.solab.iso8583.IsoMessage
import io.netty.channel.ChannelHandlerContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CompositeIsoMessageHandlerTest {

    @Mock
    private lateinit var listener1: IsoMessageListener<IsoMessage>

    @Mock
    private lateinit var listener2: IsoMessageListener<IsoMessage>

    @Mock
    private lateinit var listener3: IsoMessageListener<IsoMessage>

    @Mock
    private lateinit var message: IsoMessage

    @Mock
    private lateinit var ctx: ChannelHandlerContext
    private var handler = CompositeIsoMessageHandler<IsoMessage>()

    @BeforeEach
    fun setUp() {
        handler.addListeners(listener1, listener2, listener3)
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveListener() {
        //given
        Mockito.`when`(listener1.applies(message)).thenReturn(true)
        Mockito.`when`(listener3.applies(message)).thenReturn(true)
        Mockito.`when`(listener1.onMessage(ctx, message)).thenReturn(true)

        //when
        handler.removeListener(listener2)
        handler.channelRead(ctx, message)

        //then
        Mockito.verify(listener1).onMessage(ctx, message)
        Mockito.verify(listener2, Mockito.never()).onMessage(ctx, message)
        Mockito.verify(listener3).onMessage(ctx, message)
    }

    @Test
    @Throws(Exception::class)
    fun shouldHandleWithAppropriateHandler() {
        //given
        Mockito.`when`(listener1.applies(message)).thenReturn(false)
        Mockito.`when`(listener2.applies(message)).thenReturn(true)
        Mockito.`when`(listener3.applies(message)).thenReturn(false)
        Mockito.`when`(listener2.onMessage(ctx, message)).thenReturn(true)

        //when
        handler.channelRead(ctx, message)

        //then
        Mockito.verify(listener1, Mockito.never()).onMessage(ctx, message)
        Mockito.verify(listener2).onMessage(ctx, message)
        Mockito.verify(listener3, Mockito.never()).onMessage(ctx, message)
    }

    @Test
    @Throws(Exception::class)
    fun testStopProcessing() {
        //given
        Mockito.`when`(listener1.applies(message)).thenReturn(true)
        Mockito.`when`(listener2.applies(message)).thenReturn(true)
        Mockito.`when`(listener1.onMessage(ctx, message)).thenReturn(true)
        Mockito.`when`(listener2.onMessage(ctx, message)).thenReturn(false)

        //when
        handler.channelRead(ctx, message)

        //then
        Mockito.verify(listener1).onMessage(ctx, message)
        Mockito.verify(listener2).onMessage(ctx, message)
        Mockito.verify(listener3, Mockito.never()).onMessage(ctx, message)
    }

    @Test
    @Throws(Exception::class)
    fun shouldNotFailOnExceptionInFailsafeMode() {
        //given
        handler = CompositeIsoMessageHandler(false)
        handler.addListeners(listener1, listener2, listener3)
        Mockito.`when`(listener1.applies(message)).thenReturn(true)
        Mockito.`when`(listener2.applies(message)).thenThrow(RuntimeException("Expected exception"))
        Mockito.`when`(listener3.applies(message)).thenReturn(true)
        Mockito.`when`(listener1.onMessage(ctx, message)).thenReturn(true)

        // when
        handler.channelRead(ctx, message)

        //then
        Mockito.verify(listener1).onMessage(ctx, message)
        Mockito.verify(listener2, Mockito.never()).onMessage(ctx, message)
        Mockito.verify(listener3).onMessage(ctx, message)
    }
}
