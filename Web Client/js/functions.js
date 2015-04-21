/**
 * Function: LoginScreen
 * Parameters: None
 * Description: Generates the Login and Signup Form and adds them
 *		to the DOM.
 *
**/
var LoginScreen = function () {

	// Variables for the HTML elements.
	var wrapper_complete = document.createElement("div");
		var form_login = document.createElement("form");
			var wrapper_login = document.createElement("div");
				var emblem = document.createElement("img");
				var wrapper_login_input_email = document.createElement("div");
					var icon_login_email = document.createElement("i");
					var input_login_email = document.createElement("input");
					var label_login_email = document.createElement("label");
				var wrapper_login_input_password = document.createElement("div");
					var icon_login_password = document.createElement("i");
					var input_login_password = document.createElement("input");
					var label_login_password = document.createElement("label");
				var wrapper_login_input_submit = document.createElement("div");
					var button_login_submit = document.createElement("button");
						var icon_login_submit = document.createElement("i");
				var wrapper_show_signup = document.createElement("div");			// andrew's additions
					var button_show_signup = document.createElement("button");		// 
					
		var form_signup = document.createElement("form");
			var wrapper_signup = document.createElement("div");
				var signup_prompt = document.createElement("p");
				var wrapper_signup_input_email = document.createElement("div");
					var icon_signup_email = document.createElement("i");
					var input_signup_email = document.createElement("input");
					var label_signup_email = document.createElement("label");
				var wrapper_signup_input_password = document.createElement("div");
					var icon_signup_password = document.createElement("i");
					var input_signup_password = document.createElement("input");
					var label_signup_password = document.createElement("label");
				var wrapper_signup_input_submit = document.createElement("div");
					var button_signup_submit = document.createElement("button");
						//var icon_signup_submit = document.createElement("i");


	wrapper_complete.setAttribute("class", "row");
		form_login.setAttribute("class", "login");
		form_login.setAttribute("id", "loginForm");
			wrapper_login.setAttribute("class", "row");
				emblem.setAttribute("class", "emblem");
				emblem.setAttribute("src", "assets/web_hi_res_512.png");
				wrapper_login_input_email.setAttribute("class", "input-field col s12");
					//icon_login_email.setAttribute("class", "mdi-content-mail prefix");
					input_login_email.setAttribute("placeholder", "Email");
					input_login_email.setAttribute("id", "login_email");
					input_login_email.setAttribute("type", "text");
					input_login_email.setAttribute("class", "validate");
					label_login_email.setAttribute("for", "login_email");
				wrapper_login_input_password.setAttribute("class", "input-field col s12");
					//icon_login_password.setAttribute("class", "mdi-communication-vpn-key prefix");
					input_login_password.setAttribute("placeholder", "Password");
					input_login_password.setAttribute("id", "login_password");
					input_login_password.setAttribute("type", "password");
					input_login_password.setAttribute("class", "validate");
					label_login_password.setAttribute("for", "login_password");
				wrapper_login_input_submit.setAttribute("class", "row");
					button_login_submit.setAttribute("class", "btn waves-effect waves-light light-blue darken-1 login-button");
					button_login_submit.setAttribute("type", "submit");
					button_login_submit.setAttribute("name", "action");
					button_login_submit.setAttribute("onclick", "javascript:LoginUser()");
					button_login_submit.innerHTML = "Login";
						//icon_login_submit.setAttribute("class", "mdi-content-send right");
				wrapper_show_signup.setAttribute("class", "row");														//
					button_show_signup.setAttribute("class", "btn waves-effect waves-light white btn-flat grey-text login-button");	// andrew's stuff
					button_show_signup.innerHTML = "New User"															//
					button_show_signup.setAttribute("onclick", "javascript:ShowSignUp()")
		form_signup.setAttribute("class", "signup");
		form_signup.setAttribute("id", "signupForm");
			wrapper_signup.setAttribute("class", "row");
				signup_prompt.innerHTML = "New user?";
				signup_prompt.setAttribute("class", "center signup-prompt");
				wrapper_signup_input_email.setAttribute("class", "input-field col s12");
					icon_signup_email.setAttribute("class", "mdi-content-mail prefix ");
					input_signup_email.setAttribute("placeholder", "Email");
					input_signup_email.setAttribute("id", "login_email");
					input_signup_email.setAttribute("type", "text");
					input_signup_email.setAttribute("class", "validate");
					label_signup_email.setAttribute("for", "signup_email");
				wrapper_signup_input_password.setAttribute("class", "input-field col s12");
					icon_signup_password.setAttribute("class", "mdi-communication-vpn-key prefix");
					input_signup_password.setAttribute("placeholder", "Password");
					input_signup_password.setAttribute("id", "login_password");
					input_signup_password.setAttribute("type", "password");
					input_signup_password.setAttribute("class", "validate");
					label_signup_password.setAttribute("for", "signup_password");
				wrapper_signup_input_submit.setAttribute("class", "row");
					button_signup_submit.setAttribute("class", "btn waves-effect waves-light light-blue darken-1 login-button");
					button_signup_submit.setAttribute("type", "submit");
					button_signup_submit.setAttribute("name", "action");
					button_signup_submit.setAttribute("onclick", "javascript:SignupUser()");
					button_signup_submit.innerHTML = "Signup";
						//icon_signup_submit.setAttribute("class", "mdi-content-send right");

	var mainContainer = document.getElementById("mainContainer");

	mainContainer.appendChild(wrapper_complete);
	wrapper_complete.appendChild(form_login);
		form_login.appendChild(wrapper_login);
			wrapper_login.appendChild(emblem);
			wrapper_login.appendChild(wrapper_login_input_email);
				wrapper_login_input_email.appendChild(icon_login_email);
				wrapper_login_input_email.appendChild(input_login_email);
				wrapper_login_input_email.appendChild(label_login_email);
			wrapper_login.appendChild(wrapper_login_input_password);
				wrapper_login_input_password.appendChild(icon_login_password);
				wrapper_login_input_password.appendChild(input_login_password);
				wrapper_login_input_password.appendChild(label_login_password);
			wrapper_login.appendChild(wrapper_login_input_submit);
				//wrapper_login_input_submit.appendChild(button_show_signup);
				wrapper_login_input_submit.appendChild(button_login_submit);
					button_login_submit.appendChild(icon_login_submit);
			//wrapper_login.appendChild(wrapper_show_signup);
	
	wrapper_complete.appendChild(form_signup);
		form_signup.appendChild(wrapper_signup);
			wrapper_signup.appendChild(signup_prompt);
			wrapper_signup.appendChild(wrapper_signup_input_email);
				//wrapper_signup_input_email.appendChild(icon_signup_email);
				wrapper_signup_input_email.appendChild(input_signup_email);
				wrapper_signup_input_email.appendChild(label_signup_email);
			wrapper_signup.appendChild(wrapper_signup_input_password);
				//wrapper_signup_input_password.appendChild(icon_signup_password);
				wrapper_signup_input_password.appendChild(input_signup_password);
				wrapper_signup_input_password.appendChild(label_signup_password);
			wrapper_signup.appendChild(wrapper_signup_input_submit);
				wrapper_signup_input_submit.appendChild(button_signup_submit);
					//button_signup_submit.appendChild(icon_signup_submit);

	//mainContainer.setAttribute("class", "indigo lighten-5");

}





