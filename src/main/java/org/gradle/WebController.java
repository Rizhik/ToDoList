package org.gradle;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(
			HttpServletResponse response,
			@CookieValue(value = "current_user_id", defaultValue = "") String currentUserID) {
		if (currentUserID.isEmpty()) {
			return "login";
		} else {
			return "index";
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String signin(@RequestParam String login,
			@RequestParam String password, HttpServletResponse response)
			throws Exception {

		DatabaseConnector dbconnector = new DatabaseConnector();

		User user = dbconnector.getUser(login);

		if (user != null) {

			boolean isVeridied = dbconnector.verifyPassword(password, user);

			if (isVeridied) {
				// create cookie and set it in response
				Cookie cookie = new Cookie("current_user_id",
						Integer.toString(user.id));
				response.addCookie(cookie);
				return "index";
			}
		}

		dbconnector.close();

		return "login";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String signup(@RequestParam String login,
			@RequestParam String password, HttpServletResponse response)
			throws Exception {
		DatabaseConnector dbconnector = new DatabaseConnector();
		boolean result = dbconnector.createNewUser(login, password);
		if (result) {
			User user = dbconnector.getUser(login);
			Cookie cookie = new Cookie("current_user_id",
					Integer.toString(user.id));
			response.addCookie(cookie);
			return "index";
		}
		dbconnector.close();
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String signOut(HttpServletResponse response) throws Exception {
		Cookie cookie = new Cookie("current_user_id", "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return "login";
	}
}
