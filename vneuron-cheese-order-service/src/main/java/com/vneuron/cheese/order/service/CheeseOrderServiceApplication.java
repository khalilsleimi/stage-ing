package com.vneuron.cheese.order.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Fails if persistence isn't configured correctly
@SpringBootApplication
public class CheeseOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheeseOrderServiceApplication.class, args);
    }
}
