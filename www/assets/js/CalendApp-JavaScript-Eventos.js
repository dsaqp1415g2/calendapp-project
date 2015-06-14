var API_BASE_URL = "http://147.83.7.158:8080/calendapp-api";
var USERNAME = getCookie("usuario");
var PASSWORD = getCookie("password");

$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});

/*
Details about repository of GitHub API 
https://developer.github.com/v3/repos/
*/

//COOKIES//
//SETCOOKIE
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}
//GETCOOKIE
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length,c.length);
    }
    return "";
}
//GET EVENTOS
$("#button_get_eventos").click(function(e){
	e.preventDefault();
	//console.log("Entramos función listar eventos, buscamos por grupo default = 2");
	var id = 2;
	getEvents(id);
	
});
$("#button_delete_event").click(function(e){
	e.preventDefault();
	var event = new Object();
	event.eventid = $("#event_name").val();
	deleteEvent(event);
	
});
$("#button_create_event").click(function(e){
	e.preventDefault();
	var event = new Object();
	var z = document.getElementById("opts");
	var opt = z.options[z.selectedIndex].value;
	var myDate = $("#inicio").val();
	var myDate = myDate+"T00:00:00";	
	console.log(myDate);
	var dateStart  = new Date(myDate);
	dateStart = dateStart.getTime();
    var notmyDate = $("#final").val();
	var notmyDate = notmyDate+"T00:00:00";
	var dateEnd = new Date(notmyDate);
	dateEnd = dateEnd.getTime();
	event.name = $("#group_name").val();
	event.dateInitial = dateStart;
	event.dateFinish = dateEnd;
	event.groupid = opt;
	if ($("#group_name").val() !=''){
			createEvent(event);
	}else{
		window.alert("El nombre no puede estar en blanco");
	}
	//console.log("Entramos función listar eventos, buscamos por grupo default = 2");

	//FALTA REStRIngIR INPUT
});
$("#button_create_event_pri").click(function(e){
	e.preventDefault();
	var event = new Object();
	event.name = $("#group_name_pri").val();
	var myDate = $("#inicio_pri").val();
	var myDate = myDate+"T00:00:00";	
	console.log(myDate);
	var dateStart  = new Date(myDate);
	dateStart = dateStart.getTime();
    var notmyDate = $("#final_pri").val();
	var notmyDate = notmyDate+"T00:00:00";
	var dateEnd = new Date(notmyDate);
	dateEnd = dateEnd.getTime();
	event.dateInitial = dateStart;
	event.dateFinish = dateEnd;
	event.userid = getCookie("userid");
	event.groupid = 0;
	if ($("#group_name_pri").val() !=''){
			createEvent(event);
	}else{
		window.alert("El nombre no puede estar en blanco");
	}
	//console.log("Entramos función listar eventos, buscamos por grupo default = 2");

	//FALTA REStRIngIR INPUT
});



//Cargar DOC
$(document).ready(function(){
		var x = getCookie("usuario");
	//console.log(x);
	getUserIndex(x);
	getGroupsAdmin();
	//getGroups(getCookie("userid"));
	getLiveEvents();
});

function createEvent(event){
	var url = API_BASE_URL + '/events';
	console.log(event);
	var data = JSON.stringify(event);
	console.log(data);
	console.log("")
	$("#get_repo_result").text('');
	console.log(event);
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
		headers:{
		Accept : 'application/vnd.calendapp.api.event+json',
		"content-Type": 'application/vnd.calendapp.api.event+json'
		},
		statusCode :{
			400 : function() {window.alert("Error de formato en el JSON");}
		}
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Event Created</div>').appendTo($("#create_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#create_result"));
	});
}

function getEvents(userid){
	var url = API_BASE_URL + '/events/group/' + userid;
	//console.log(url);
	$("repos_result").text('');
		$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				var repos = data;
				$.each(repos, function(i, v) {
					var repo = v;
					$.each(repo, function(i, z) {
					
					console.log(z);
					$('<br><strong> Nombre del evento: ' + z.name + '</strong><br>').appendTo($('#get_repo_result'));
					$('<strong> ID: </strong> ' + z.groupid + '<br>').appendTo($('#get_repo_result'));
					});
				});
				

	}).fail(function() {
		$("#repos_result").text("No repositories.");
	});
	
}


