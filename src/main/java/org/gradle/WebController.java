package org.gradle;

import java.sql.ResultSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
	public String login(
			@RequestParam String login,
			@RequestParam String password,
			HttpServletResponse response) throws Exception {
		DatabaseConnector dbconnector = new DatabaseConnector();
		ResultSet result = dbconnector.getUser(login);
		boolean isVeridied = dbconnector.verifyPassword(password, result);

		if (isVeridied) {
			int current_user_id = dbconnector.getUserID(result);

			// create cookie and set it in response
			Cookie cookie = new Cookie("current_user_id",
					Integer.toString(current_user_id));
			response.addCookie(cookie);
			return "index";
		}
		dbconnector.close();
		return "login";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String signup(@RequestParam String login,
			@RequestParam String password) throws Exception {
		DatabaseConnector dbconnector = new DatabaseConnector();
		boolean result = dbconnector.createNewUser(login, password);
		if (result) {
			return "index";
		}
		dbconnector.close();
		return "login";
	}
}
