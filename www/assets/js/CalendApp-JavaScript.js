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

function getGroupsAdmin() {
	var userid = getCookie("userid");
	var url = API_BASE_URL + '/groups/admin/' + userid;
		$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		Accept : 'application/vnd.calendapp.api.group.collection+json'
	}).done(function(data, status, jqxhr) {
				var repos = data;

				$.each(repos, function(i, v) {
					var repo = v;
					$.each(repo, function(i, z) {
							if(typeof z.name !== 'undefined'){
						console.log("Grupos Admin : " +z.name);
							$("<div class='details'><p><muted>" +z.name+" </muted><br/></p></div>").appendTo($('#desc'));}
							else{}
					});
				});
				

	}).fail(function() {
		$("#repos_result").text("No Groups.");
	});	
}


//Cargar DOC
$(document).ready(function(){
		var x = getCookie("usuario");
	console.log(x);
	getUserIndex(x);
	getGroupsAdmin();
	getLiveEvents();

});

function getPendingEvents()
{
	window.alert("ENTRA EN PENDING");
	var url = API_BASE_URL +'/events/state/'+getCookie("userid")+'/pending';
			$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		Accept : 'application/vnd.calendapp.api.event.collection+json'
	}).done(function(data, status, jqxhr) {
		console.log("EEEEEEEEEEEEEEE" +data);
				var repos = data;
					if (typeof repos.name !== 'undefined'){
						console.log("Listando pending " + repos.name);
									$("<div class='thumb'><img class='img-circle' src='assets/img/ui-danro.jpg' width='20px' height='20px' align=''></div>" +repos.name).appendTo($('#def456'));
					}

	}).fail(function() {
			window.alert("Se ha producido un error al cargar tus eventos pendientes");
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
									$("<div class='details'><p><muted>" +z.name+" </muted><br/></p></div>").appendTo($('#abc123'));
								}
								else{}
														}
						});
				});
				

	}).fail(function() {
			window.alert("Se ha producido un error al cargar tus eventos Live");
	});
	
}

function getEvents(userid){
	var url = API_BASE_URL + '/events/group/' + userid;
	console.log(url);
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
		window.alert("No se ha podido cargar tus eventos");
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
			console.log(data);
			
		});
}

