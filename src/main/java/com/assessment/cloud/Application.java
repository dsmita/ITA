package com.assessment.cloud;

import java.net.URISyntaxException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.assessment" })
@PropertySource("classpath:application.properties")
public class Application {

	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(Application.class, args);
	}

}
