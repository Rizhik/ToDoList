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

	public static Task parseString(String str) {

		int startID = str.indexOf("id=") + 3;
		int endID = str.indexOf("&");
		String id = str.substring(startID, endID);

		String copy_str = str.substring(endID + 1);

		int startTask = copy_str.indexOf("task=") + 5;
		int endTask = copy_str.indexOf("&");
		String taskDescription = copy_str.substring(startTask, endTask);

		int startStatus = copy_str.indexOf("status=") + 7;
		String status = copy_str.substring(startStatus);

		return new Task(id, taskDescription, status);
	}
}
