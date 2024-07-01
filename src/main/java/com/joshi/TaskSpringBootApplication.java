package com.joshi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskSpringBootApplication.class, args);
	}
	
}
