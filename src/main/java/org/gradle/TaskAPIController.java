package org.gradle;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
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
			@RequestParam String status, @RequestParam String userID)
			throws Exception {

		DatabaseConnector dbconnector = new DatabaseConnector();
		Task newTask = new Task(id, task, Integer.parseInt(userID), status);
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
	public ArrayList<Task> list(@CookieValue("current_user_id") String userID)
			throws Exception {
		DatabaseConnector dbconnector = new DatabaseConnector();
		ArrayList<Task> tasksList = dbconnector.getAllTasks(Integer
				.parseInt(userID));
		dbconnector.close();
		return tasksList;
	}

}
