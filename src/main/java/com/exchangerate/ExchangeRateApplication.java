package com.exchangerate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@EnableScheduling
@SpringBootApplication
public class ExchangeRateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExchangeRateApplication.class, args);
    }
}
