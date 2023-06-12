package com.jimmy.swiftwheels;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.jimmy.swiftwheels.user")
public class SwiftwheelsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwiftwheelsApplication.class, args);
	}
}
