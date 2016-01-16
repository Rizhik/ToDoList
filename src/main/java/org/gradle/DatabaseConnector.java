package org.gradle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnector {

	private static final Logger log = LoggerFactory
			.getLogger(Application.class);

	private Connection connection;

	public DatabaseConnector(String url, String username, String password) {
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			log.debug(e.toString());
		}
	}

	public void close() throws Exception {
		connection.close();
	}

	public Connection getConnection() {
		return connection;
	}

	public void initializeDatabase() throws Exception {

		Statement statement = connection.createStatement();
		log.info("Creating table tasks_tbl");
		statement
				.execute("CREATE TABLE IF NOT EXISTS tasks_tbl(id VARCHAR(8) NOT NULL PRIMARY KEY, task VARCHAR(255) NOT NULL, user_id INT NOT NULL, status VARCHAR(255) NOT NULL CHECK (status IN ('Active', 'Completed')))");

		log.info("Creating table users_tbl");
		statement
				.execute("CREATE TABLE IF NOT EXISTS users_tbl(id INT AUTO_INCREMENT PRIMARY KEY, login VARCHAR(255) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL)");
		close();
	}

	public void addTask(Task task) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("INSERT INTO tasks_tbl (id, task, user_id, status) VALUES (?, ?, ?, ?)");
		statement.setString(1, task.id);
		statement.setString(2, task.task);
		statement.setInt(3, task.userID);
		statement.setString(4, task.status);
		statement.executeUpdate();
	}

	public void deleteTask(String id) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("DELETE FROM tasks_tbl WHERE id = ?");
		statement.setString(1, id);
		statement.executeUpdate();
	}

	public String getTaskDescription(String id) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("SELECT task FROM tasks_tbl WHERE id =?");
		statement.setString(1, id);

		statement.executeQuery();
		ResultSet result = statement.getResultSet();
		result.next();

		return result.getString(1);
	}

	public int getUserID(String id) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("SELECT user_id FROM tasks_tbl WHERE id =?");
		statement.setString(1, id);
		statement.executeQuery();

		ResultSet result = statement.getResultSet();
		result.next();

		return result.getInt(1);
	}

	public String getStatus(String id) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("SELECT status FROM tasks_tbl WHERE id = ?");
		statement.setString(1, id);
		statement.executeQuery();

		ResultSet result = statement.getResultSet();
		result.next();

		return result.getString(1);
	}

	public void setStatus(String id, String status) throws SQLException {
		if (status.equals("Active") || status.equals("Completed")) {
			PreparedStatement statement = connection
					.prepareStatement("UPDATE tasks_tbl SET status = ? WHERE id = ?");
			statement.setString(1, status);
			statement.setString(2, id);

			statement.executeUpdate();
		} else {
			log.info("STATUS WAS NOT UPDATED. Status could be 'Active' or 'Completed'.");
		}
	}

	public void setTaskDescription(String id, String newTaskDescription)
			throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("UPDATE tasks_tbl SET task = ? WHERE id=?");
		statement.setString(1, newTaskDescription);
		statement.setString(2, id);
		statement.executeUpdate();
	}

	public ArrayList<Task> getAllTasks(int userID) throws SQLException {

		PreparedStatement statement = connection
				.prepareStatement("SELECT * FROM tasks_tbl WHERE user_id=?");
		statement.setInt(1, userID);
		statement.executeQuery();
		ResultSet result = statement.getResultSet();

		ArrayList<Task> tasksList = new ArrayList<Task>();

		while (result.next()) {
			String id = result.getString(1);
			String task = result.getString(2);
			String status = result.getString(4);
			Task currentTask = new Task(id, task, userID, status);
			tasksList.add(currentTask);
		}

		return tasksList;
	}

	public User getUser(String login) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("SELECT * FROM users_tbl WHERE login=?");
		statement.setString(1, login);
		statement.executeQuery();
		ResultSet result = statement.getResultSet();

		if (result.first()) {

			return new User(result.getInt(1), result.getString(2),
					result.getString(3));
		}
		return null;
	}

	public boolean verifyPassword(String password, User user)
			throws SQLException {

		if (user.password.equals(password)) {
			log.info("USER VERIFIED LOGIN: " + user.login + " PASSWORD: "
					+ user.password);
			return true;
		} else {
			log.info("CREDENTIALS ARE WRONG");
			return false;
		}
	}

	public boolean createNewUser(String login, String password) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("INSERT INTO users_tbl  (id, login, password) VALUES (0, ?, ?)");
			statement.setString(1, login);
			statement.setString(2, password);
			statement.executeUpdate();

			log.info("USER CREATED");

			return true;

		} catch (SQLException e) {

			log.info("USER CAN NOT BE CREATED");

			return false;
		}

	}

	public boolean deleteUser(String login) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("DELETE FROM users_tbl WHERE login=?");
			statement.setString(1, login);
			statement.executeUpdate();

			log.info("USER " + login + " DELETED");

			return true;

		} catch (SQLException e) {

			log.info("USER CAN NOT BE DELETED");

			return false;
		}

	}
}
