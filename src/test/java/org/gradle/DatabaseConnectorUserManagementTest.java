package org.gradle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DatabaseConnectorUserManagementTest {

	private DatabaseConnector dbconnector;

	@BeforeGroups(groups = { "UserManagement" })
	public void setup() throws Exception {
		dbconnector = new DatabaseConnector();
	}

	@AfterGroups(groups = { "UserManagement" })
	public void tearDown() throws Exception {
		dbconnector.close();
	}

	User user;

	@BeforeMethod(groups = { "UserManagement" })
	public void prepareUserTestData() {
		try {
			user = new User(-25, "tester", "tester");
			PreparedStatement statement = dbconnector
					.getConnection()
					.prepareStatement(
							"INSERT INTO users_tbl  (id, login, password) VALUES (0, ?, ?)");
			statement.setString(1, user.login);
			statement.setString(2, user.password);
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("BEFORE METHOD FAILED: " + e);
		}
	}

	@AfterMethod(groups = { "UserManagement" })
	public void cleanupUserDatabase() throws SQLException {
		try {
			PreparedStatement statement = dbconnector.getConnection()
					.prepareStatement("DELETE FROM users_tbl WHERE login=?");
			statement.setString(1, user.login);
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("AFTER METHOD FAILED: " + e);
		}
	}

	@Test(groups = { "UserManagement" })
	public void createNewUser() throws SQLException {
		cleanupUserDatabase();
		boolean isCreated = dbconnector
				.createNewUser(user.login, user.password);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM users_tbl WHERE login=?");
		statement.setString(1, user.login);
		statement.execute();

		ResultSet result = statement.getResultSet();
		AssertJUnit.assertTrue("TEST FAILED: result set is empty",
				result.first());

		AssertJUnit.assertEquals("TEST FAILED: login is incorrect", user.login,
				result.getString(2));
		AssertJUnit.assertEquals("TEST FAILED: password is incorrect",
				user.password, result.getString(3));
		AssertJUnit.assertTrue("TEST FAILED: user is NOT created", isCreated);
	}

	@Test(groups = { "UserManagement" })
	public void createNewUser_passwordExists() throws SQLException {
		boolean isCreated = dbconnector.createNewUser("tester2", user.password);
		AssertJUnit.assertTrue("TEST FAILED: user is NOT created", isCreated);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM users_tbl WHERE login=?");
		statement.setString(1, "tester2");
		statement.execute();

		ResultSet result = statement.getResultSet();
		AssertJUnit.assertTrue("TEST FAILED: result set is empty",
				result.first());

		AssertJUnit.assertEquals("TEST FAILED: login is incorrect", "tester2",
				result.getString(2));
		AssertJUnit.assertEquals("TEST FAILED: password is incorrect",
				user.password, result.getString(3));

		statement = dbconnector.getConnection().prepareStatement(
				"DELETE FROM users_tbl WHERE login=?");
		statement.setString(1, "tester2");
		statement.executeUpdate();
	}

	@Test(groups = { "UserManagement" })
	public void createNewUser_loginExists() throws SQLException {

		boolean isCreated = dbconnector
				.createNewUser(user.login, user.password);

		AssertJUnit.assertFalse(isCreated);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM users_tbl WHERE login=?");
		statement.setString(1, user.login);
		statement.execute();

		ResultSet result = statement.getResultSet();
		AssertJUnit.assertTrue("TEST FAILED: result set is empty",
				result.first());
		AssertJUnit.assertFalse(result.next());
	}

	@Test(groups = { "UserManagement" })
	public void getUser_loginExists() throws SQLException {
		User actual_user = dbconnector.getUser(user.login);

		PreparedStatement statement = dbconnector.getConnection()
				.prepareStatement("SELECT * FROM users_tbl WHERE login=?");
		statement.setString(1, user.login);
		statement.execute();

		ResultSet result = statement.getResultSet();
		AssertJUnit.assertTrue("TEST FAILED: result set is empty",
				result.first());
		User expected_user = new User(result.getInt(1), result.getString(2),
				result.getString(3));
		AssertJUnit.assertEquals(expected_user.id, actual_user.id);
		AssertJUnit.assertEquals(expected_user.login, actual_user.login);
		AssertJUnit.assertEquals(expected_user.password, actual_user.password);
		AssertJUnit
				.assertFalse("TEST FAILED: several users with the same login",
						result.next());

	}

	@Test(groups = { "UserManagement" }, expectedExceptions = SQLException.class)
	public void getUser_loginNotExists() throws SQLException {
		dbconnector.getUser("login_not_exists");
	}

	@Test(groups = { "UserManagement" })
	public void verifyPassword_correctPassword() throws SQLException {
		boolean actualResult = dbconnector.verifyPassword("tester", user);
		AssertJUnit.assertTrue(actualResult);
	}

	@Test(groups = { "UserManagement" })
	public void verifyPassword_incorrectPassword() throws SQLException {
		boolean actualResult = dbconnector.verifyPassword("tes", user);
		AssertJUnit.assertFalse(actualResult);
	}
}
