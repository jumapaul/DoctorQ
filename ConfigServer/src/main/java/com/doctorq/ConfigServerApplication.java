package com.doctorq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    //Config application
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}