function getUserIndex(username){
		$("#user_name").text('');
		$('<h5 class="centered" id="user_name"> Bienvenido, '+ username+'</h5>').appendTo($('#user_name'));
		var url = API_BASE_URL + '/users/' + username;
		$.ajax({
			url : url,
			type : 'GET',
			crossDomain : true,
			dataType : 'json',
		}).done(function(data,status,jqxhr)
		{
			setCookie("username", data.name);
			setCookie("mail", data.email);
			setCookie("userid", data.userid);
			setCookie("userage", data.age);
			//var z=getCookie("username");
			//var y = getCookie("mail");
			//console.log("Post Login El nombre del usuario es " + z + " con mail " + y);
			//console.log(data);
			
		});
}

function getGroupsAdmin() {
	//console.log("variable recogida " +abc);
	var url = API_BASE_URL + '/groups/admin/' + getCookie("userid");
	$("#repos_result").text('');
	//console.log(url);
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				var repos = data;
				$.each(repos, function(i, v) {
					var repo = v;
						//console.log(repo);
						//console.log(repo.name);
					
					$.each(repo, function(j, k){
						var z = k;
						if(typeof z.name !== "undefined"){
							console.log("Grupo - " +z.name);
					$('<option value='+z.groupid+'>'+z.name+'</option>').appendTo($('#opts'));
						}
					});
				});
				

	}).fail(function() {
		$("#repos_result").text("No repositories.");
	});

}

function getLiveEvents(){
	var z = getCookie("userid");
	var url = API_BASE_URL + '/events/now/' + z;
	//console.log("ENTRA EN LIVE EVENTS");
	
			$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				console.log("EVENTOS LIVE");
				var repos = data;
				$.each(repos, function(i, v) {
					var repo = v;
						$.each(repo, function(i,z){
							var asd = z;
						if(typeof asd.name !== "undefined"){
								if(asd.groupid == 0)
								{
					$('<a onClick="idevent('+asd.eventid+','+asd.groupid+')"> <div class="form-panel"> <br><strong> Name: ' + asd.name + '</strong><br><strong> Grupo: </strong> Privado<br></div></a>').appendTo($('#events_now'));
								}
								else{
								$('<a onClick="idevent('+asd.eventid+')"> <div class="form-panel"> <br><strong> Name: ' + asd.name + '</strong><br><strong> Grupo: </strong> ' +asd.groupid+'<br></div></a>').appendTo($('#events_now'));

								}
						}
						});
				});
				

	}).fail(function() {
		$("#repos_result").text("No repositories.");
	});
	
}
function idevent(abc, def)
{
	setCookie("eventid", abc);
	setCookie("isprivate", def);
	//if($.cookie("eventid"))
	//{
		$(location).attr('href', "http://localhost/Detalle Evento.html");
	//}
	//else{
	//	window.alert("Impossible obtener ID grupo");
//	}
	
}
/*function getGroupsAdmin() {
	var userid = getCookie("userid");
	var url = API_BASE_URL + '/groups/admin/' + userid;
		$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers:{
		Accept : 'application/vnd.calendapp.api.group.collection+json',
		}
	}).done(function(data, status, jqxhr) {
				var repos = data;
				$.each(repos, function(i, v) {
					var repo = v;
					$.each(repo, function(i, z) {
							if(typeof z.name !== 'undefined'){
						console.log("Grupos Admin : " +z.name);
							$("<div class='thumb'><span class='badge bg-theme'><i class='fa fa-clock-o'></i></span></div><div class='details'><p><muted>" +z.name+" </muted><br/></p></div>").appendTo($('#asd123'));}
							else{}
					});
				});
				

	}).fail(function() {
		$("#repos_result").text("No Groups.");
	});	
}
*/
function deleteEvent(abc){
	console.log(abc.eventid);
	var url = API_BASE_URL + '/events/' + abc.eventid;
	
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
					console.log("DELETED OK");
								
	}).fail(function() {
		console.log("DELETE MALA");
	});

	
}

