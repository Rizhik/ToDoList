package org.gradle;

import java.sql.Connection;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public void saveTask(@RequestBody String task) throws Exception {

		DataBaseConnector dbconnector = new DataBaseConnector();
		Connection connection = dbconnector.connectToDB();
		Statement statement = connection.createStatement();

		Task newTask = Task.parseString(task);
		dbconnector.addRecord(statement, newTask);
		log.info("Task added");
		connection.close();
	}

	@ResponseBody
	@RequestMapping(value = "/ajax_delete")
	public void deleteTask(@RequestBody String id) throws Exception {

		DataBaseConnector dbconnector = new DataBaseConnector();
		Connection connection = dbconnector.connectToDB();
		Statement statement = connection.createStatement();

		dbconnector.deleteRecord(statement, id.substring(3));
		log.info("Task deleted");
		connection.close();
	}

	@ResponseBody
	@RequestMapping(value = "/ajax_changeStatus")
	public void changeStatus(@RequestBody String task) throws Exception {

		DataBaseConnector dbconnector = new DataBaseConnector();
		Connection connection = dbconnector.connectToDB();
		Statement statement = connection.createStatement();

		int startID = task.indexOf("id=") + 3;
		int endID = task.indexOf("&");
		String id = task.substring(startID, endID);
		
		int startStatus = task.indexOf("status=") + 7;
		String status = task.substring(startStatus);
		
		dbconnector.setStatus(statement, id, status);
		log.info("Status changed");
		connection.close();
	}
}
