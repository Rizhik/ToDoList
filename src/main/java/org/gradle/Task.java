package org.gradle;

public class Task {
	public String id;
	public String task;
	public String status;
	public boolean editFlag = false;
	public int userID;

	public Task() {
	}

	public Task(String id, String taskDescription, int userID, String status) {
		this.id = id;
		this.task = taskDescription;
		this.userID = userID;
		this.status = status;
	}
}
