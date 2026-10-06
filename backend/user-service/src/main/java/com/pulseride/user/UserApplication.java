package com.pulseride.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApplication {

    private static final Logger log =
            LoggerFactory.getLogger(UserApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);

        log.info("------------------------------------------");
        log.info("USER-SERVICE STARTED ON PORT : 8086");
        log.info("------------------------------------------");
    }
}