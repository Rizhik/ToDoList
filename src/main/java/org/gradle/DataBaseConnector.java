package org.gradle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnector {

	private Connection connection;

	private static final Logger log = LoggerFactory
			.getLogger(Application.class);

	public DatabaseConnector() throws Exception {
		connection = connectToDB();
	}

	public void close() throws Exception {
		connection.close();
	}

	public void addRecord(Task task) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("INSERT INTO tasks_tbl (id, task, user_id, status) VALUES (?, ?, ?, ?)");
		statement.setString(1, task.id);
		statement.setString(2, task.task);
		statement.setInt(3, task.userID);
		statement.setString(4, task.status);
		statement.executeUpdate();

	}

	public void deleteRecord(String id) throws SQLException {
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
		PreparedStatement statement = connection
				.prepareStatement("UPDATE tasks_tbl SET status = ? WHERE id = ?");
		statement.setString(1, status);
		statement.setString(2, id);

		statement.executeUpdate();
	}

	public void setTaskDescription(String id, String newTaskDescription)
			throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("UPDATE tasks_tbl SET task = ? WHERE id=?");
		statement.setString(1, newTaskDescription);
		statement.setString(2, id);
		statement.executeUpdate();
	}

	private static Connection connectToDB() throws Exception {

		String url = "jdbc:mysql://localhost:3306/javabase";
		String username = "java";
		String password = "password";

		return DriverManager.getConnection(url, username, password);
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

	public ResultSet getUser(String login) throws SQLException {
		PreparedStatement statement = connection
				.prepareStatement("SELECT * FROM users_tbl WHERE login=?");
		statement.setString(1, login);
		statement.executeQuery();
		return statement.getResultSet();
	}

	public boolean verifyPassword(String password, ResultSet result)
			throws SQLException {
		if (result.first() && result.getString(3).equals(password)) {
			log.info("USER VERIFIED LOGIN: " + result.getString(2)
					+ " PASSWORD: " + password);
			return true;
		} else {
			log.info("CREDENTIALS ARE WRONG");
			return false;
		}
	}

	public int getUserID(ResultSet result) throws SQLException {
		result.first();
		return result.getInt(1);
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
}
