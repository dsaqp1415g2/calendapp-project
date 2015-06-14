var API_BASE_URL = "http://147.83.7.158:8080/calendapp-api";
var USERNAME = getCookie("usuario");
var PASSWORD = getCookie("password");
//147.83.7.158:8080/calendapp-api/
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
			statusCode: {
				    		404: function() {window.alert("Usuario no encontrado");},
			},
			headers: {
			Accept : 'application/vnd.calendapp.api.user+json',
			"Content-Type" : 'application/vnd.calendapp.api.user+json'
		}
	}).done(function(result, status, jqxhr) {
		var logCheck = result;		
		if (logCheck.loginSuccessful) {	
			console.log("Ususario " + logCheck.username  + " autenticado");
			getUserIndex(logCheck.username);
			var url2 = 'http://localhost/Index.html';
			$(location).attr('href',url2);
		}else if(!logCheck.loginSuccessful){
			window.alert("Error, revisa tus datos");
		}
});
});
//REGISTRAR
$("#submit_register").click(function(e){
	e.preventDefault();
	if (($("#name_reg").val()) == '' || ($("#username_reg").val()) == '' | ($("#userpass_reg").val()) == '' || ($("#email_reg").val()) == '' || ($("#age_reg").val()) == ''){
		window.alert("Debes rellenar todos los campos");
	}
	else{
		var newuser = new Object();
		newuser.username = ($("#username_reg").val());
		newuser.userpass = ($("#userpass_reg").val());
		newuser.name = ($("#name_reg").val());
		newuser.age = ($("#age_reg").val());
		newuser.email = ($("#email_reg").val());
		registerUser(newuser);
	}
});
function registerUser(userdata){
	var url = API_BASE_URL + '/users/';
	console.log("URL usada para registrar es " + url);
	data=JSON.stringify(userdata);
	console.log(data);
		$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
		contentType : 'application/vnd.calendapp.api.user+json',
		Accept : 'application/vnd.calendapp.api.user+json'
	}).done(function(data, status, jqxhr) {
			window.alert("Bienvenido a CalendApp " +userdata.username);				
  	}).fail(function() {
			window.alert("Problema al registrarse");
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




