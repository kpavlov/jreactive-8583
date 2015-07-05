package org.jreactive.iso8583.example.server;

import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;
import org.jreactive.iso8583.Netty8583Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class Iso8583ServerConfig {

    @Value("${iso8583.connection.port}")
    int port;

    @Bean
    public Netty8583Server iso8583Server() throws IOException {
        return new Netty8583Server(port, null, serverMessageFactory());
    }

    private MessageFactory serverMessageFactory() throws IOException {
        final MessageFactory messageFactory = ConfigParser.createFromClasspathConfig("iso8583-config.xml");
        messageFactory.setCharacterEncoding(StandardCharsets.US_ASCII.name());
        messageFactory.setUseBinaryMessages(true);
        messageFactory.setAssignDate(true);
        return messageFactory;
    }
}
