# JReactive-8583

ISO8583 Java Connector

[![Build Status](https://travis-ci.org/kpavlov/jreactive-8583.png?branch=master)](https://travis-ci.org/kpavlov/jreactive-8583)

## Motivation

1. [jPOS][jpos] library is not free for commercial use. 
2. [j8583][j8583] is free but does not offer network client

Solution: **"J-Reactive-8583"** ISO8583 Client and Server built on top of excellent Netty asynchronous messaging framework with the help of j8583.

## Supported Features

* Client and Server endpoints.
* Support ISO8583 messages using [j8583][j8583] library.
* Customizable [ISO MessageFactory][j8583-message-factory].
* Automatic responding to Echo messages.
* Automatic client reconnection.
* Secure [message logger](https://github.com/kpavlov/jreactive-8583/blob/master/src/main/java/org/jreactive/iso8583/netty/pipeline/IsoMessageLoggingHandler.java): mask PAN and track data or any any other field (customizable). Optionally prints field descriptions.
 * Configurable netty [Bootstrap](https://github.com/netty/netty/blob/master/transport/src/main/java/io/netty/bootstrap/Bootstrap.java) and [ChannelPipeline](https://github.com/netty/netty/blob/master/transport/src/main/java/io/netty/channel/ChannelPipeline.java)

## ISO8583 TCP/IP Transport

For data transmission TCP/IP uses sessions.
Each session is a bi-directional data stream. 
The protocol uses a single TCP/IP session to transfer data between hosts in both directions. 

The continuous TCP/IP data stream is split into frames. 
Each [ISO8583][iso8583] message is sent in a separate frame. 

A Frame consists of a N-byte length header and a message body.
Usually, N==2.
The header contains the length of the following message.
The high byte of value is transmitted first, and the low byte of value is transmitted second.

| N bytes            | M bytes            |
| ------------------ | ------------------ |
| Message Length = M | ISO–8583 Message |

# Getting Started

First, you need to [download latest release](https://github.com/kpavlov/jreactive-8583/releases) or clone this repository and build artifact with maven manually. 

Then add dependency to your project:

    <dependencies>
        <dependency>
            <groupId>org.jreactive</groupId>
            <artifactId>netty-iso8583</artifactId>
            <version>0.1.1</version>
        </dependency>
        ...
    <dependencies>
    
Now you may use ISO8583 client or server in your code.

# Creating and Using ISO-8583 Client

The minimal client workflow includes:

    MessageFactory<IsoMessage> messageFactory = ConfigParser.createDefault();// [1]
    Iso8583Client<IsoMessage> client = new Iso8583Client<>(clientMessageFactory());// [2]

    client.addMessageListener(new IsoMessageListener<IsoMessage>() { // [3]
        ...
    });
    client.getConfiguration().replyOnError(true);// [4]
    client.init();// [5]
    
    client.connect(host, port);// [6]
    if (client.isConnected()) { // [7]
    
        IsoMessage message = messageFactory.newMessage(...);
        ...
        client.send(message);// [8]
    }
    
    ...
    client.shutdown();// [9]
    
1. First you need to create a `MessageFactory`
2. Then you create a [`Iso8583Client`][Iso8583Client] providing `MessageFactory` and, optionally, `SocketAddress` 
3. Add one or more custom [`IsoMessageListener`][IsoMessageListener]s to handle `IsoMessage`s.
4. Configure the client. You may omit this step if you're fine with default configuration.
5. Initialize client. Now it is ready to connect.
6. Establish a connection. By default, if connection will is lost, it reconnects automatically. You may disable this behaviour or change _reconnectInterval_.
7. Verify that connection is established
8. Send `IsoMessage`
9. Disconnect when you're done.

# Creating and Using ISO-8583 Server

Typical server workflow includes:

    MessageFactory<IsoMessage> messageFactory = ConfigParser.createDefault();// [1]
    Iso8583Server<IsoMessage> server = new Iso8583Server<>(port, serverMessageFactory());// [2]

    server.addMessageListener(new IsoMessageListener<IsoMessage>() { // [3]
        ...
    });
    server.getConfiguration().replyOnError(true);// [4]
    server.init();// [5]
    
    server.start();// [6]
    if (server.isStarted()) { // [7]
        ...
    }
    
    ...
    server.shutdown();// [8]
    
1. First you need to create a `MessageFactory`
2. Then you create a [`Iso8583Server`][Iso8583Server] providing `MessageFactory` and port to bind to 
3. Add one or more custom [`IsoMessageListener`][IsoMessageListener]s to handle `IsoMessage`s.
4. Configure the server. You may omit this step if you're fine with default configuration.
5. Initialize server. Now it is ready to start.
6. Start server. Now it is ready to accept client connections.
7. Verify that the server is started
9. Shutdown server when you're done.

## ISO 8583 Links 

- Beginner's guide: http://www.lytsing.org/downloads/iso8583.pdf.
- Introduction to ISO8583: http://www.codeproject.com/Articles/100084/Introduction-to-ISO-8583.

[iso8583]: https://en.wikipedia.org/wiki/ISO_8583
[iso-examples]: https://github.com/beckerdo/ISO-8583-Examples "Some payments processing examples"
[j8583-example]: https://krishnarag.wordpress.com/2014/06/18/iso-8583-j8583-java-library/
[j8583]: https://github.com/chochos/j8583 "Java implementation of the ISO8583 protocol."
[j8583-message-factory]: https://github.com/chochos/j8583/blob/master/src/main/java/com/solab/iso8583/IsoMessage.java
[jpos]: http://jpos.org 

[Iso8583Client]: https://github.com/kpavlov/jreactive-8583/blob/master/src/main/java/org/jreactive/iso8583/client/Iso8583Client.java
[Iso8583Server]: https://github.com/kpavlov/jreactive-8583/blob/master/src/main/java/org/jreactive/iso8583/server/Iso8583Server.java
[IsoMessageListener]: https://github.com/kpavlov/jreactive-8583/blob/master/src/main/java/org/jreactive/iso8583/IsoMessageListener.java
