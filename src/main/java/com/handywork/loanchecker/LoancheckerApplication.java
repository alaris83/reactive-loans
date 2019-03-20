package com.handywork.loanchecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LoancheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoancheckerApplication.class, args);
	}

}
