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
		headers :
		{
			Accept : 'application/vnd.calendapp.api.user+json',
			"Content-Type" : 'application/vnd.calendapp.api.user+json'
		},
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
				headers :
		{
			Accept : 'application/vnd.calendapp.api.user+json',
			"Content-Type" : 'application/vnd.calendapp.api.user+json'
		}
	}).done(function(data, status, jqxhr) {
			window.alert("Bienvenido a CalendApp " +userdata.username);				
  	}).fail(function() {
			window.alert("Problema al registrarse");
	});
	
}




