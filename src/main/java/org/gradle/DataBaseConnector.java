package org.gradle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {

	private Connection connection;

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
		statement.setString(2, task.taskDescription);
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

		Connection connection = DriverManager.getConnection(url, username,
				password);
		return connection;
	}

}