/**
 * Function: LoginUser
 * Parameters: None
 * Description: Takes the login form data as input, reads the
 *		values the user typed in, and checks to see if said user
 *		and password is in the Parse backend. If they are,
 *		the login and signup forms are removed from the DOM,
 *		and we display the app (load the App Dashboard).
 *
**/
var LoginUser = function () {
	Parse.initialize("tdorw6A41hVhwXtMn1Pe07jfPXQOZyJEP6ztLNAX",
						  "nitkBWLYXc8TLdmMxLxU65z4ZBObt6xOguFoSe7x");
	var form = document.getElementById("loginForm");
	var user = form[0].value;
	var pass = form[1].value;
	Parse.User.logIn(user, pass, {
		success: function() {
			console.log("Successful login!");
			var mainContainer = document.getElementById("mainContainer");
			mainContainer.removeChild(mainContainer.children[0]);
			Dashboard();
		},
		error: function() {
			console.log("Unsuccessful login!");
		}
	});
}

var SignupUser = function (f) {
}

var Dashboard = function () {

	MakeBody();

}

var LogoutUser = function () {

	Parse.User.logOut();
	var body = document.getElementById("mainContainer");
	body.removeChild(body.children[0]);
	LoginScreen();

}

var MakeBody = function () {

	var mainContainer = document.getElementById("mainContainer");

	// Remove everything from main.
	while (mainContainer.firstChild) {
		mainContainer.removeChild(mainContainer.firstChild);
	}

	var wrapper = document.createElement("div");
		wrapper.setAttribute("class", "row");

	var sidebar = document.createElement("div");
		sidebar.setAttribute("id", "sidebar");
		sidebar.setAttribute("class", "col s12 m4 l3 purple lighten-4");

	var content = document.createElement("div");
		content.setAttribute("id", "content");
		content.setAttribute("class", "col m8 l9 purple accent-1");

	MakeSidebar(sidebar);
	MakeContent(content);

	mainContainer.appendChild(wrapper);
	wrapper.appendChild(sidebar);
	wrapper.appendChild(content);

}

