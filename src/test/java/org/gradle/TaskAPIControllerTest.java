package org.gradle;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WebAppConfiguration
public class TaskAPIControllerTest {
	@Autowired
	WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

	}

	@Test
	public void CRUD_Test() throws Exception {

		// CREATE
		Task task = new Task("test0000", "Some task description", -1, "Active");
		Gson gson = new Gson();
		String json_task = gson.toJson(task);
		this.mockMvc
				.perform(
						post("/api/task/create").contentType(
								MediaType.APPLICATION_JSON).content(json_task))
				.andDo(print()).andExpect(status().isOk());

		Cookie testCookie = new Cookie("current_user_id", "-1");

		ArrayList<Task> task_list = new ArrayList<Task>();
		task_list.add(task);

		String result_json = this.mockMvc
				.perform(get("/api/task/getcontent").cookie(testCookie))
				.andDo(print()).andExpect(status().isOk()).andReturn()
				.getResponse().getContentAsString();

		assertEquals(gson.toJson(task_list), result_json);

		// EDIT
		Task task_updated = new Task("test0000",
				"Some task description: UPDATED", -1, "Completed");

		String json_task_updated = gson.toJson(task_updated);
		this.mockMvc
				.perform(
						post("/api/task/update").contentType(
								MediaType.APPLICATION_JSON).content(
								json_task_updated)).andDo(print())
				.andExpect(status().isOk());

		task_list.remove(0);
		task_list.add(task_updated);

		result_json = this.mockMvc
				.perform(get("/api/task/getcontent").cookie(testCookie))
				.andDo(print()).andExpect(status().isOk()).andReturn()
				.getResponse().getContentAsString();

		assertEquals(gson.toJson(task_list), result_json);

		// DELETE
		String json_task_to_delete = gson.toJson(task_updated);
		this.mockMvc
				.perform(
						post("/api/task/remove").contentType(
								MediaType.APPLICATION_JSON).content(
								json_task_to_delete)).andDo(print())
				.andExpect(status().isOk());

		result_json = this.mockMvc
				.perform(get("/api/task/getcontent").cookie(testCookie))
				.andDo(print()).andExpect(status().isOk()).andReturn()
				.getResponse().getContentAsString();

		task_list.remove(0);

		assertEquals(gson.toJson(task_list), result_json);
	}
}
