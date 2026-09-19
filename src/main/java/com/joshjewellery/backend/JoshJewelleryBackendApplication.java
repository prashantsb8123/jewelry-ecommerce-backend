package com.joshjewellery.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JoshJewelleryBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoshJewelleryBackendApplication.class, args);
    }
}
