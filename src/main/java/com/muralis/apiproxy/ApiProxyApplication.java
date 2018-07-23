package com.muralis.apiproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySource("file:./config/config.properties")
public class ApiProxyApplication {

	public static void main(String[] args) {		
		SpringApplication.run(ApiProxyApplication.class, args);
	}
}
