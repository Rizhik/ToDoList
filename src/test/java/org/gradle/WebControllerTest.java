package org.gradle;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class, TestConfiguration.class })
@WebAppConfiguration
public class WebControllerTest {

	@Autowired
	WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

	}

	@Test
	public void indexPage_FirstLogin_Test() throws Exception {
		this.mockMvc.perform(get("/")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(cookie().doesNotExist("current_user_id"))
				.andExpect(view().name("login"));
	}

	@Test
	public void indexPage_UserLogedIn_Test() throws Exception {
		Cookie myCookie = new Cookie("current_user_id", "1");

		this.mockMvc.perform(get("/").cookie(myCookie)).andDo(print())
				.andExpect(status().isOk()).andExpect(view().name("index"));
	}

	@Test
	public void signin_Successfull_Test() throws Exception {
		this.mockMvc
				.perform(
						post("/").param("login", "admin").param("password",
								"admin")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(cookie().exists("current_user_id"))
				.andExpect(view().name("index"));
	}

	@Test
	public void signin_wrongLogin_Test() throws Exception {
		this.mockMvc
				.perform(
						post("/").param("login", "wrong_login").param(
								"password", "admin")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(cookie().doesNotExist("current_user_id"))
				.andExpect(view().name("login"));
	}

	@Test
	public void signin_wrongPassword_Test() throws Exception {
		this.mockMvc
				.perform(
						post("/").param("login", "admin").param("password",
								"wrong_password")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(cookie().doesNotExist("current_user_id"))
				.andExpect(view().name("login"));
	}

	@Test
	public void signout_Test() throws Exception {
		Cookie myCookie = new Cookie("current_user_id", "1");

		this.mockMvc.perform(get("/login").cookie(myCookie)).andDo(print())
				.andExpect(status().isOk())
				.andExpect(cookie().value("current_user_id", ""))
				.andExpect(view().name("login"));
	}

	@Test
	public void signup_Successfull_Test() throws Exception {
		this.mockMvc
				.perform(
						post("/registration").param("login",
								"test_web_controller_login").param("password",
								"test_web_controller_password")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(cookie().exists("current_user_id"))
				.andExpect(view().name("index"));
		DatabaseConnector dbconnector = new RealDbConnectionProvider().create();
		dbconnector.deleteUser("test_web_controller_login");
	}

	@Test
	public void signup_loginExist_Test() throws Exception {
		this.mockMvc
				.perform(
						post("/registration").param("login", "admin").param(
								"password", "admin")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(cookie().doesNotExist("current_user_id"))
				.andExpect(view().name("login"));
	}
}
