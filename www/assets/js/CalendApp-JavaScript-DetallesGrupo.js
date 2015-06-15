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

function TStoDate(abc)
{
	var date = new Date(abc);
	var day = date.getDate();
	var month = date.getMonth(); 
	var year = date.getFullYear();
	var completeDate =  day+'/'+month+'/'+year;
	return completeDate;
}
//Cargar DOC
$(document).ready(function(){
		var x = getCookie("usuario");
	console.log(x);
	var z = getCookie("groupid");
	var y = getCookie("groupstatus");
	if(y == 'false')
	{
		window.alert(y);
		 $('<input id="textbox_invite" type="text" class="form-control" disabled>').appendTo($("#nombre_invite"));
	}
	else
	{
				 $('<input id="textbox_invite" type="text" class="form-control">').appendTo($("#nombre_invite"));
	}
	getGrupo(z);
	getUsersGroup(z);
	getEvents(z);
});
function getUsersGroup(a){
	
	var url = API_BASE_URL + '/groups/' + a + '/accepted/';
	console.log("URL GET USUSARIOS gROUP " +url);
	var vector_names = [];
			$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			Accept : 'application/vnd.calendapp.api.user.collection+json',
		}
	}).done(function(data, status, jqxhr) {
				var repos = data;
				$.each(repos, function(i, v) {
					var repo = v;
					$.each(repo, function(i, z) {

					console.log(z);
						if (typeof z.name !== "undefined"){
							vector_names.push(z.name);
						console.log("Vector nombres  " +vector_names);
						}

					});
										$('<h6 class="mb"><b class="fa fa-angle-right"></b>' +vector_names+ '</h6>').appendTo($('#member_list'));

				});
				

	}).fail(function() {
			window.alert("No se han encontrado usuarios para este grupo");
	});
	
}


function getEvents(groupid){
	var url = API_BASE_URL + '/events/group/' + groupid;
	console.log(url);
	$("repos_result").text('');
		$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			Accept : 'application/vnd.calendapp.api.event.collection+json',
		}
	}).done(function(data, status, jqxhr) {
				var repos = data;
				$.each(repos, function(i, v) {
					var repo = v;
					$.each(repo, function(i, z) {

					console.log(z);
					if (typeof z.name !== "undefined"){
					$('<a href="Detalle Evento.html"><div class="form-panel"  id=""> <br><strong> Evento : ' + z.name + '</strong><br><strong> Duración: </strong> ' + TStoDate(z.dateInitial) + '-' +TStoDate(z.dateFinish)+ '<br></div></a>').appendTo($('#event_list'));
					}
					});
				});
				

	}).fail(function() {
		window.alert("No se han encontrado eventos para este grupo");
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
		var usuario = new Object();
		var url = API_BASE_URL + '/users/' + username;
		$.ajax({
			url : url,
			type : 'GET',
			crossDomain : true,
			dataType : 'json',
			headers : {
				Accept : 'application/vnd.calendapp.api.user+json',
			}
					statusCode: {
    		404: function() {window.alert("Usuario no encontrado");},
			500: function() {window.alert("Error interno del servidor, porfavor asegurate que el usuario no está ya dentro del grupo");}
    	}
		}).done(function(data,status,jqxhr)
		{
		
			//console.log("Post Login El nombre del usuario es " + data.name + " con mail " + data.email);
			//console.log("USUSARIO INFO : " + data);
			
			inviteUser(data);
		});
		console.log("VALOR DE USUARIO EN EL RETURN DENTROD E LA FUNCION " +usuario);
}

function getGrupo(groupid) {
	var url = API_BASE_URL + '/groups/' +groupid;
	$("#get_repo_result").text('');

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			Accept : 'application/vnd.calendapp.api.group+json',
		}
	}).done(function(data, status, jqxhr) {

				var grupo = data;
					$
				console.log(grupo);
				$("#get_repo_result").text('');
				
				$('<h3 class="mb"><b class="fa fa-angle-right"></b>' +grupo.name+ '</h3>').appendTo($('#group_name'));
				$('<h6 class="mb"><b class="fa fa-angle-right"></b>' +grupo.description+ '</h6>').appendTo($('#group_desc'));

			}).fail(function() {
				$('<div class="alert alert-danger"> <strong>Oh!</strong> No se ha encontrado el grupo </div>').appendTo($("#get_repo_result"));
	});

}

