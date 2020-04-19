package com.github.kpavlov.jreactive8583.example.server;

import com.github.kpavlov.jreactive8583.server.Iso8583Server;
import com.github.kpavlov.jreactive8583.server.ServerConfiguration;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
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
        final ServerConfiguration configuration = ServerConfiguration.newBuilder()
//                .addLoggingHandler()
//                .logSensitiveData(false)
                .workerThreadsCount(1)
                .build();

        return new Iso8583Server<>(port, configuration, serverMessageFactory());
    }

    private MessageFactory<IsoMessage> serverMessageFactory() throws IOException {
        final MessageFactory<IsoMessage> messageFactory = ConfigParser.createDefault();
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(false);
        messageFactory.setAssignDate(true);
        return messageFactory;
    }


}
