package org.gradle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory
			.getLogger(Application.class);

	public static void main(String args[]) throws Exception {
		SpringApplication.run(Application.class, args);
		String url = "jdbc:mysql://localhost:3306/javabase";
		String username = "java";
		String password = "password";

		try (Connection connection = DriverManager.getConnection(url, username,
				password)) {
			Statement statement = connection.createStatement();
			log.info("Creating table tasks_tbl");
			statement
					.execute("CREATE TABLE IF NOT EXISTS tasks_tbl(id VARCHAR(8) NOT NULL PRIMARY KEY, task VARCHAR(255) NOT NULL, user_id INT NOT NULL, status VARCHAR(255) NOT NULL CHECK (status IN ('Active', 'Completed')))");

			log.info("Creating table users_tbl");
			statement
					.execute("CREATE TABLE IF NOT EXISTS users_tbl(id INT AUTO_INCREMENT PRIMARY KEY, login VARCHAR(255) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL)");
			connection.close();

		} catch (SQLException e) {

			throw new IllegalStateException("Cannot connect the database!", e);

		}
	}

}