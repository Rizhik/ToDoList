'use strict';
var userID;

var listOfItems = [];

var ItemStatus = {
	All : "All",
	Active : "Active",
	Completed : "Completed"
}

var statusFilter = ItemStatus.All;

// Function for adding task UI
function addButton() {
	$('#checkAll').attr('checked', false);

	var newTaskValue = $("#newTask").val();

	if (newTaskValue == '')
		return;

	addItem(newTaskValue);

	$("#newTask").val('');
	showByStatus(statusFilter);
}

// Add new task to the task list array
function addItem(taskText) {
	var taskItem = {
		"id" : generateUUID(),
		"task" : taskText,
		"status" : "Active",
		"userID" : userID,
		"editFlag" : false
	};

	listOfItems.push(taskItem);

	var json = JSON.stringify(taskItem);

	// AJAX request to Server
	$.post({
		url : "/api/task/create",
		data : json,
		contentType : "application/json"
	});
}

// remove deleted task from the screen and run deleteItem() function
function deleteButton(eventArgs) {
	var id = $(eventArgs.target).closest("li[data-id]").attr("data-id");
	deleteItem(id);
	showByStatus(statusFilter);
}

// delete task from the task list
function deleteItem(id) {
	var index = getIndexById(id);

	var json = JSON.stringify(listOfItems[index]);

	// AJAX request to Server
	$.post({
		url : "/api/task/remove",
		data : json,
		contentType : "application/json"
	});

	listOfItems.splice(index, 1);
}

// Task can be Active or Completed. When user check checkbox, status of task is
// changed
function changeStatus(eventArgs) {

	var id = $(eventArgs.target).closest("li[data-id]").attr("data-id");
	var checkbox = eventArgs.target;
	var completeTasksCount = 0;

	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].id == id) {
			if (checkbox.checked) {
				listOfItems[i].status = ItemStatus.Completed;
			} else {
				listOfItems[i].status = ItemStatus.Active;
				$('#checkAll').attr('checked', false);
			}
			// AJAX request to Server
			var json = JSON.stringify(listOfItems[i]);
			$.post({
				url : "/api/task/update",
				data : json,
				contentType : "application/json"
			});
		}

		if (listOfItems[i].status == ItemStatus.Completed) {
			completeTasksCount++;
		}
	}

	if (completeTasksCount == listOfItems.length) {
		$('#checkAll').attr('checked', true);
	}

	showByStatus(statusFilter);

}

var firstLoadData = true;

// add Listener to all checkboxes, inputs and buttons on the page
function addListener(currentStatus) {

	// ajax event listener for loading data from server
	if (firstLoadData) {
		// AJAX request to Server
		$.getJSON('/api/task/getcontent', function(responseJSON) {
			listOfItems = responseJSON;
			firstLoadData = false;
			showByStatus(statusFilter);
		});

		userID = readUserIdFromCookie();
	}

	// Add event listener to CheckAll checkbox
	$("#checkAll").change(checkAll);

	// Add event listener to input field for new task
	$("#newTask").keypress(function onEnterButton(event) {
		var key = event.which || event.keyCode;
		if (key === 13) {
			addButton();
		}
	});

	// Add event listener to input field for task in edit mode
	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].editFlag) {
			var editField = $(".edit").eq(i);
			editField.focus();

			// save all changes by Enter
			function onEnterButton(event) {
				var key = event.which || event.keyCode;
				if (key === 13) {
					unsubscribe();
					save(event);
				}
			}

			function onLooseFocuse(event) {
				unsubscribe();
				save(event);
			}

			// save all changes when focus is lost
			editField.keypress(onEnterButton).blur(onLooseFocuse);

			function unsubscribe() {
				editField.off('keypress', onEnterButton).off('blur',
						onLooseFocuse);
			}
		}
	}

	// Add event listener to each checkbox, label and DeleteButton in ToDo list
	$('button.deleteButton').click(deleteButton);
	$('input.checkbox').change(changeStatus);
	$('label.label').dblclick(editButton);

}

// Track how many tasks has status "Active"
function trackActiveTaskNumber() {
	var activeTasksCounter = 0;
	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].status == ItemStatus.Active) {
			activeTasksCounter++;
		}
	}

	$("#ClearCompletedButton").toggle(activeTasksCounter != listOfItems.length);

	$("#counter").text(activeTasksCounter + " items left");
}

