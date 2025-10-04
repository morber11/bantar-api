package com.bantar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BantarApplication {

	public static void main(String[] args) {
		SpringApplication.run(BantarApplication.class, args);
	}
}
