'use strict';
var userID;

var listOfItems = [];

var ItemStatus = {
	All : "All",
	Active : "Active",
	Completed : "Completed"
}

var statusFilter = ItemStatus.All;
var checkAllBox = document.getElementById("checkAll");
var newTaskField = document.getElementById('newTask');
var ul = document.getElementById("ToDoList");

// Function for adding task UI
function addButton() {
	checkAllBox.checked = false;

	if (newTaskField.value == '')
		return;

	addItem(newTaskField.value);

	newTaskField.value = '';
	showByStatus(statusFilter);
}

function ajax(url, method, data, onSucccess) {
	var xhttp = new XMLHttpRequest();
	xhttp.open(method, url, true);
	xhttp.setRequestHeader("Accept", "application/json");
	xhttp.setRequestHeader("Content-type", "application/json");
	if (method == 'POST') {
		xhttp.send(data);
	} else {
		xhttp.send();
	}

	if (onSucccess)

		xhttp.onreadystatechange = function() {
			if (xhttp.readyState == 4 && xhttp.status == 200) {

				onSucccess(xhttp.responseText);
			}
		}
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
	ajax("/api/task/create", "POST", json);
}

// remove deleted task from the screen and run deleteItem() function
function deleteButton(eventArgs) {
	var id = getDataIdValue(eventArgs.target);
	deleteItem(id);
	showByStatus(statusFilter);
}

// delete task from the task list
function deleteItem(id) {
	var index = getIndexById(id);
	
	// AJAX request to Server
	var json = JSON.stringify(listOfItems[index]);
	
	ajax("/api/task/remove", "POST", json);

	listOfItems.splice(index, 1);
}

// Task can be Active or Completed. When user check checkbox, status of task is
// changed
function changeStatus(eventArgs) {

	var id = getDataIdValue(eventArgs.target);
	var checkbox = eventArgs.target;
	var completeTasksCount = 0;

	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].id == id) {
			if (checkbox.checked) {
				listOfItems[i].status = ItemStatus.Completed;

				// AJAX request to Server
				var json = JSON.stringify(listOfItems[i]);
				ajax("/api/task/update", "POST", json);
			} else {
				listOfItems[i].status = ItemStatus.Active;

				// AJAX request to Server
				var json = JSON.stringify(listOfItems[i]);
				ajax("/api/task/update", "POST", json);

				checkAllBox.checked = false;
			}
		}

		if (listOfItems[i].status == ItemStatus.Completed) {
			completeTasksCount++;
		}
	}

	if (completeTasksCount == listOfItems.length) {
		checkAllBox.checked = true;
	}

	showByStatus(statusFilter);

}

var firstLoadData = true;


// add Listener to all checkboxes, inputs and buttons on the page
function addListener(currentStatus) {

	// ajax event listener for loading data from server
	if (firstLoadData) {
		// AJAX request to Server

		ajax("/api/task/getcontent", "GET","" ,function (responseText) {
			listOfItems = JSON.parse(responseText);
			firstLoadData = false;
			showByStatus(statusFilter);
		});
		userID = readUserIdFromCookie();
	}

	// Add event listener to CheckAll checkbox
	checkAllBox.addEventListener("change", checkAll);

	// Add event listener to input field for new task
	newTaskField.addEventListener('keypress', function onEnterButton(event) {
		var key = event.which || event.keyCode;
		if (key === 13) {
			addButton();
		}
	});

	// Add event listener to input field for task in edit mode
	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].editFlag) {
			var editElement = document.getElementsByClassName("edit")[i];
			editElement.focus();

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

			editElement.addEventListener('keypress', onEnterButton);

			// save all changes if focus was lost
			editElement.addEventListener('blur', onLooseFocuse);

			function unsubscribe() {
				editElement.removeEventListener('keypress', onEnterButton);
				editElement.removeEventListener('blur', onLooseFocuse);
			}
		}
	}

	// Add event listener to each checkbox, label and DeleteButton in ToDo list
	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].status == currentStatus
				|| currentStatus == ItemStatus.All) {
			var currentLI = findNode(ul, "data-id", listOfItems[i].id);
			if (currentLI != null) {
				var label = findNode(currentLI, "class", "label");
				label.addEventListener("dblclick", editButton);

				var del = findNode(currentLI, "class", "deleteButton");
				del.addEventListener("click", deleteButton);

				var check = findNode(currentLI, "class", "checkbox");
				check.addEventListener("change", changeStatus);
			} else {
				console.log("EVENT LISTENER -  DOES NOT WORK");
			}
		}
	}
}

// Track how many tasks has status "Active"
function trackActiveTaskNumber() {
	var activeTasksCounter = 0;
	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].status == ItemStatus.Active) {
			activeTasksCounter++;
		}
	}
	if ((activeTasksCounter != listOfItems.length)) {
		document.getElementById("ClearCompletedButton").style.display = "inline";
	} else {
		document.getElementById("ClearCompletedButton").style.display = "none";
	}
	document.getElementById("counter").innerHTML = activeTasksCounter
			+ " items left";
}

// display manage content buttons only if any task exists in the task list
function displayManageContentButtons() {
	var buttons = document.getElementsByClassName("controls");
	for (var i = 0; i < buttons.length; i++) {
		if (listOfItems != 0) {
			buttons[i].style.display = "inline";
		} else {
			buttons[i].style.display = "none";
		}
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

	document.getElementById("ToDoList").innerHTML = htmlCode;
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

	var currentValueofCheckbox = checkAllBox.checked;

	for (var i = 0; i < listOfItems.length; i++) {
		if (currentValueofCheckbox
				&& listOfItems[i].status == ItemStatus.Active) {
			listOfItems[i].status = ItemStatus.Completed;
		}

		if (!currentValueofCheckbox
				&& listOfItems[i].status == ItemStatus.Completed) {
			listOfItems[i].status = ItemStatus.Active;
		}
	}

	showByStatus(statusFilter);
}

// keep checkAll checkbox unchecked during first run (or refresh)
function uncheck() {
	checkAllBox.checked = false;
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
			listOfItems.splice(i, 1);
		}
	}
}

// Display edit form for selected element
function editButton(eventArgs) {
	addListener(statusFilter);

	var id = getDataIdValue(eventArgs.target);
	var index = getIndexById(id);
	listOfItems[index].editFlag = true;

	showByStatus(statusFilter);

	var tagParent = findNode(ul, "data-id", id);
	var input = findNode(tagParent, "class", "edit");
	input.value = listOfItems[index].task;
}

// Save changes after edit
function save(eventArgs) {
	var id = getDataIdValue(eventArgs.target);
	var index = getIndexById(id);
	listOfItems[index].task = eventArgs.target.value;
	listOfItems[index].editFlag = false;

	// AJAX request to Server
	var json = JSON.stringify(listOfItems[index]);
	ajax("/api/task/update", "POST", json);

	showByStatus(statusFilter);

}

// Get index of task by its ID
function getIndexById(id) {
	for (var i = 0; i < listOfItems.length; i++) {
		if (listOfItems[i].id == id) {
			return i;
		}
	}
	return -1;
}

// find LI by DATA-ID
function getElementByDataId(id) {
	var list = document.getElementsByTagName("ul");

	for (var i = 0; i < list.length; i++) {
		var currentResult = findNode(list[i], "data-id", id);
		if (currentResult != null) {
			return currentResult;
		}
	}
	return null;
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