package com.fizalise.accountapi;

import org.springframework.boot.SpringApplication;

public class TestAccountapiApplication {

	public static void main(String[] args) {
		SpringApplication.from(AccountapiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