// display manage content buttons only if any task exists in the task list
function displayManageContentButtons() {
	for (var i = 0; i < $(".controls").length; i++) {
		$(".controls").eq(i).toggle(listOfItems != 0);
	}
}

// create html for displaying tasks in ToDo List
function createHTMLForTasks(currentStatus) {
	var htmlCode = "";

	var itemTemplate = '<li class="{{classValue}}" data-id="{{id}}"><div><input {{status}} type="checkbox" class="checkbox"></input><label class="label">{{taskDescription}}</label><button class = "deleteButton" type="Delete">X</button><input class="edit" type="text"></div></li>';
	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].status == currentStatus
				|| currentStatus == ItemStatus.All) {
			var checkStatus = "";

			// Create class value for LI element
			// Available Classes: (editMode or empty) and (Completed or Active)
			var classValue = ItemStatus.Active;

			if (listOfItems[i].status == ItemStatus.Completed) {
				checkStatus = 'checked="checked"';
				classValue = ItemStatus.Completed.toLowerCase();
			}

			if (listOfItems[i].editFlag) {
				classValue = classValue + " editMode";
			}

			htmlCode += itemTemplate.replace(/{{status}}/g, checkStatus)
					.replace(/{{id}}/g, listOfItems[i].id).replace(
							/{{taskDescription}}/g, listOfItems[i].task)
					.replace(/{{classValue}}/g, classValue);
		}
	}

	$("#ToDoList").html(htmlCode);
}

// currentStatus cat be Active, Completed or All
function showByStatus(currentStatus) {
	// display manage content buttons only if any task exists in the task list
	displayManageContentButtons();

	// create html for displaying tasks in ToDo List
	createHTMLForTasks(currentStatus);

	// display active task counter
	trackActiveTaskNumber();

	// attach listeners to the buttons
	addListener(currentStatus);
}

// Allow to mark/un-mark all tasks as Completed
function checkAll() {

	for (var i = 0; i < listOfItems.length; i++) {

		var checkAllStatus = $("#checkAll").is(':checked');

		if (checkAllStatus && listOfItems[i].status == ItemStatus.Active) {
			listOfItems[i].status = ItemStatus.Completed;
		}

		if (!checkAllStatus && listOfItems[i].status == ItemStatus.Completed) {
			listOfItems[i].status = ItemStatus.Active;
		}

		var json = JSON.stringify(listOfItems[i]);
		$.post({
			url : "/api/task/update",
			data : json,
			contentType : "application/json"
		});
	}

	showByStatus(statusFilter);
}

// keep checkAll checkbox unchecked during first run (or refresh)
function uncheck() {
	$('#checkAll').attr('checked', false);
}

// Remove completed tasks from the page and run clearCompleted() function
function clearCompletedButton() {
	clearCompleted();
	showByStatus(statusFilter);
}

// Delete completed tasks from the list
function clearCompleted() {
	for (var i = listOfItems.length - 1; i >= 0; i--) {
		if (listOfItems[i].status == ItemStatus.Completed) {
			deleteItem(listOfItems[i].id);
		}
	}
}

// Display edit form for selected element
function editButton(eventArgs) {
	addListener(statusFilter);

	var id = $(eventArgs.target).closest("li[data-id]").attr("data-id");
	var index = getIndexById(id);
	listOfItems[index].editFlag = true;

	showByStatus(statusFilter);

	$('input.edit').val(listOfItems[index].task);
}

// Save changes after edit
function save(eventArgs) {
	var id = $(eventArgs.target).closest("li[data-id]").attr("data-id");

	var index = getIndexById(id);
	listOfItems[index].task = eventArgs.target.value;
	listOfItems[index].editFlag = false;

	// AJAX request to Server
	var json = JSON.stringify(listOfItems[index]);
	$.post({
		url : "/api/task/update",
		data : json,
		contentType : "application/json"
	});

	showByStatus(statusFilter);

}

// Get index of task in listOfItems array by its ID
function getIndexById(id) {
	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].id == id) {
			return i;
		}
	}
	return -1;
}

function readUserIdFromCookie() {
	var allcookies = document.cookie;

	// Get all the cookies pairs in an array
	var cookiearray = allcookies.split(';');

	// take key value pair out of this array
	for (var i = 0; i < cookiearray.length; i++) {
		var name = cookiearray[i].split('=')[0];
		if (name == 'current_user_id') {
			return cookiearray[i].split('=')[1];
		}
		return 0;
	}
}