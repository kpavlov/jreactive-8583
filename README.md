# JReactive-8583

Free ISO8583 Connector for JDK (Netty)

[![Maven Central](https://img.shields.io/maven-central/v/com.github.kpavlov.jreactive8583/netty-iso8583.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.kpavlov.jreactive8583%22%20AND%20a:%22netty-iso8583%22)
[![Travis](https://img.shields.io/travis/kpavlov/jreactive-8583/master.svg?maxAge=2592000)](https://travis-ci.org/kpavlov/jreactive-8583)
![Libraries.io for GitHub](https://img.shields.io/librariesio/github/kpavlov/jreactive-8583.svg?maxAge=2592000)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c8d9680f5cae470ea7cbb152e1134ef5)](https://www.codacy.com/app/kpavlov/jreactive-8583?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kpavlov/jreactive-8583&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/56e9f30962b0cbd97ed7/maintainability)](https://codeclimate.com/github/kpavlov/jreactive-8583/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/56e9f30962b0cbd97ed7/test_coverage)](https://codeclimate.com/github/kpavlov/jreactive-8583/test_coverage)

## Motivation

1. [jPOS][jpos] library is not free for commercial use.
2. [j8583][j8583] is free but does not offer network client

Solution: **"J-Reactive-8583"** ISO8583 Client and Server built on top of excellent [Netty][netty] asynchronous messaging framework with the help of [j8583][j8583] for encoding/decoding. It is distributed under Apache License 2.0.

## Supported Features

* Client and Server endpoints.
* Java 11+
* Support ISO8583 messages using [j8583][j8583] library.
* Customizable [ISO MessageFactory][j8583-message-factory].
* Automatic responding to Echo messages.
* Automatic client reconnection.
* Secure [message logger](https://github.com/kpavlov/jreactive-8583/blob/master/src/main/java/com/github/kpavlov/jreactive8583/netty/pipeline/IsoMessageLoggingHandler.java): mask PAN and track data or any other field (customizable). Optionally prints field descriptions.
 * Configurable netty [Bootstrap](https://github.com/netty/netty/blob/master/transport/src/main/java/io/netty/bootstrap/Bootstrap.java) and [ChannelPipeline](https://github.com/netty/netty/blob/master/transport/src/main/java/io/netty/channel/ChannelPipeline.java)

## ISO8583 TCP/IP Transport

For data transmission TCP/IP uses sessions.
Each session is a bi-directional data stream.
The protocol uses a single TCP/IP session to transfer data between hosts in both directions.

The continuous TCP/IP data stream is split into frames.
Each [ISO8583][iso8583] message is sent in a separate frame.

A Frame consists of an N-byte length header and message body.
Usually, N==2.
The header contains the length of the following message.
The high byte of value is transmitted first, and the low byte of value is transmitted second.

| N bytes            | M bytes            |
| ------------------ | ------------------ |
| Message Length = M | ISO–8583 Message   |

# Getting Started

Add dependency to your project:

    <dependencies>
        <dependency>
            <groupId>com.github.kpavlov.jreactive8583</groupId>
            <artifactId>netty-iso8583</artifactId>
            <version>${VERSION}</version>
        </dependency>
    </dependencies>

Now you may use ISO8583 client or server in your code.

## Creating and Using ISO-8583 Client

The minimal client workflow includes:

~~~java
var messageFactory = new J8583MessageFactory<>();// [1]
Iso8583Client<IsoMessage> client = new Iso8583Client<>(messageFactory);// [2]

client.addMessageListener(new IsoMessageListener<IsoMessage>() { // [3]
    ...
});
client.getConfiguration().replyOnError(true);// [4]
client.init();// [5]

client.connect(host, port);// [6]
if (client.isConnected()) { // [7]

    IsoMessage message = messageFactory.newMessage(...);
    ...
    client.sendAsync(message);// [8]
    // or
    client.send(message);// [9]
    // or
    client.send(message, 1, TimeUnit.SECONDS);// [10]
}

...
client.shutdown();// [11]
~~~

1. First you need to create a `MessageFactory`
2. Then you create a [`Iso8583Client`][Iso8583Client] providing `MessageFactory` and, optionally, `SocketAddress`
3. Add one or more custom [`IsoMessageListener`][IsoMessageListener]s to handle `IsoMessage`s.
4. Configure the client. You may omit this step if you're fine with default configuration.
5. Initialize a client. Now it is ready to connect.
6. Establish a connection. By default, if connection will is lost, it reconnects automatically. You may disable this behaviour or change _reconnectInterval_.
7. Verify that connection is established
8. Send `IsoMessage` asynchronously
9. Send `IsoMessage` synchronously
10. Send `IsoMessage` synchronously with timeout support
11. Disconnect when you're done.

## Creating and Using ISO-8583 Server

Typical server workflow includes:

~~~java

var messageFactory = new J8583MessageFactory<>(ConfigParser.createDefault(), ISO8583Version.V1987);// [1]
Iso8583Server<IsoMessage> server = new Iso8583Server<>(port, messageFactory);// [2]

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
~~~

1. First you need to create a `MessageFactory`
2. Then you create a [`Iso8583Server`][Iso8583Server] providing `MessageFactory` and port to bind to
3. Add one or more custom [`IsoMessageListener`][IsoMessageListener]s to handle `IsoMessage`s.
4. Configure the server. You may omit this step if you're fine with default configuration.
5. Initialize a server. Now it is ready to start.
6. Start server. Now it is ready to accept client connections.
7. Verify that the server is started
9. Shutdown server when you're done.

## Logging

Default [`IsoMessageLoggingHandler`][IsoMessageLoggingHandler] may produce output like:

    312 [nioEventLoopGroup-5-1] DEBUG IsoMessageLoggingHandler - [id: 0xa72cc005, /127.0.0.1:50853 => /127.0.0.1:9876] MTI: 0x0200
      2: [Primary account number (PAN):NUMERIC(19)] = '000400*********0002'
      3: [Processing code:NUMERIC(6)] = '650000'
      7: [Transmission date & time:DATE10(10)] = '0720233443'
      11: [System trace audit number:NUMERIC(6)] = '483248'
      32: [Acquiring institution identification code:LLVAR(3)] = '456'
      35: [Track 2 data:LLVAR(17)] = '***'
      43: [Card acceptor name/location (1-23 address 24-36 city 37-38 state 39-40 country):ALPHA(40)] = 'SOLABTEST TEST-3 DF MX                  '
      49: [Currency code, transaction:ALPHA(3)] = '484'
      60: [Reserved national:LLLVAR(3)] = 'foo'
      61: [Reserved private:LLLVAR(5)] = '1234P'
      100: [Receiving institution identification code:LLVAR(3)] = '999'
      102: [Account identification 1:LLVAR(4)] = 'ABCD'

Using client or server configuration

You may:

- enable message logging. As of 0.2.2 logging is disabled by default (#50).
- enable and disable printing of sensitive information, like PAN
- customize which fields should be masked in logs
- enable and disable printing field descriptions
- customize tcp frame length field length
- customize [ISO 8583 version](https://en.wikipedia.org/wiki/ISO_8583#ISO_8583_version)

See
[ConnectorConfiguration](./src/main/java/com/github/kpavlov/jreactive8583/ConnectorConfiguration.java),
[ServerConfiguration](./src/main/java/com/github/kpavlov/jreactive8583/server/ServerConfiguration.java) and
[ClientConfiguration](./src/main/java/com/github/kpavlov/jreactive8583/client/ClientConfiguration.java).

---
For frequently asked questions check the [FAQ](https://github.com/kpavlov/jreactive-8583/wiki/FAQ) page.

## ISO 8583 Links

- Beginner's guide: http://www.lytsing.org/downloads/iso8583.pdf
- Introduction to ISO8583: http://www.codeproject.com/Articles/100084/Introduction-to-ISO-8583
- NPM package for Packing and unpacking ISO 8583 messages: https://www.npmjs.com/package/iso-8583
- [ISO 8583 wiki page](https://en.wikipedia.org/wiki/ISO_8583)

[iso8583]: https://en.wikipedia.org/wiki/ISO_8583
[iso-examples]: https://github.com/beckerdo/ISO-8583-Examples "Some payments processing examples"
[j8583-example]: https://krishnarag.wordpress.com/2014/06/18/iso-8583-j8583-java-library/
[j8583]: https://bitbucket.org/chochos/j8583 "Java implementation of the ISO8583 protocol."
[j8583-message-factory]: https://github.com/chochos/j8583/blob/master/src/main/java/com/solab/iso8583/IsoMessage.java
[jpos]: http://jpos.org
[netty]: https://netty.io

[Iso8583Client]: https://github.com/kpavlov/jreactive-8583/blob/master/src/main/java/com/github/kpavlov/jreactive8583/client/Iso8583Client.java
[Iso8583Server]: https://github.com/kpavlov/jreactive-8583/blob/master/src/main/java/com/github/kpavlov/jreactive8583/server/Iso8583Server.java
[IsoMessageListener]: https://github.com/kpavlov/jreactive-8583/blob/master/src/main/java/com/github/kpavlov/jreactive8583/IsoMessageListener.java
[IsoMessageLoggingHandler]: https://github.com/kpavlov/jreactive-8583/blob/master/src/main/java/com/github/kpavlov/jreactive8583/netty/pipeline/IsoMessageLoggingHandler.java
