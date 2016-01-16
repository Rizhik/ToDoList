package org.gradle;

public interface DbConnectionProvider {
	 DatabaseConnector create();
}
