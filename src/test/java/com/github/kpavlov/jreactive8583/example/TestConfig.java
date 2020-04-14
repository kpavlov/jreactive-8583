package com.github.kpavlov.jreactive8583.example;

import com.github.kpavlov.jreactive8583.example.client.Iso8583ClientConfig;
import com.github.kpavlov.jreactive8583.example.server.Iso8583ServerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({
        Iso8583ClientConfig.class,
        Iso8583ServerConfig.class
})
@PropertySource("/application-test.properties")
public class TestConfig {

    static {
        System.setProperty("nfs.rpc.tcp.nodelay", "true");
    }
}
