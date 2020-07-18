package com.github.kpavlov.jreactive8583.example.server;

import com.github.kpavlov.jreactive8583.iso.ISO8583Version;
import com.github.kpavlov.jreactive8583.iso.J8583MessageFactory;
import com.github.kpavlov.jreactive8583.iso.MessageFactory;
import com.github.kpavlov.jreactive8583.server.Iso8583Server;
import com.github.kpavlov.jreactive8583.server.ServerConfiguration;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.parse.ConfigParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class Iso8583ServerConfig {

    @Value("${iso8583.connection.port}")
    private int port;

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public Iso8583Server<IsoMessage> iso8583Server() throws IOException {
        final var configuration = ServerConfiguration.newBuilder()
//                .addLoggingHandler()
//                .logSensitiveData(false)
                .workerThreadsCount(4)
                .build();

        return new Iso8583Server<>(port, configuration, serverMessageFactory());
    }

    private MessageFactory<IsoMessage> serverMessageFactory() throws IOException {
        final var messageFactory = ConfigParser.createDefault();
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(false);
        messageFactory.setAssignDate(true);
        return new J8583MessageFactory<>(messageFactory, ISO8583Version.V1987);
    }


}