var MakeSidebar = function ( sidebar ) {

	var sideuserinfo = document.createElement("ul");
		sideuserinfo.setAttribute("class", "collection");
	var sideviews = document.createElement("div");
		sideviews.setAttribute("class", "collection");
	var sidelists = document.createElement("div");
		sidelists.setAttribute("class", "collection");
		sidelists.setAttribute("id", "sidelists");
	var sideoptions = document.createElement("div");
		sideoptions.setAttribute("class", "collection");

	SInfo(sideuserinfo);
	sidebar.appendChild(sideuserinfo);

	ViewInbox(sideviews);
	ViewUpcoming(sideviews);
	ViewComplete(sideviews);
	ViewAllTasks(sideviews);
	ViewUnassigned(sideviews);
	sidebar.appendChild(sideviews);

	ActiveLists(sidelists);
	sidebar.appendChild(sidelists);

	OptionCreateNew(sideoptions);
	//OptionSettings(sideoptions);
	//OptionAbout(sideoptions);
	OptionLogout(sideoptions);
	sidebar.appendChild(sideoptions);

}

var SInfo = function ( list ) {

	var item_username = document.createElement("li");
		item_username.setAttribute("id", "item_username");
		item_username.setAttribute("class", "collection-item left-align");
		item_username.innerHTML = Parse.User.current().getUsername();

	list.appendChild(item_username);

}

var ViewInbox = function ( list ) {

	var item_inbox = document.createElement("a");
		item_inbox.setAttribute("id", "item_inbox");
		item_inbox.setAttribute("href", "#!");
		item_inbox.setAttribute("class", "collection-item left-align");
		item_inbox.addEventListener('click', function() {ContentInbox()}, false);
		item_inbox.innerHTML = "Inbox";

	list.appendChild(item_inbox);

}

var ContentInbox = function () {

	// Variables
	var Task = Parse.Object.extend("Task");
	var query = new Parse.Query(Task);

	// Find all Tasks belonging to the logged in user,
	//		and display them.
	// * Need to figure out how to compare dates.
	query.equalTo("user", Parse.User.current());
	DisplayTaskQuery(query);
}

var ViewUpcoming = function ( list ) {
	var item_upcoming = document.createElement("a");
	item_upcoming.setAttribute("id", "item_upcoming");
	item_upcoming.setAttribute("href", "#!");
	item_upcoming.setAttribute("class", "collection-item left-align");
	item_upcoming.addEventListener('click', function() {ContentUpcoming()}, false);
	item_upcoming.innerHTML = "Upcoming";

	list.appendChild(item_upcoming);
}

var ContentUpcoming = function () {

	// Variables
	var Task = Parse.Object.extend("Task");
	var query = new Parse.Query(Task);

	// Find all Tasks belonging to the logged in user,
	//		and display them.
	// * Need to figure out how to compare dates.
	query.equalTo("user", Parse.User.current());
	DisplayTaskQuery(query);
}

