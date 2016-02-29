'use strict';
var app = angular.module('todoApp', []);

app.directive('autofocus', function($timeout) {
	return function(scope, elem, attrs) {
		scope.$watch(attrs.autofocus, function(newVal) {
			if (newVal) {
				$timeout(function() {
					elem[0].focus();
				}, 0, false);
			}
		});
	};
});

app.controller('TodoAppController', function($http) {
	var that = this;
	this.tasks = [];

	// load data from server
	$http.get('/api/task/getcontent').then(function(response) {
		that.tasks = response.data;
	});

	this.isCompleted = function(status) {
		if (status === 'Completed') {
			return true;
		}
		return false;
	}

	this.addTask = function($event) {
		if ($event.keyCode === 13) {
			var taskItem = {
				"id" : generateUUID(),
				"task" : $event.target.value,
				"status" : "Active",
				"editFlag" : false
			};

			this.tasks.splice(0, 0, taskItem);

			// Push data to server
			var json = JSON.stringify(taskItem);
			$http.post("/api/task/create", json);
			$event.target.value = "";
		}
	};

	this.deleteTask = function(task) {
		var idForDelete = task.id;

		for (var i = 0; i < this.tasks.length; i++) {

			var currentTask = this.tasks[i];

			if (currentTask.id == idForDelete) {

				this.tasks.splice(i, 1);

				// Push data to server
				var json = JSON.stringify(currentTask);
				$http.post("/api/task/remove", json);
			}
		}
	};

	// Task can be Active or Completed. When user check checkbox, status of task
	// is changed
	this.changeStatus = function(task) {
		// Push data to server
		var json = JSON.stringify(task);
		$http.post("/api/task/update", json);
	};

	var checkAllValue = false;

	this.checkAll = function(checkAllValue) {
		for (var i = 0; i < this.tasks.length; i++) {
			var currentTask = this.tasks[i];
			if (currentTask.status != checkAllValue) {

				currentTask.status = checkAllValue;

				var json = JSON.stringify(currentTask);
				$http.post("/api/task/update", json);
			}
		}
	};

	this.allChecked = function() {
		for (var i = 0; i < this.tasks.length; i++) {
			if (this.tasks[i].status == 'Active') {
				return false;
			}
		}
		return true;
	};

	this.isReadyForDisplay = function() {
		if (this.tasks.length > 0) {
			return true;
		}
		return false;
	};

	this.isNoCompleted = function() {
		for (var i = 0; i < this.tasks.length; i++) {
			if (this.tasks[i].status === 'Completed') {
				return false;
			}
		}
		return true;
	};

	this.clearCompleted = function() {
		for (var i = this.tasks.length - 1; i >= 0; i--) {
			var currentTask = this.tasks[i];
			if (currentTask.status == 'Completed') {
				var json = JSON.stringify(currentTask);
				$http.post("/api/task/remove", json);
				this.tasks.splice(i, 1);
			}
		}
	};

	this.editModeOn = function(task) {
		task.editFlag = true;
	};

	this.editModeOff = function(task) {
		task.editFlag = false;
	};

	this.getTaskValue = function(task) {
		return task.task;
	};

	this.updateTask = function($event, task) {
		if ($event.keyCode === 13) {

			task.editFlag = false;

			// Push data to server
			var json = JSON.stringify(task);
			$http.post("/api/task/update", json);
		}
	};

});