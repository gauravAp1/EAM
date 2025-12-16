package com.example.eam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EamApplication {

	public static void main(String[] args) {
		SpringApplication.run(EamApplication.class, args);
	}

}
