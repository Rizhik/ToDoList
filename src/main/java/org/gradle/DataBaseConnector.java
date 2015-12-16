package org.gradle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {
	public DatabaseConnector() throws Exception {
		connection = connectToDB();
	}
	
	public void close() throws Exception 
	{
		connection.close();
	}
	
	private Connection connection;
	
	public void addRecord(Task task) throws SQLException {
		Statement statement= connection.createStatement();
		String insertQuery = "INSERT INTO tasks_tbl (id, task, user_id, status) VALUES ('"
				+ task.id
				+ "', '"
				+ task.taskDescription
				+ "','"
				+ task.userID
				+ "',' " + task.status + "')";
		statement.execute(insertQuery);

	}

	public void deleteRecord(Statement statement, String id)
			throws SQLException {
		String deleteQuery = "DELETE FROM tasks_tbl WHERE id = '" + id + "'";

		statement.execute(deleteQuery);
	}

	public String getTaskDescription(Statement statement, String id)
			throws SQLException {
		String selectQuery = "SELECT task FROM tasks_tbl WHERE id ='" + id
				+ "'";

		statement.execute(selectQuery);
		ResultSet result = statement.getResultSet();
		result.next();

		return result.getString(1);
	}

	public int getUserID(Statement statement, String id) throws SQLException {
		String selectQuery = "SELECT user_id FROM tasks_tbl WHERE id ='" + id
				+ "'";

		statement.execute(selectQuery);
		ResultSet result = statement.getResultSet();
		result.next();

		return result.getInt(1);
	}

	public String getStatus(Statement statement, String id) throws SQLException {
		String selectQuery = "SELECT status FROM tasks_tbl WHERE id = '" + id
				+ "'";

		statement.execute(selectQuery);
		ResultSet result = statement.getResultSet();
		result.next();

		return result.getString(1);
	}

	public void setStatus(Statement statement, String id, String status)
			throws SQLException {
		String updateQuery = "UPDATE tasks_tbl SET status = '" + status
				+ "' WHERE id='" + id + "'";
		statement.execute(updateQuery);
	}

	public void setTaskDescription(Statement statement, String id,
			String newTaskDescription) throws SQLException {
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