var ViewComplete = function ( list ) {

	var item_complete = document.createElement("a");
	item_complete.setAttribute("id", "item_complete");
	item_complete.setAttribute("href", "#!");
	item_complete.setAttribute("class", "collection-item left-align");
	item_complete.addEventListener('click', function() {ContentComplete()}, false);
	item_complete.innerHTML = "Complete";

	list.appendChild(item_complete);
}

var ContentComplete = function () {

	// Variables
	var Task = Parse.Object.extend("Task");
	var query = new Parse.Query(Task);

	// Find all Tasks belonging to the logged in user which
	//		are not completed.
	query.equalTo("user", Parse.User.current());
	query.equalTo("done", true);
	DisplayTaskQuery(query);
}

var ViewAllTasks = function ( list ) {
	var item_alltasks = document.createElement("a");
	item_alltasks.setAttribute("id", "item_alltasks");
	item_alltasks.setAttribute("href", "#!");
	item_alltasks.setAttribute("class", "collection-item left-align");
	item_alltasks.addEventListener('click', function() {ContentAllTasks()}, false);
	item_alltasks.innerHTML = "All Tasks";

	list.appendChild(item_alltasks);
}

var ContentAllTasks = function () {

	// Variables
	var Task = Parse.Object.extend("Task");
	var query = new Parse.Query(Task);

	query.equalTo("user", Parse.User.current());
	DisplayTaskQuery(query);
}

var ViewUnassigned = function ( list ) {
	var item_unassigned = document.createElement("a");
	item_unassigned.setAttribute("id", "item_unassigned");
	item_unassigned.setAttribute("href", "#!");
	item_unassigned.setAttribute("class", "collection-item left-align");
	item_unassigned.addEventListener('click', function() {ContentUnassigned()}, false);
	item_unassigned.innerHTML = "Unassigned";

	list.appendChild(item_unassigned);
}

var ContentUnassigned = function () {

	var Task = Parse.Object.extend("Task");
	var query = new Parse.Query(Task);

	query.equalTo("user", Parse.User.current());
	query.equalTo("parent", null);
	DisplayTaskQuery(query);
}

var ActiveLists = function ( list ) {

	var user = Parse.User.current();
	var List = Parse.Object.extend("List");
	var query = new Parse.Query(List);

	query.equalTo("user", user);
	query.find({
		success: function (results) {
			for (var i = 0; i < results.length; ++i) {
				var parse_list_item = results[i];
				var list_item = document.createElement("a");
				list_item.setAttribute("id", "list" + i);
				list_item.setAttribute("href", "#!");
				list_item.setAttribute("class", "collection-item left-align");
				list_item.addEventListener('click', function() {DisplayList(this.id)}, false);
				list_item.innerHTML = parse_list_item.get('name');
				list.appendChild(list_item);
			}
			console.log(list.length);
			console.log("Lists owned by user above.");
		}
	});

}

var DisplayList = function ( lid ) {

	var List = Parse.Object.extend("List");
	var query = new Parse.Query(List);
	var curList = document.getElementById(lid);
	var curListName = curList.innerHTML;

	query.equalTo("user", Parse.User.current());
	console.log("user id => " + Parse.User.current().id);
	query.find({
		success: function(result) {
			console.log("num results = " + result.length);
			console.log("user found = " + result[0].get('user'));
		}
	});
	query.equalTo("name", curListName);
	query.find({
		success: function(result) {
			var good = result[0];
			console.log(good);
			var Task = Parse.Object.extend("Task");
			var query2 = new Parse.Query(Task);

			query2.equalTo("user", Parse.User.current());
			query2.equalTo("parent", good);
			DisplayTaskQuery(query2, lid);
		},
		error: function() {
			console.log("sad face");
		}
	});

}

var OptionCreateNew = function ( list ) {

	var item_create_new = document.createElement("a");
	item_create_new.setAttribute("id", "item_create_new");
	item_create_new.setAttribute("href", "#!");
	item_create_new.setAttribute("class", "collection-item left-align");
	item_create_new.setAttribute("onclick", "javascript:ListPrompt()");
	item_create_new.innerHTML = "Create New...";

	list.appendChild(item_create_new);

}

