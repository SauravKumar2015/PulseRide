package com.pulseride.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class UserAuthApplication {

    public static void main(String[] args) {

        SpringApplication.run(UserAuthApplication.class, args);

        log.info("------------------------------------------------------------------");
        log.info("🚀 PulseRide Auth Service started successfully on port 8081");
        log.info("------------------------------------------------------------------");
    }
}