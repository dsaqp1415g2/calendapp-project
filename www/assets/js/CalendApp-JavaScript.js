var API_BASE_URL = "http://localhost:8080/calendapp-api";
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
			var url2 = 'http://localhost/index.html';
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
});
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

function loginSuccessfull()
{
	
	
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
		$("#repos_result").text("No repositories.");
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

function getRepoToEdit(repository_name) {
	var url = API_BASE_URL + '/repos/' + USERNAME + '/' + repository_name;
	$("#update_result").text('');

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
		
				var repo = data;
				

				$("#update_result").text('');
				$("#repository_name_to_edit").val(repo.name);
				$("#description_to_edit").val(repo.description);

	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Repository not found </div>').appendTo($("#update_result"));
	});

}

function updateRepo(repository) {
	var url = API_BASE_URL + '/repos/' + USERNAME + '/' + repository.name;
	var data = JSON.stringify(repository);

	$("#update_result").text('');

	$.ajax({
		url : url,
		type : 'PATCH',
		crossDomain : true,
		dataType : 'json',
		data : data,
		statusCode: {
    		404: function() {$('<div class="alert alert-danger"> <strong>Oh!</strong> Page not found </div>').appendTo($("#update_result"));}
    	}
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Repository Updated</div>').appendTo($("#update_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#update_result"));
	});


}


/*function createGroup(group) {
	var url = API_BASE_URL + '/groups';
	var data = JSON.stringify(group);
	console.log(data);
	$("#create_result").text('');

	$.ajax({
		headers: { Accept: 'application/json',
		ContentType: 'application/json'
					},
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
		contentType : 'application/json',
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Repository Created</div>').appendTo($("#create_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#create_result"));
	});

}
*/

