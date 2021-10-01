package com.vneuron.creamery.eureka.vneuroncreameryeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class VneuronCreameryEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(VneuronCreameryEurekaApplication.class, args);
	}

}
