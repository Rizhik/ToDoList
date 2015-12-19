package org.gradle;

public class Task {
	String id;
	String taskDescription;
	String status;
	boolean editFlag = false;
	int userID;

	public Task(String id, String taskDescription, int userID, String status) {
		this.id = id;
		this.taskDescription = taskDescription;
		this.userID = userID;
		this.status = status;
	}
}
