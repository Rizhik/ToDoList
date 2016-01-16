package org.gradle;

import org.springframework.stereotype.Component;

@Component
public class RealDbConnectionProvider implements DbConnectionProvider {
	public DatabaseConnector create() {
		String url = "jdbc:mysql://localhost:3306/javabase";
		String username = "java";
		String password = "password";
		return new DatabaseConnector(url, username, password);
	}
}