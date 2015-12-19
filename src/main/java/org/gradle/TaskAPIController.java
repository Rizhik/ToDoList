package org.gradle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TaskAPIController {
	private static final Logger log = LoggerFactory
			.getLogger(Application.class);

	@ResponseBody
	@RequestMapping(value = "/api/task/create")
	public void save(@RequestParam String id, @RequestParam String task,
			@RequestParam String status) throws Exception {

		DatabaseConnector dbconnector = new DatabaseConnector();
		Task newTask = new Task(id, task, 1, status);
		dbconnector.addRecord(newTask);
		log.info("Task created");
		dbconnector.close();
	}

	@ResponseBody
	@RequestMapping(value = "/api/task/remove")
	public void delete(@RequestParam String id) throws Exception {

		DatabaseConnector dbconnector = new DatabaseConnector();
		dbconnector.deleteRecord(id);
		log.info("Task removed");
		dbconnector.close();
	}

	@ResponseBody
	@RequestMapping(value = "/api/task/update")
	public void edit(@RequestParam String id, @RequestParam String task,
			@RequestParam String status) throws Exception {

		DatabaseConnector dbconnector = new DatabaseConnector();
		dbconnector.setTaskDescription(id, task);
		dbconnector.setStatus(id, status);
		log.info("Task updated");
		dbconnector.close();
	}

	@ResponseBody
	@RequestMapping(value = "/api/task/getcontent", method = RequestMethod.GET)
	public String edit() throws Exception {
		DatabaseConnector dbconnector = new DatabaseConnector();
		ArrayList<Task> tasksList = dbconnector.getAllTasks();
		dbconnector.close();

		String json = "";
		ArrayList<String> temp;
		for (int i = 0; i < tasksList.size(); i++) {
			temp = new ArrayList<String>();
			temp.add("\"id\":\"" + tasksList.get(i).id + "\"");
			temp.add("\"task\":\"" + tasksList.get(i).taskDescription + "\"");
			temp.add("\"userID\":\"" + tasksList.get(i).userID + "\"");
			temp.add("\"status\":\"" + tasksList.get(i).status + "\"");
			temp.add("\"editFlag\":false");
			if (json.length() != 0) {
				json = json + ",";
			}
			String currentRow = temp.toString();
			json = json + "{"
					+ currentRow.substring(1, currentRow.length() - 1) + "}";
		}

		return "[" + json + "]";
	}
}
