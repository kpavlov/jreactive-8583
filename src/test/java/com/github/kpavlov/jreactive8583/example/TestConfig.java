package com.github.kpavlov.jreactive8583.example;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:/test-config.xml")
public class TestConfig {

    static {
        System.setProperty("nfs.rpc.tcp.nodelay", "true");
    }
}
