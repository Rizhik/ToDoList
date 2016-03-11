describe(
		"TodoAppController",
		function() {

			beforeEach(module('todoApp'));

			var $controller;
			var $httpBackend;

			beforeEach(inject(function(_$controller_, _$httpBackend_) {
				// The injector unwraps the underscores (_) from around the
				// parameter
				// names when matching
				$controller = _$controller_;
				$httpBackend = _$httpBackend_;
			}));

			afterEach(function() {
				$httpBackend.verifyNoOutstandingExpectation();
				$httpBackend.verifyNoOutstandingRequest();
			});

			it(
					"should not load any content for logged in user, if user doesn`t have any tasks",
					function() {
						var ctrl = $controller("TodoAppController");
						expect(ctrl).not.toBe(null);

						$httpBackend.expectGET("/api/task/getcontent").respond(
								200, '[]');
						expect(ctrl.tasks.length).toBe(0);
						$httpBackend.flush();

						expect(ctrl.tasks.length).toBe(0);
					});

			it(
					"should load existing content for logged in user, if user has some tasks",
					function() {
						var ctrl = $controller("TodoAppController");
						// Verify that controller is created
						expect(ctrl).not.toBe(null);

						// Create test data
						var tasks = [ {
							"id" : "30dd9e9a",
							"task" : "First task description",
							"status" : "Active",
							"editFlag" : false
						}, {
							"id" : "2324c513",
							"task" : "Second task description",
							"status" : "Completed",
							"editFlag" : true
						} ];

						// Create GET request to server
						$httpBackend.expectGET("/api/task/getcontent").respond(
								200, tasks);

						// Verify that client array of tasks is empty
						expect(ctrl.tasks.length).toBe(0);

						// Get respond from server
						$httpBackend.flush();

						// Verify content size
						expect(ctrl.tasks.length).toBe(2);

						// Verify task content
						for (var i = 0; i < tasks.length; i++) {
							expect(ctrl.tasks[i].id).toBe(tasks[i].id);
							expect(ctrl.tasks[i].task).toBe(tasks[i].task);
							expect(ctrl.tasks[i].status).toBe(tasks[i].status);
							expect(ctrl.tasks[i].editFlag).toBe(
									tasks[i].editFlag);
						}

					});
			describe("addTask()", function() {
				it("should add new task when enter is pressed", function() {
					$httpBackend.expectGET("/api/task/getcontent").respond(200,
							'[]');
					var ctrl = $controller("TodoAppController");
					$httpBackend.flush();

					var event = {
						target : {
							value : "Test"
						},
						keyCode : 13
					};
					$httpBackend.expectPOST("/api/task/create", ctrl.tasks[0])
							.respond(200);

					ctrl.addTask(event);
					$httpBackend.flush();

					expect(ctrl.tasks.length).toBe(1);
					expect(ctrl.tasks[0].task).toBe("Test");
					expect(ctrl.tasks[0].status).toBe("Active");
					expect(ctrl.tasks[0].editFlag).toBe(false);

					expect(event.target.value).toBe("");
				});
			});

			describe("deleteTask()", function() {
				it("should delete task when delete button is clicked",
						function() {
							$httpBackend.expectGET("/api/task/getcontent")
									.respond(200, '[]');
							var ctrl = $controller("TodoAppController");
							$httpBackend.flush();
							var event = {
								target : {
									value : "Test"
								},
								keyCode : 13
							};
							$httpBackend.expectPOST("/api/task/create",
									ctrl.tasks[0]).respond(200);
							ctrl.addTask(event);
							$httpBackend.flush();

							expect(ctrl.tasks.length).toBe(1);

							$httpBackend.expectPOST("/api/task/remove",
									ctrl.tasks[0]).respond(200);
							ctrl.deleteTask(ctrl.tasks[0]);
							$httpBackend.flush();

							expect(ctrl.tasks.length).toBe(0);
						});
			});
			describe(
					"changeStatus()",
					function() {
						it(
								"should change task's status from Active to Completed when checkbox is checked",
								function() {
									$httpBackend
											.expectGET("/api/task/getcontent")
											.respond(
													200,
													'[{"id" : "30dd9e9a","task" : "Initial status is Active","status" : "Active", "editFlag" : false }]');
									var ctrl = $controller("TodoAppController");
									$httpBackend.flush();

									var updatedTask = {
										"id" : ctrl.tasks[0].id,
										"task" : "Initial status is Active",
										"status" : "Completed",
										"editFlag" : false
									};

									$httpBackend.expectPOST("/api/task/update",
											updatedTask).respond(200);
									ctrl.changeStatus(updatedTask);
									$httpBackend.flush();
								});

						it(
								"should change task's status from Completed to Active when checkbox is unchecked",
								function() {
									$httpBackend
											.expectGET("/api/task/getcontent")
											.respond(
													200,
													'[{"id" : "30dd9e9a","task" : "Initial status is Completed","status" : "Completed", "editFlag" : false }]');
									var ctrl = $controller("TodoAppController");
									$httpBackend.flush();

									var updatedTask = {
										"id" : ctrl.tasks[0].id,
										"task" : "Initial status is Completed",
										"status" : "Active",
										"editFlag" : false
									};

									$httpBackend.expectPOST("/api/task/update",
											updatedTask).respond(200);
									ctrl.changeStatus(updatedTask);
									$httpBackend.flush();
								});
					});
			describe("checkAll()", function() {
				it("should mark all tasks as Completed", function() {

					var ctrl = $controller("TodoAppController");

					// Create test data
					var tasks = [ {
						"id" : "30dd9e9a",
						"task" : "First task description",
						"status" : "Active",
						"editFlag" : false
					}, {
						"id" : "2324c513",
						"task" : "Second task description",
						"status" : "Completed",
						"editFlag" : true
					}, {
						"id" : "2324c514",
						"task" : "Third task description",
						"status" : "Active",
						"editFlag" : true
					} ];

					$httpBackend.expectGET("/api/task/getcontent").respond(200,
							tasks);
					$httpBackend.flush();

					expect(ctrl.checkAllValue).toBe(false);

					ctrl.checkAllValue = true;
					expect(ctrl.checkAllValue).toBe(true);

					ctrl.checkAll('Completed');

					expect(ctrl.tasks[0].status).toBe('Completed');
					expect(ctrl.tasks[1].status).toBe('Completed');
					expect(ctrl.tasks[2].status).toBe('Completed');

					$httpBackend.expectPOST("/api/task/update", ctrl.tasks[0])
							.respond(200);
					$httpBackend.expectPOST("/api/task/update", ctrl.tasks[2])
							.respond(200);
					$httpBackend.flush();
				});

				it("should mark all tasks as Active", function() {

					var ctrl = $controller("TodoAppController");

					// Create test data
					var tasks = [ {
						"id" : "30dd9e9a",
						"task" : "First task description",
						"status" : "Completed",
						"editFlag" : false
					}, {
						"id" : "2324c513",
						"task" : "Second task description",
						"status" : "Completed",
						"editFlag" : true
					}, {
						"id" : "2324c514",
						"task" : "Third task description",
						"status" : "Active",
						"editFlag" : true
					} ];

					$httpBackend.expectGET("/api/task/getcontent").respond(200,
							tasks);
					$httpBackend.flush();

					expect(ctrl.checkAllValue).toBe(false);

					ctrl.checkAll('Active');

					expect(ctrl.tasks[0].status).toBe('Active');
					expect(ctrl.tasks[1].status).toBe('Active');
					expect(ctrl.tasks[2].status).toBe('Active');

					$httpBackend.expectPOST("/api/task/update", ctrl.tasks[0])
							.respond(200);
					$httpBackend.expectPOST("/api/task/update", ctrl.tasks[1])
							.respond(200);
					$httpBackend.flush();
				});
			});

			describe(
					"isReadyForDisplay()",
					function() {
						it(
								"should return true (SHOW) if tasks array length more than 0",
								function() {

									var ctrl = $controller("TodoAppController");

									// Create test data
									var tasks = [ {
										"id" : "30dd9e9a",
										"task" : "First task description",
										"status" : "Active",
										"editFlag" : false
									} ];

									$httpBackend.expectGET(
											"/api/task/getcontent").respond(
											200, tasks);
									$httpBackend.flush();

									expect(ctrl.tasks.length).toBe(1);

									expect(ctrl.isReadyForDisplay()).toBe(true);
								});

						it(
								"should return false (HIDE) if tasks array length is 0",
								function() {

									var ctrl = $controller("TodoAppController");
									$httpBackend.expectGET(
											"/api/task/getcontent").respond(
											200, '[]');
									$httpBackend.flush();

									expect(ctrl.tasks.length).toBe(0);
									expect(ctrl.isReadyForDisplay())
											.toBe(false);
								});
					});

			describe(
					"isNoCompleted()",
					function() {
						it(
								"should return true if ALL tasks have status - Active",
								function() {

									var ctrl = $controller("TodoAppController");

									// Create test data
									var tasks = [ {
										"id" : "30dd9e9a",
										"task" : "First task description",
										"status" : "Active",
										"editFlag" : false
									}, {
										"id" : "2324c513",
										"task" : "Second task description",
										"status" : "Active",
										"editFlag" : true
									}, {
										"id" : "2324c514",
										"task" : "Third task description",
										"status" : "Active",
										"editFlag" : true
									} ];

									$httpBackend.expectGET(
											"/api/task/getcontent").respond(
											200, tasks);
									$httpBackend.flush();

									expect(ctrl.isNoCompleted()).toBe(true);
								});

						it(
								"should return false if AT LEAST ONE task has status - Completed",
								function() {

									var ctrl = $controller("TodoAppController");
									// Create test data
									var tasks = [ {
										"id" : "30dd9e9a",
										"task" : "First task description",
										"status" : "Completed",
										"editFlag" : false
									}, {
										"id" : "2324c513",
										"task" : "Second task description",
										"status" : "Active",
										"editFlag" : true
									}, {
										"id" : "2324c514",
										"task" : "Third task description",
										"status" : "Active",
										"editFlag" : true
									} ];
									$httpBackend.expectGET(
											"/api/task/getcontent").respond(
											200, tasks);
									$httpBackend.flush();

									expect(ctrl.isNoCompleted()).toBe(false);
								});
					});

			describe(
					"clearCompleted()",
					function() {
						it(
								"should delete ALL tasks with Completed status",
								function() {

									var ctrl = $controller("TodoAppController");

									// Create test data
									var tasks = [ {
										"id" : "30dd9e9a",
										"task" : "First task description",
										"status" : "Active",
										"editFlag" : false
									}, {
										"id" : "2324c513",
										"task" : "Second task description",
										"status" : "Completed",
										"editFlag" : true
									}, {
										"id" : "2324c514",
										"task" : "Third task description",
										"status" : "Completed",
										"editFlag" : true
									} ];

									$httpBackend.expectGET(
											"/api/task/getcontent").respond(
											200, tasks);
									$httpBackend.flush();

									ctrl.clearCompleted();

									$httpBackend.expectPOST("/api/task/remove",
											ctrl.tasks[1]).respond(200);
									$httpBackend.expectPOST("/api/task/remove",
											ctrl.tasks[2]).respond(200);
									$httpBackend.flush();

									expect(ctrl.tasks.length).toBe(1);
									expect(ctrl.tasks[0].status).toBe('Active');
								});

						it(
								"should NOT delete any task if all tasks has Active status",
								function() {

									var ctrl = $controller("TodoAppController");
									// Create test data
									var tasks = [ {
										"id" : "30dd9e9a",
										"task" : "First task description",
										"status" : "Active",
										"editFlag" : false
									}, {
										"id" : "2324c513",
										"task" : "Second task description",
										"status" : "Active",
										"editFlag" : true
									}, {
										"id" : "2324c514",
										"task" : "Third task description",
										"status" : "Active",
										"editFlag" : true
									} ];
									$httpBackend.expectGET(
											"/api/task/getcontent").respond(
											200, tasks);
									$httpBackend.flush();

									ctrl.clearCompleted();

									expect(ctrl.tasks.length).toBe(3);
									expect(ctrl.tasks[0].status).toBe('Active');
									expect(ctrl.tasks[1].status).toBe('Active');
									expect(ctrl.tasks[2].status).toBe('Active');
								});

						it(
								"should erase tasks array if all tasks has Completed status",
								function() {

									var ctrl = $controller("TodoAppController");
									// Create test data
									var tasks = [ {
										"id" : "30dd9e9a",
										"task" : "First task description",
										"status" : "Completed",
										"editFlag" : false
									}, {
										"id" : "2324c513",
										"task" : "Second task description",
										"status" : "Completed",
										"editFlag" : true
									}, {
										"id" : "2324c514",
										"task" : "Third task description",
										"status" : "Completed",
										"editFlag" : true
									} ];
									$httpBackend.expectGET(
											"/api/task/getcontent").respond(
											200, tasks);
									$httpBackend.flush();

									ctrl.clearCompleted();

									$httpBackend.expectPOST("/api/task/remove",
											ctrl.tasks[0]).respond(200);
									$httpBackend.expectPOST("/api/task/remove",
											ctrl.tasks[1]).respond(200);
									$httpBackend.expectPOST("/api/task/remove",
											ctrl.tasks[2]).respond(200);
									$httpBackend.flush();

									expect(ctrl.tasks.length).toBe(0);

								});
					});

			describe("editModeOn()", function() {
				it("should change editFlag value from false to true",
						function() {

							var ctrl = $controller("TodoAppController");

							// Create test data
							var task = {
								"id" : "30dd9e9a",
								"task" : "First task description",
								"status" : "Active",
								"editFlag" : false
							};

							$httpBackend.expectGET("/api/task/getcontent")
									.respond(200, '[]');
							$httpBackend.flush();

							expect(task.editFlag).toBe(false);
							ctrl.editModeOn(task);

							expect(task.editFlag).toBe(true);

						});

				it("should change editFlag value from true to true",
						function() {

							var ctrl = $controller("TodoAppController");

							// Create test data
							var task = {
								"id" : "30dd9e9a",
								"task" : "First task description",
								"status" : "Active",
								"editFlag" : true
							};

							$httpBackend.expectGET("/api/task/getcontent")
									.respond(200, '[]');
							$httpBackend.flush();

							expect(task.editFlag).toBe(true);
							ctrl.editModeOn(task);

							expect(task.editFlag).toBe(true);
						});

			});

			describe("editModeOff()", function() {
				it("should change editFlag value from true to false",
						function() {

							var ctrl = $controller("TodoAppController");

							// Create test data
							var task = {
								"id" : "30dd9e9a",
								"task" : "First task description",
								"status" : "Active",
								"editFlag" : true
							};

							$httpBackend.expectGET("/api/task/getcontent")
									.respond(200, '[]');
							$httpBackend.flush();

							expect(task.editFlag).toBe(true);
							ctrl.editModeOff(task);

							expect(task.editFlag).toBe(false);

						});

				it("should change editFlag value from false to false",
						function() {

							var ctrl = $controller("TodoAppController");

							// Create test data
							var task = {
								"id" : "30dd9e9a",
								"task" : "First task description",
								"status" : "Active",
								"editFlag" : false
							};

							$httpBackend.expectGET("/api/task/getcontent")
									.respond(200, '[]');
							$httpBackend.flush();

							expect(task.editFlag).toBe(false);
							ctrl.editModeOff(task);

							expect(task.editFlag).toBe(false);
						});

			});

			describe("getTaskValue()", function() {
				it("should change editFlag value from true to false",
						function() {

							var ctrl = $controller("TodoAppController");

							// Create test data
							var task = {
								"id" : "30dd9e9a",
								"task" : "First task description",
								"status" : "Active",
								"editFlag" : true
							};

							$httpBackend.expectGET("/api/task/getcontent")
									.respond(200, '[]');
							$httpBackend.flush();

							expect(ctrl.getTaskValue(task)).toBe(task.task);

						});
			});

			describe("updateTask()", function() {
				it("should update task value and set editFlag to false",
						function() {

							var tasks = [ {
								"id" : "30dd9e9a",
								"task" : "First task description",
								"status" : "Active",
								"editFlag" : true
							} ];

							$httpBackend.expectGET("/api/task/getcontent")
									.respond(200, tasks);
							var ctrl = $controller("TodoAppController");
							$httpBackend.flush();

							expect(ctrl.tasks[0].task).toBe(tasks[0].task);
							// Create test data

							var event = {
								target : {
									value : "UPDATED task description"
								},
								keyCode : 13
							};

							expect(tasks[0].editFlag).toBe(true);

							ctrl.updateTask(event, tasks[0]);

							expect(tasks[0].editFlag).toBe(false);

							$httpBackend.expectPOST("/api/task/update",
									tasks[0]).respond(200);
							$httpBackend.flush();
						});
			});
		});
