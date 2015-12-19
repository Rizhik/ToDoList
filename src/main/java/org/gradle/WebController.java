package org.gradle;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String start() {
		return "login";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String login(@RequestParam String login,
			@RequestParam String password) throws Exception {
		DatabaseConnector dbconnector = new DatabaseConnector();
		boolean result = dbconnector.verifyUserCredentials(login, password);
		if (result) {
			return "index";
		}
		dbconnector.close();
		return "login";
	}
}
