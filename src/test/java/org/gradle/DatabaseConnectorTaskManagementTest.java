package org.gradle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DatabaseConnectorTaskManagementTest {

	private Task task = new Task("-123", "1 task under test", -25, "Completed");
	private DatabaseConnector dbconnector;

	@BeforeGroups(groups = { "TaskManagement" })
	public void setup() throws Exception {
		dbconnector = new DatabaseConnector();
	}

	@BeforeMethod(groups = { "TaskManagement" })
	public void prepareTaskTestData() {
		try {
			PreparedStatement statement = dbconnector
					.getConnection()
					.prepareStatement(
							"INSERT INTO tasks_tbl (id, task, user_id, status) VALUES (?, ?, ?, ?)");
			statement.setString(1, task.id);
			statement.setString(2, task.task);
			statement.setInt(3, task.userID);
			statement.setString(4, task.status);
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("BEFORE METHOD FAILED: " + e);
		}
	}

	@AfterMethod(groups = { "TaskManagement" })
	public void cleanupTaskDatabase() throws SQLException {
		try {
			PreparedStatement statement = dbconnector.getConnection()
					.prepareStatement("DELETE FROM tasks_tbl WHERE user_id=?");
			statement.setInt(1, task.userID);
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("AFTER METHOD FAILED: " + e);
		}
	}

	@AfterGroups(groups = { "TaskManagement" })
	public void tearDown() throws Exception {
		dbconnector.close();
	}

	@Test(groups = { "TaskManagement" })
	public void addTask() throws Exception {
		cleanupTaskDatabase();

		dbconnector.addTask(task);

		PreparedStatement statement = dbconnector
				.getConnection()
				.prepareStatement(
						"SELECT * FROM tasks_tbl WHERE id=? AND task=? AND user_id=? AND status=?");
		statement.setString(1, task.id);
		statement.setString(2, task.task);
		statement.setInt(3, task.userID);
		statement.setString(4, task.status);
		statement.execute();

		ResultSet result = statement.getResultSet();

		AssertJUnit.assertTrue("TEST FAILED: There is NO such record in DB",
				result.first());
		AssertJUnit.assertFalse("TEST FAILED: Duplicated task record",
				result.next());
	}

	@Test(groups = { "TaskManagement" })
	public void deleteExistingTask() throws Exception {
		dbconnector.deleteTask(task.id);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, task.id);
		statement.execute();

		AssertJUnit.assertFalse("TEST FAILED: Task record was NOT deleted",
				statement.getResultSet().first());
	}

	@Test(groups = { "TaskManagement" })
	public void deleteNonExistingTask() throws Exception {
		dbconnector.deleteTask("-1000");

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, "-1000");
		statement.execute();

		AssertJUnit.assertFalse(statement.getResultSet().first());
	}

	@Test(groups = { "TaskManagement" })
	public void getAllTasks() throws Exception {

		ArrayList<Task> task_array = new ArrayList<Task>();

		task_array.add(task);

		task = new Task("-124", "2 task under test", -25, "Completed");
		task_array.add(task);
		prepareTaskTestData();

		task = new Task("-125", "3 task under test", -25, "Active");
		task_array.add(task);
		prepareTaskTestData();

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE user_id=?");
		statement.setInt(1, task.userID);
		statement.execute();

		ResultSet result = statement.getResultSet();

		ArrayList<Task> result_array = new ArrayList<Task>();

		while (result.next()) {
			Task current_task = new Task(result.getString(1),
					result.getString(2), result.getInt(3), result.getString(4));
			result_array.add(current_task);
		}
		AssertJUnit.assertEquals(
				"TEST FAILED: result set length are different",
				task_array.size(), result_array.size());
		AssertJUnit.assertFalse(
				"TEST FAILED: database return empty result set",
				result_array.isEmpty());
		for (int i = 0; i < task_array.size(); i++) {
			AssertJUnit.assertEquals("TEST FAILED: task ids are different",
					task_array.get(i).id, result_array.get(i).id);
			AssertJUnit.assertEquals(
					"TEST FAILED: task descriptions are different",
					task_array.get(i).task, result_array.get(i).task);
			AssertJUnit.assertEquals("TEST FAILED: user ids are different",
					task_array.get(i).userID, result_array.get(i).userID);
			AssertJUnit.assertEquals("TEST FAILED: statuses are different",
					task_array.get(i).status, result_array.get(i).status);
		}
	}
	@Test(groups = { "TaskManagement" })
	public void getAllTasks_taskNotExist() throws Exception {

		AssertJUnit.assertFalse("Test not implemented", true);
//		ArrayList<Task> task_array = new ArrayList<Task>();
//
//		task_array.add(task);
//
//		task = new Task("-124", "2 task under test", -25, "Completed");
//		task_array.add(task);
//		prepareTaskTestData();
//
//		task = new Task("-125", "3 task under test", -25, "Active");
//		task_array.add(task);
//		prepareTaskTestData();
//
//		PreparedStatement statement = dbconnector.getConnection()
//				.prepareStatement("SELECT * FROM tasks_tbl WHERE user_id=?");
//		statement.setInt(1, task.userID);
//		statement.execute();
//
//		ResultSet result = statement.getResultSet();
//
//		ArrayList<Task> result_array = new ArrayList<Task>();
//
//		while (result.next()) {
//			Task current_task = new Task(result.getString(1),
//					result.getString(2), result.getInt(3), result.getString(4));
//			result_array.add(current_task);
//		}
//		AssertJUnit.assertEquals(
//				"TEST FAILED: result set length are different",
//				task_array.size(), result_array.size());
//		AssertJUnit.assertFalse(
//				"TEST FAILED: database return empty result set",
//				result_array.isEmpty());
//		for (int i = 0; i < task_array.size(); i++) {
//			AssertJUnit.assertEquals("TEST FAILED: task ids are different",
//					task_array.get(i).id, result_array.get(i).id);
//			AssertJUnit.assertEquals(
//					"TEST FAILED: task descriptions are different",
//					task_array.get(i).task, result_array.get(i).task);
//			AssertJUnit.assertEquals("TEST FAILED: user ids are different",
//					task_array.get(i).userID, result_array.get(i).userID);
//			AssertJUnit.assertEquals("TEST FAILED: statuses are different",
//					task_array.get(i).status, result_array.get(i).status);
//		}
	}
	
	@Test(groups = { "TaskManagement" })
	public void getAllTasks_userHas1Task() throws Exception {

		AssertJUnit.assertFalse("Test not implemented", true);
		
//		ArrayList<Task> task_array = new ArrayList<Task>();
//
//		task_array.add(task);
//
//		task = new Task("-124", "2 task under test", -25, "Completed");
//		task_array.add(task);
//		prepareTaskTestData();
//
//		task = new Task("-125", "3 task under test", -25, "Active");
//		task_array.add(task);
//		prepareTaskTestData();
//
//		PreparedStatement statement = dbconnector.getConnection()
//				.prepareStatement("SELECT * FROM tasks_tbl WHERE user_id=?");
//		statement.setInt(1, task.userID);
//		statement.execute();
//
//		ResultSet result = statement.getResultSet();
//
//		ArrayList<Task> result_array = new ArrayList<Task>();
//
//		while (result.next()) {
//			Task current_task = new Task(result.getString(1),
//					result.getString(2), result.getInt(3), result.getString(4));
//			result_array.add(current_task);
//		}
//		AssertJUnit.assertEquals(
//				"TEST FAILED: result set length are different",
//				task_array.size(), result_array.size());
//		AssertJUnit.assertFalse(
//				"TEST FAILED: database return empty result set",
//				result_array.isEmpty());
//		for (int i = 0; i < task_array.size(); i++) {
//			AssertJUnit.assertEquals("TEST FAILED: task ids are different",
//					task_array.get(i).id, result_array.get(i).id);
//			AssertJUnit.assertEquals(
//					"TEST FAILED: task descriptions are different",
//					task_array.get(i).task, result_array.get(i).task);
//			AssertJUnit.assertEquals("TEST FAILED: user ids are different",
//					task_array.get(i).userID, result_array.get(i).userID);
//			AssertJUnit.assertEquals("TEST FAILED: statuses are different",
//					task_array.get(i).status, result_array.get(i).status);
//		}
	}
	
	@Test(groups = { "TaskManagement" })
	public void getAllTasks_userDoesNotHaveTasks() throws Exception {
		
		AssertJUnit.assertFalse("Test not implemented", true);

//		ArrayList<Task> task_array = new ArrayList<Task>();
//
//		task_array.add(task);
//
//		task = new Task("-124", "2 task under test", -25, "Completed");
//		task_array.add(task);
//		prepareTaskTestData();
//
//		task = new Task("-125", "3 task under test", -25, "Active");
//		task_array.add(task);
//		prepareTaskTestData();
//
//		PreparedStatement statement = dbconnector.getConnection()
//				.prepareStatement("SELECT * FROM tasks_tbl WHERE user_id=?");
//		statement.setInt(1, task.userID);
//		statement.execute();
//
//		ResultSet result = statement.getResultSet();
//
//		ArrayList<Task> result_array = new ArrayList<Task>();
//
//		while (result.next()) {
//			Task current_task = new Task(result.getString(1),
//					result.getString(2), result.getInt(3), result.getString(4));
//			result_array.add(current_task);
//		}
//		AssertJUnit.assertEquals(
//				"TEST FAILED: result set length are different",
//				task_array.size(), result_array.size());
//		AssertJUnit.assertFalse(
//				"TEST FAILED: database return empty result set",
//				result_array.isEmpty());
//		for (int i = 0; i < task_array.size(); i++) {
//			AssertJUnit.assertEquals("TEST FAILED: task ids are different",
//					task_array.get(i).id, result_array.get(i).id);
//			AssertJUnit.assertEquals(
//					"TEST FAILED: task descriptions are different",
//					task_array.get(i).task, result_array.get(i).task);
//			AssertJUnit.assertEquals("TEST FAILED: user ids are different",
//					task_array.get(i).userID, result_array.get(i).userID);
//			AssertJUnit.assertEquals("TEST FAILED: statuses are different",
//					task_array.get(i).status, result_array.get(i).status);
//		}
	}

	@Test(groups = { "TaskManagement" })
	public void getStatus_taskExists_Completed() throws SQLException {

		String result = dbconnector.getStatus(task.id);
		AssertJUnit.assertEquals(task.status, result);
	}

	@Test(groups = { "TaskManagement" })
	public void getStatus_taskExists_Active() throws SQLException {
		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement(
						"UPDATE tasks_tbl SET status = ? WHERE id = ?");
		statement.setString(2, task.id);
		statement.setString(1, "Active");
		statement.executeUpdate();

		String result = dbconnector.getStatus(task.id);
		AssertJUnit.assertEquals("Active", result);
	}

	@Test(groups = { "TaskManagement" }, expectedExceptions = SQLException.class)
	public void getStatus_taskNotExists() throws SQLException {
		dbconnector.getStatus("-1000");
	}

	@Test(groups = { "TaskManagement" })
	public void getTaskDescription() throws SQLException {

		String result = dbconnector.getTaskDescription(task.id);
		AssertJUnit.assertEquals(task.task, result);
	}

	@Test(groups = { "TaskManagement" }, expectedExceptions = SQLException.class)
	public void getTaskDescription_taskNotExists() throws SQLException {
		dbconnector.getTaskDescription("-1000");
	}

	@Test(groups = { "TaskManagement" })
	public void setStatus_Active() throws SQLException {

		String newStatus = "Active";
		dbconnector.setStatus(task.id, newStatus);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, task.id);
		statement.execute();

		ResultSet result = statement.getResultSet();
		result.first();
		String actual = result.getString(4);

		AssertJUnit.assertEquals(
				"TEST FAILED: Task status was not updated correctly",
				newStatus, actual);

		AssertJUnit
				.assertFalse(
						"TEST FAILED: Database containes several tasks with the same id",
						result.next());
	}

	@Test(groups = { "TaskManagement" })
	public void setStatus_Completed() throws SQLException {

		String newStatus2 = "Completed";
		dbconnector.setStatus(task.id, newStatus2);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, task.id);
		statement.execute();

		ResultSet result = statement.getResultSet();
		result.first();
		String actual = result.getString(4);

		AssertJUnit.assertEquals(
				"TEST FAILED: Task status was not updated correctly",
				newStatus2, actual);

		AssertJUnit
				.assertFalse(
						"TEST FAILED: Database containes several tasks with the same id",
						result.next());

	}

	@Test(groups = { "TaskManagement" })
	public void setStatusForTaskWithWrongID() throws SQLException {

		String newStatus = "Active";
		dbconnector.setStatus("-1000", newStatus);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, "-1000");
		statement.execute();

		ResultSet result = statement.getResultSet();

		AssertJUnit.assertFalse(result.next());

	}

	@Test(groups = { "TaskManagement" })
	public void setStatus_incorrectStatus() throws SQLException {

		String newStatus = "WRONG";
		dbconnector.setStatus(task.id, newStatus);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, task.id);
		statement.execute();

		ResultSet result = statement.getResultSet();
		result.first();
		String actual = result.getString(4);

		AssertJUnit.assertFalse(
				"TEST FAILED: Task status should not be updated",
				newStatus.equals(actual));
	}

	@Test(groups = { "TaskManagement" })
	public void setTaskDescription() throws SQLException {

		String newTaskDescription = "TEST: Updated task decription";
		dbconnector.setTaskDescription(task.id, newTaskDescription);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, task.id);
		statement.execute();

		ResultSet result = statement.getResultSet();
		result.first();
		String actual = result.getString(2);

		AssertJUnit.assertEquals("TEST FAILED: Task was not updated correctly",
				newTaskDescription, actual);

		AssertJUnit
				.assertFalse(
						"TEST FAILED: Database containes several tasks with the same id",
						result.next());

	}

	@Test(groups = { "TaskManagement" })
	public void setTaskDescription_emptyString() throws SQLException {

		String newTaskDescription = "";
		dbconnector.setTaskDescription(task.id, newTaskDescription);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, task.id);
		statement.execute();

		ResultSet result = statement.getResultSet();
		result.first();
		String actual = result.getString(2);

		AssertJUnit.assertEquals("TEST FAILED: Task was not updated correctly",
				newTaskDescription, actual);

		AssertJUnit
				.assertFalse(
						"TEST FAILED: Database containes several tasks with the same id",
						result.next());

	}

	@Test(groups = { "TaskManagement" })
	public void setTaskDescription_256Symbols() throws SQLException {

		String newTaskDescription = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyu";
		dbconnector.setTaskDescription(task.id, newTaskDescription);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, task.id);
		statement.execute();

		ResultSet result = statement.getResultSet();
		result.first();
		String actual = result.getString(2);

		AssertJUnit.assertEquals("TEST FAILED: Task was not updated correctly",
				newTaskDescription, actual);

		AssertJUnit
				.assertFalse(
						"TEST FAILED: Database containes several tasks with the same id",
						result.next());

	}

	@Test(groups = { "TaskManagement" })
	public void setTaskDescription_specialSymbols() throws SQLException {

		String newTaskDescription = "TEST: `~!@#$%^&*()_+=-{}[]|\';:\",.<>/?";
		dbconnector.setTaskDescription(task.id, newTaskDescription);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, task.id);
		statement.execute();

		ResultSet result = statement.getResultSet();
		result.first();
		String actual = result.getString(2);

		AssertJUnit.assertEquals("TEST FAILED: Task was not updated correctly",
				newTaskDescription, actual);

		AssertJUnit
				.assertFalse(
						"TEST FAILED: Database containes several tasks with the same id",
						result.next());

	}

	@Test(groups = { "TaskManagement" })
	public void setTaskDescription_taskNotExist() throws SQLException {

		String newTaskDescription = "TEST: Updated task decription";
		dbconnector.setTaskDescription("-1000", newTaskDescription);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE task=?");
		statement.setString(1, newTaskDescription);
		statement.execute();

		boolean result = statement.getResultSet().first();
		AssertJUnit.assertFalse(result);
	}

	@Test(groups = { "TaskManagement" })
	public void getUserID_taskExists() throws SQLException {
		int actual_userID = dbconnector.getUserID(task.id);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM tasks_tbl WHERE id=?");
		statement.setString(1, task.id);
		statement.execute();

		ResultSet result = statement.getResultSet();
		result.first();
		int expected_userID = result.getInt(3);
		AssertJUnit.assertEquals(expected_userID, actual_userID);
	}

	@Test(groups = { "TaskManagement" }, expectedExceptions = SQLException.class)
	public void getUserID_taskNotExists() throws SQLException {
		dbconnector.getUserID("-1000");
	}
}
