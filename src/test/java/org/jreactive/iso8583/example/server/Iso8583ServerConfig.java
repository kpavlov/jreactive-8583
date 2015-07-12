package org.jreactive.iso8583.example.server;

import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import org.jreactive.iso8583.server.Iso8583Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class Iso8583ServerConfig {

    @Value("${iso8583.connection.port}")
    int port;

    @Bean
    public Iso8583Server iso8583Server() throws IOException {
        return new Iso8583Server(port, serverMessageFactory());
    }

    @Bean
    MessageFactory serverMessageFactory() throws IOException {
        final MessageFactory messageFactory = ConfigParser.createDefault();
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(false);
        messageFactory.setAssignDate(true);
        return messageFactory;
    }
}
