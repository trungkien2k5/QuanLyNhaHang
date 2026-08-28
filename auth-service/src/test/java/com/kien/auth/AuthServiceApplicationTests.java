package com.kien.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"JWT_SECRET=test-secret-key-for-unit-test-123456789"
})
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}
}