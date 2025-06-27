package com.bacthinh.BacThinh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BacThinhApplication {

	public static void main(String[] args) {
		SpringApplication.run(BacThinhApplication.class, args);
	}

}