var ListPrompt = function () {

	var lname = prompt("List Name");
	var List = Parse.Object.extend("List");
	var query = new Parse.Query(List);
	query.equalTo("user", Parse.User.current());
	query.equalTo("name", lname);

	ListDNE(query, function(hits) {
		if (hits == 0 ) {
			AddList(lname);
			UpdateActiveLists(lname);
		}
		else alert("List already exists!");
	});

}

function AddList(name) {
	var List = Parse.Object.extend("List");
	var list = new List();

	list.set("name", name);
	list.set("user", Parse.User.current());
	list.save(null, {
		success: function(results) {}, error: function() {console.log("FUCKKKKKKKKKKKKKKKK");}
	});

}

function UpdateActiveLists (name) {

	var sidelists = document.getElementById("sidelists");
	var list = document.createElement("a");
	var sizeof_sidelists = sidelists.children.length;
	
	list.setAttribute("id", "list" + sizeof_sidelists);
	list.setAttribute("href", "#!");
	list.setAttribute("class", "collection-item left-align");
	list.addEventListener('click', function() {DisplayList(this.id)}, false);
	list.innerHTML = name;

	sidelists.appendChild(list);
}

	


function ListDNE(query, callback) {

	query.count({
		success: function (results) {
			if (results == 0) callback(results);
			else callback(results);
		},
		error: function () {
			callback(1);
		}
	});

}

var OptionSettings = function ( div ) {
	var s = document.createElement("button");
	s.setAttribute("id", "oSettings");
	s.innerHTML = "Settings";
	div.appendChild(s);
}

var OptionAbout = function ( div ) {
	var a = document.createElement("button");
	a.setAttribute("id", "oAbout");
	a.innerHTML = "About";
	div.appendChild(a);
}

var OptionLogout = function ( list ) {

	var item_logout = document.createElement("a");
	item_logout.setAttribute("id", "item_logout");
	item_logout.setAttribute("href", "#!");
	item_logout.setAttribute("class", "collection-item left-align");
	item_logout.setAttribute("onclick", "javascript:LogoutUser()");
	item_logout.innerHTML = "Logout";

	list.appendChild(item_logout);

}

var MakeContent = function ( div ) {
	div.innerHTML = "THE CONTENT GOES HERE.";
}

var DisplayTaskQuery = function ( query , list_id ) {

	var content = document.getElementById("content");
	var task_list = document.createElement("ul");
		task_list.setAttribute("class", "collection");

	// Remove everything inside our content box from the DOM.
	while (content.firstChild) {
		content.removeChild(content.firstChild);
	}

	if (content.parentNode.lastChild.getAttribute("id") == "task_button")
		content.parentNode.removeChild(content.parentNode.lastChild);


	query.find({
		success: function (tasks) {
			console.log("SUCCESS");
			for (var i = 0; i < tasks.length; ++i) {
				var task = tasks[i];
				var item_task = document.createElement("li");
				item_task.setAttribute("class", "collection-item left-align");
				item_task.innerHTML = task.get("name");
				task_list.appendChild(item_task);
			}
		},
		error: function() {
			console.log("No tasks to display.");
		}
	});

	content.appendChild(task_list);

	// Create the Add Task button to add a task to a given list.
	var button_wrapper = document.createElement("a");
		button_wrapper.setAttribute("id", "task_button");
		button_wrapper.setAttribute("class", "btn-floating btn-large waves-effect waves-light red right-align");
		button_wrapper.setAttribute("style", "position: relative; bottom: 0px; right: 0px;");
		button_wrapper.setAttribute("onclick", "javascript:AddTaskToList()");
		var button_image = document.createElement("i");
			button_image.setAttribute("class", "mdi-content-add");

	var button_box = document.createElement("div");
		button_box.setAttribute("style", "postition: absolute; background-color: red");

	button_wrapper.appendChild(button_image);
	button_box.appendChild(button_wrapper);
	content.parentNode.appendChild(button_box);
}

var AddTaskToList = function () {

	prompt("Task Name");





}
