package com.fizalise.accountapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccountapiApplication {
	public static void main(String[] args) {
		SpringApplication.run(AccountapiApplication.class, args);
	}
}
