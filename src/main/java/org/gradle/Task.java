package org.gradle;

public class Task {
	String id;
	String taskDescription;
	String status;
	boolean editFlag = false;
	int userID = 1;

	public Task(String id, String taskDescription, String status) {
		this.id = id;
		this.taskDescription = taskDescription;
		this.status = status;
	}
}
