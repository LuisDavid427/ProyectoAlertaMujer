package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RegistroApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistroApplication.class, args);
	}

}
