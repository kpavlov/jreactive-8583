package com.github.kpavlov.jreactive8583.example.client;

import com.github.kpavlov.jreactive8583.client.ClientConfiguration;
import com.github.kpavlov.jreactive8583.client.Iso8583Client;
import com.github.kpavlov.jreactive8583.iso.ISO8583Version;
import com.github.kpavlov.jreactive8583.iso.J8583MessageFactory;
import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class Iso8583ClientConfig {

    @Value("${iso8583.connection.host}")
    private String host;

    @Value("${iso8583.connection.port}")
    private int port;

    @Value("${iso8583.connection.idleTimeout}")
    private int idleTimeout;

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public Iso8583Client<IsoMessage> iso8583Client() throws IOException {
        final SocketAddress socketAddress = new InetSocketAddress(host, port);

        final var configuration = ClientConfiguration.newBuilder()
//                .addLoggingHandler()
                .idleTimeout(idleTimeout)
                .logSensitiveData(false)
                .workerThreadsCount(2)
                .build();

        return new Iso8583Client<>(socketAddress, configuration, clientMessageFactory());
    }

    private MessageFactory<IsoMessage> clientMessageFactory() throws IOException {
        final var messageFactory = ConfigParser.createDefault();
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(false);
        messageFactory.setAssignDate(true);
        messageFactory.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System
                .currentTimeMillis() % 1000000)));
        return new J8583MessageFactory<>(messageFactory, ISO8583Version.V1987);
    }
}
