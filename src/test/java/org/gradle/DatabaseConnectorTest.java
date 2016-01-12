package org.gradle;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class DatabaseConnectorTest {
	@Test(groups = { "DatabaseInit" })
	public void firstRun_CleanDB() {
		AssertJUnit.assertTrue("Test Not Implemented", false);
	}

	@Test(groups = { "DatabaseInit" })
	public void firstRun_allTablesExist() {
		AssertJUnit.assertTrue("Test Not Implemented", false);
	}

	@Test(groups = { "DatabaseInit" })
	public void firstRun_onlyTaskTableExists() {
		AssertJUnit.assertTrue("Test Not Implemented", false);
	}

	@Test(groups = { "DatabaseInit" })
	public void firstRun_onlyUserTableExists() {
		AssertJUnit.assertTrue("Test Not Implemented", false);
	}
}
