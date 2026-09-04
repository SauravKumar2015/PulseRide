package com.pulseride.ride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class RideServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RideServiceApplication.class, args);
        log.info("------------------------------------------");
        log.info("RIDE-SERVICE START ON PORT : 8084");
        log.info("------------------------------------------");
    }
}
