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
							$("<div class='desc'><div class='thumb'><span class='badge bg-theme'><i class='fa fa-clock-o'></i></span></div><div class='details'><p>"+z.name+"</muted></p></div></div>").appendTo($('#asd123'));}
							else{}
					});
				});
				

	}).fail(function() {
		$("#repos_result").text("No Groups.");
	});	
}
//VERIFICACIÓN DE USUARIO
$("#boton_login").click(function(e) {
	var url = API_BASE_URL + '/users/login';
	e.preventDefault();
	document.cookieusername = $.cookie("usuario", ($("#usuario").val()));
	document.cookieusername = $.cookie("password", ($("#password").val()));
	var usuario = new Object();
	usuario.username = getCookie("usuario");
	usuario.userpass = getCookie("password");
	var data = JSON.stringify(usuario);
	console.log(usuario);
	
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		contentType : 'application/vnd.calendapp.api.user+json',
		data : data,
		dataType:'json',
		
	}).done(function(result, status, jqxhr) {
		var logCheck = result;		
		if (logCheck.loginSuccessful) {	
			console.log("Ususario " + logCheck.username  + " autenticado");
			getUserIndex(logCheck.username);
			var url2 = 'http://localhost/Index.html';
			$(location).attr('href',url2);
		}else if(!logCheck.loginSuccessful){
			alert("Error, revisa tus datos");
		}
});
});

//GET EVENTOS
$("#button_get_eventos").click(function(e){
	e.preventDefault();
	console.log("Entramos función listar eventos, buscamos por grupo default = 2");
	var id = 2;
	getEvents(id);
	
});

//Cargar DOC
$(document).ready(function(){
		var x = getCookie("usuario");
	console.log(x);
	getUserIndex(x);
	getGroupsAdmin();
	getLiveEvents();
	getPendingEvents();
});

function getPendingEvents()
{
	var counter = 0;
	var url = API_BASE_URL +'/events/states/'+getCookie("userid")+'/pending';
			$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
				headers:{
		Accept : 'application/vnd.calendapp.api.event.collection+json',
		},
	}).done(function(data, status, jqxhr) {
					var repos = data;
					console.log("Listando pending " + data);
					$.each(repos,function(i,v){
							$.each(v, function(j,y){
							console.log("AAAAAAAAAAA"+ y.name);
						if (typeof y.name !== 'undefined'){
							$("<div class='desc'><div class='thumb'><span class='badge bg-theme'><i class='fa fa-clock-o'></i></span></div><div class='details'><p>"+y.name+"</muted></p><button onClick='acceptgetID("+y.eventid+")' class='btn btn-success btn-xs'><i class='fa fa-check'></i></button><button onClick='declinegetID("+y.eventid+")' class='btn btn-danger btn-xs'><i class='fa fa-trash-o'></i></button></div></div>").appendTo($('#def456'));
							counter += 1;
							
						}
						});
					});
					$(counter).appendTo($('#not_counter'));
					$(counter + "Invitaciones a eventos").appendTo($('#jeje'));
				    $("Tienes " +counter+ " notificaciones").appendTo($('#nots'));

				

	}).fail(function() {
			window.alert("Se ha producido un error al cargar tus eventos pendientes");
	});
	
}

function acceptgetID(id)
{
	window.alert("Evento accept " +id);
	var url = API_BASE_URL +'/events/state/'+id+'/'+getCookie("userid")+'/join';
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		headers:{
		},
	}).done(function(data, status, jqxhr) {
			window.alert("Has confirmado tu asistencia");
  	}).fail(function() {
			window.alert("Error");
	});
}
function declinegetID(id)
{
		window.alert("Evento decline " +id);
	var url = API_BASE_URL +'/events/state/'+id+'/'+getCookie("userid")+'/decline';
	console.log(url);
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json'
		}).done(function(data, status, jqxhr) {
			window.alert("Has confirmado tu asistencia");
  	}).fail(function() {
			window.alert("Error");
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
									$("<div class='desc'><div class='thumb'><span class='badge bg-theme'><i class='fa fa-clock-o'></i></span></div><div class='details'><p>"+asd.name+"</muted></p></div></div>").appendTo($('#abcd123'));
								}
								else{}
														}
						});
				});
				

	}).fail(function() {
			window.alert("Se ha producido un error al cargar tus eventos Live");
	});
	
}
/*$("#poner-nombre").click(function(e){
	console.log("kjljklj");
	e.preventDefault();
	var x = getCookie("usuario");
	console.log(x);
	getUserIndex(x);
	//console.log("Nombre ususario recogido de login.html :" + x);
});*/

/*$("#button_get_groups").click(function(e) {
	e.preventDefault();
	$.cookie("cookies_prueba", 10);
	var x = getCookie("userid");
	getGroups(getCookie("userid"));
});
*/
/*$("#button_get_group").click(function(e) {
	e.preventDefault();
	getGrupo($("#nombre_grupo").val());
	$.cookie("cookies_prueba", 10);
	console.log("Imprime usando la funcion console "  + console.debug($.cookie("usuario")));
	//console.log("El valor del cookie es " + x);
	console.log($("#nombre_grupo").val())
});
*/
$("#button_get_repo_to_edit").click(function(e) {
	e.preventDefault();
	getRepoToEdit($("#repository_name_get_to_edit").val());
});


$("#button_edit_repo").click(function(e) {
	e.preventDefault();

    var newRepo = new Object();
	newRepo.name = $("#repository_name_to_edit").val()
	newRepo.description = $("#description_to_edit").val()
	
	updateRepo(newRepo);
});

/*$("#button_create_group").click(function(e) {
	e.preventDefault();

    var group = new Object();
	group.name = $("#group_name").val();
	group.admin="angel";
	group.description = $("#group_description").val();
	//group.shared = true;
	//if($("#button_create_group").val() == 'n')
	//{
	//	group.shared = false;
	//}

	createGroup(group);
});
*/


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

/*function getGroups(abc) {
	console.log("variable recogida " +abc);
	var url = API_BASE_URL + '/groups/user/' + abc;
	$("#repos_result").text('');
	console.log(url);
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
					$('<div class="form-panel"> <br><strong> Name: ' + z.name + '</strong><br><strong> ID: </strong> ' + z.groupid + '<br></div>').appendTo($('#get_repo_result'));
					});
				});
				

	}).fail(function() {
		$("#repos_result").text("No repositories.");
	});

}
*/

function getUserIndex(username){
		$("#user_name").text('');
		$('<h5 class="centered" id="user_name"> Bienvenido, '+ username+'</h5>').appendTo($('#user_name'));
		var url = API_BASE_URL + '/users/' + username;
		$.ajax({
			url : url,
			type : 'GET',
			crossDomain : true,
			dataType : 'json',
					headers:{
		Accept : 'application/vnd.calendapp.api.user+json',
		}
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

/*function getGrupo(groupid) {
	var url = API_BASE_URL + '/groups?name=' +groupid;
	$("#get_repo_result").text('');

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {

				var grupo = data;
					$
				console.log(grupo);
				$("#get_repo_result").text('');
				$('<strong> Nombre: </strong> ' + grupo.name + '<br>').appendTo($('#get_repo_result'));
				$('<strong> Administrador: ' + grupo.admin + '<br>').appendTo($('#get_repo_result'));
				$('<strong> Descripcion: </strong> ' + grupo.description + '<br>').appendTo($('#get_repo_result'));

			}).fail(function() {
				$('<div class="alert alert-danger"> <strong>Oh!</strong> Repository not found </div>').appendTo($("#get_repo_result"));
	});

}
*/


