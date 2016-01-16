package org.gradle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfiguration {
	@Bean
	@Primary
	DbConnectionProvider TestDb() {
		return new TestDbConnectionProvider();
	}
}
