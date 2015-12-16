package org.gradle;

import java.sql.Connection;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
public class WebController extends WebMvcConfigurerAdapter {
	private static final Logger log = LoggerFactory
			.getLogger(Application.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String start() {
		return "index";
	}

	@ResponseBody
	@RequestMapping(value = "/ajax_add")
	public void saveTask(@RequestParam String id, @RequestParam String task,
			@RequestParam String status) throws Exception {
		DatabaseConnector dbconnector = new DatabaseConnector();

		Task newTask = new Task(id, task, status);
		dbconnector.addRecord(newTask);
		log.info("Task added");
		dbconnector.close();
	}

	@ResponseBody
	@RequestMapping(value = "/ajax_delete")
	public void deleteTask(@RequestParam String id) throws Exception {

		DatabaseConnector dbconnector = new DatabaseConnector();
		Connection connection = dbconnector.connectToDB();
		Statement statement = connection.createStatement();

		dbconnector.deleteRecord(statement, id);
		log.info("Task deleted");
		connection.close();
	}

	@ResponseBody
	@RequestMapping(value = "/ajax_changeStatus")
	public void changeStatus(@RequestParam String id, @RequestParam String status) throws Exception {

		DatabaseConnector dbconnector = new DatabaseConnector();
		Connection connection = dbconnector.connectToDB();
		Statement statement = connection.createStatement();

		dbconnector.setStatus(statement, id, status);
		log.info("Status changed");
		connection.close();
	}

	@ResponseBody
	@RequestMapping(value = "/ajax_edit")
	public void editTask(@RequestParam String id, @RequestParam String task) throws Exception {

		Connection connection = DatabaseConnector.connectToDB();

		dbconnector.setTaskDescription(statement, id, task);

		log.info("Task edited");

		connection.close();
	}
}
