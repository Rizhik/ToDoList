package org.gradle;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TaskAPIController {
	private static final Logger log = LoggerFactory
			.getLogger(Application.class);

	@Autowired
	public DbConnectionProvider DbConnectionProvider;

	@ResponseBody
	@RequestMapping(value = "/api/task/create", method = RequestMethod.POST)
	public void save(@RequestBody Task task) throws Exception {

		DatabaseConnector dbconnector = DbConnectionProvider.create();
		dbconnector.addTask(task);
		log.info("Task created");
		dbconnector.close();
	}

	@ResponseBody
	@RequestMapping(value = "/api/task/remove")
	// POST
	public void delete(@RequestBody Task task) throws Exception {

		DatabaseConnector dbconnector = DbConnectionProvider.create();
		dbconnector.deleteTask(task.id);
		log.info("Task removed");
		dbconnector.close();
	}

	@ResponseBody
	@RequestMapping(value = "/api/task/update")
	// POST
	public void edit(@RequestBody Task task) throws Exception {

		DatabaseConnector dbconnector = DbConnectionProvider.create();
		dbconnector.setTaskDescription(task.id, task.task);
		dbconnector.setStatus(task.id, task.status);
		log.info("Task updated");
		dbconnector.close();
	}

	@ResponseBody
	@RequestMapping(value = "/api/task/getcontent", method = RequestMethod.GET)
	public ArrayList<Task> list(@CookieValue("current_user_id") String userID)
			throws Exception {
		DatabaseConnector dbconnector = DbConnectionProvider.create();
		ArrayList<Task> tasksList = dbconnector.getAllTasks(Integer
				.parseInt(userID));
		dbconnector.close();
		return tasksList;
	}

}
