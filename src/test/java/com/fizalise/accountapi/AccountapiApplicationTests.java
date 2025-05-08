package com.fizalise.accountapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AccountapiApplicationTests {

	@Test
	void contextLoads() {
	}

}
