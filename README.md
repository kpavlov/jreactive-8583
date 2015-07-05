# JReactive-8583

ISO8583 Java Connector

[![Build Status](https://travis-ci.org/kpavlov/jreactive-8583.png?branch=master)](https://travis-ci.org/kpavlov/jreactive-8583)

## Motivation

1. [jPOS][jpos] library is not free for commercial use. 
2. [j8583][j8583] is free but does not offer network client

Solution: **"J-Reactive-8583"** ISO8583 Client and Server built on top of excellent Netty asynchronous messaging framework with the help of j8583.

For data transmission TCP/IP uses sessions.
Each session is a bi-directional data stream. 
The protocol uses a single TCP/IP session to transfer data between hosts in both directions. 

The continuous TCP/IP data stream is split into frames. 
Each [ISO8583][iso8583] message is sent in a separate frame. 

A Frame consists of a 2-byte length header and a message body. 
The header contains the length of the following message.
The high byte of value is transmitted first, and the low byte of value is transmitted second.

|| 2 bytes          || N bytes
|--------------------|-------------------
| Message Length = N | ISO – 8583 Message

## Supported Features

* Client and Server endpoints.
* Support ISO8583 messages using [j8583][j8583] library.
* Automatic responding to Echo messages.
* Customizable ISO MessageFactory

## ISO 8583 Links 

- Beginner's guide: http://www.lytsing.org/downloads/iso8583.pdf.
- Introduction to ISO8583: http://www.codeproject.com/Articles/100084/Introduction-to-ISO-8583.

[iso8583]: https://en.wikipedia.org/wiki/ISO_8583
[iso-examples]: https://github.com/beckerdo/ISO-8583-Examples "Some payments processing examples"
[j8583-example]: https://krishnarag.wordpress.com/2014/06/18/iso-8583-j8583-java-library/
[j8583]: https://github.com/chochos/j8583 "Java implementation of the ISO8583 protocol."
[jpos]: http://jpos.org 