package com.vneuron.vneuroncheeseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class VneuronCheeseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VneuronCheeseServiceApplication.class, args);
    }

}
