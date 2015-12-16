package org.gradle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {

	private Connection connection;

	public DatabaseConnector() throws Exception {
		connection = connectToDB();
	}

	public void close() throws Exception {
		connection.close();
	}

	public void addRecord(Task task) throws SQLException {
		Statement statement = connection.createStatement();
		String insertQuery = "INSERT INTO tasks_tbl (id, task, user_id, status) VALUES ('"
				+ task.id
				+ "', '"
				+ task.taskDescription
				+ "','"
				+ task.userID
				+ "',' " + task.status + "')";
		statement.execute(insertQuery);

	}

	public void deleteRecord(String id) throws SQLException {
		Statement statement = connection.createStatement();
		String deleteQuery = "DELETE FROM tasks_tbl WHERE id = '" + id + "'";

		statement.execute(deleteQuery);
	}

	public String getTaskDescription(String id) throws SQLException {
		Statement statement = connection.createStatement();
		String selectQuery = "SELECT task FROM tasks_tbl WHERE id ='" + id
				+ "'";

		statement.execute(selectQuery);
		ResultSet result = statement.getResultSet();
		result.next();

		return result.getString(1);
	}

	public int getUserID(String id) throws SQLException {
		Statement statement = connection.createStatement();
		String selectQuery = "SELECT user_id FROM tasks_tbl WHERE id ='" + id
				+ "'";

		statement.execute(selectQuery);
		ResultSet result = statement.getResultSet();
		result.next();

		return result.getInt(1);
	}

	public String getStatus(String id) throws SQLException {
		Statement statement = connection.createStatement();
		String selectQuery = "SELECT status FROM tasks_tbl WHERE id = '" + id
				+ "'";

		statement.execute(selectQuery);
		ResultSet result = statement.getResultSet();
		result.next();

		return result.getString(1);
	}

	public void setStatus(String id, String status) throws SQLException {
		Statement statement = connection.createStatement();
		String updateQuery = "UPDATE tasks_tbl SET status = '" + status
				+ "' WHERE id='" + id + "'";
		statement.execute(updateQuery);
	}

	public void setTaskDescription(String id, String newTaskDescription)
			throws SQLException {
		Statement statement = connection.createStatement();
		String updateQuery = "UPDATE tasks_tbl SET task = '"
				+ newTaskDescription + "' WHERE id='" + id + "'";
		statement.execute(updateQuery);
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
