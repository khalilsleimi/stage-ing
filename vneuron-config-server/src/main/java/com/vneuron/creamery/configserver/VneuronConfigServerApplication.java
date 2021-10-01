package com.vneuron.creamery.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class VneuronConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VneuronConfigServerApplication.class, args);
	}

}
