package org.jreactive.iso8583.example.client;

import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;
import org.jreactive.iso8583.Netty8583Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

@Configuration
public class Iso8583ClientConfig {

    @Value("${iso8583.connection.host}")
    String host;

    @Value("${iso8583.connection.port}")
    int port;

    @Value("${iso8583.connection.idleTimeout}")
    int idleTimeout;

    @Bean
    public Netty8583Client iso8583Client() throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        final Netty8583Client client = new Netty8583Client(socketAddress, clientMessageFactory());
        client.setIdleTimeout(idleTimeout);
        return client;
    }

    private MessageFactory clientMessageFactory() throws IOException {
        final MessageFactory messageFactory =  ConfigParser.createFromClasspathConfig("iso8583-config.xml");
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(true);
        messageFactory.setAssignDate(true);
        messageFactory.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System
                .currentTimeMillis() % 1000000)));
        return messageFactory;
    }
}
