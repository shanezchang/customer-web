package com.shane.customer_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CustomerWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerWebApplication.class, args);
	}

}