function getGrupoMod(groupid) {
	var url = API_BASE_URL + '/groups/' +groupid;
	$("#get_repo_result").text('');

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			Accept : 'application/vnd.calendapp.api.group+json',
			"Content-Type" : 'application/vnd.calendapp.api.group+json'
		}
	}).done(function(data, status, jqxhr) {
			changeName(data);
			}).fail(function() {
				$('<div class="alert alert-danger"> <strong>Oh!</strong> No se ha encontrado el grupo </div>').appendTo($("#get_repo_result"));
	});

}

function getGrupoMod2(groupid) {
	
	var url = API_BASE_URL + '/groups/' +groupid;
	$("#get_repo_result").text('');
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
			headers : {
			Accept : 'application/vnd.calendapp.api.group+json',
			"Content-Type" : 'application/vnd.calendapp.api.group+json'
		}
	}).done(function(data, status, jqxhr) {
			changeDesc(data);
			}).fail(function() {
				$('<div class="alert alert-danger"> <strong>Oh!</strong> No se ha encontrado el grupo </div>').appendTo($("#get_repo_result"));
	});
}

$("#button_invite").click(function(e){
	e.preventDefault();
	var usuario_invite = new Object();
	usuario_invite.name = ($("#textbox_invite").val());
		if ($("#textbox_invite").val()!= ''){
				var usuario = getUserIndex(usuario_invite.name);
				console.log("RECOGIENDO EL RETURN DE LA FUNCION" + usuario);
		}else{
			window.alert("NO dejes el nombre en blanco");
		}
		
});


function inviteUser(name){
	var z = getCookie("groupid");
	var url = API_BASE_URL + '/groups/'+z+'/accepted';
	var invited = new Object();
	invited.userid = name.userid;
	invited.username = name.username;
	invited.name = name.name;
	var data = JSON.stringify(invited);
	console.log("JSON QUE SE ENVIA A LA API " + data);
	console.log("Añadir usuario URL : " +url);
		$.ajax({
		url: url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
		headers : {
		Accept : 'application/vnd.calendapp.api.user+json',
		"Content-Type" : 'application/vnd.calendapp.api.user+json',
		},
				statusCode: {
    		404: function() {window.alert("Usuario no encontrado");}
    	}
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Amigo invitado</div>').appendTo($("#listar_grupos_id"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#listar_grupos_id"));
	});
}

$("#button_mod_nombre").click(function(e){
	e.preventDefault();
	var group_name = $("#asd").val();
	if (group_name == '')
	{
		window.alert("No puede estar en blanco este campo");
	}
	else {
		getGrupoMod(1);
	}
});

$("#button_mod_desc").click(function(e){
	e.preventDefault();
	var group_name = $("#as2d").val();
	if (group_name == '')
	{
		window.alert("No puede estar en blanco este campo");
	}
	else {
		getGrupoMod2(1);
	}
	
});


function changeName(asd){
	var url = API_BASE_URL +'/groups/'+ asd.groupid;
	asd.name = $("#asd").val();
	console.log(asd);
	var data = asd;
		var data = JSON.stringify(asd);
	console.log("____________________________");
	console.log(data);
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : data,
		headers : {
		Accept : 'application/vnd.calendapp.api.group+json',
		"Content-Type" : 'application/vnd.calendapp.api.group+json'
		}
	}).done(function(data, status, jqxhr) {
			window.alert("Nombre cambiado con éxito");
  	}).fail(function() {
			window.alert("Error al cambiar nombre");
	});	
}

function changeDesc(asd){
	var url = API_BASE_URL +'/groups/'+ asd.groupid;
	asd.description = $("#as2d").val();
	console.log(asd);
	var data = JSON.stringify(asd);
	console.log("____________________________");
	console.log(data);
	$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : data,
		headers : {
		Accept : 'application/vnd.calendapp.api.group+json',
		"Content-Type" : 'application/vnd.calendapp.api.group+json'
		}
	}).done(function(data, status, jqxhr) {
			window.alert("Descripción cambiada con éxito");
  	}).fail(function() {
			window.alert("Error al cambiar descripción");
	});	
}
