package org.gradle;

public class TestDbConnectionProvider implements DbConnectionProvider {
	public DatabaseConnector create()
	{
		String url = "jdbc:mysql://localhost:3306/testbase";
		String username = "tester";
		String password = "password";
		return new DatabaseConnector(url, username, password) ;
	}
}
