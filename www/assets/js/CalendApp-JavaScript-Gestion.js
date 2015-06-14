var API_BASE_URL = "http://localhost:8080/calendapp-api";
var USERNAME = getCookie("usuario");
var PASSWORD = getCookie("password");

$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});

//BOTÓN//

$("#button_cambiar_pass").click(function(e) {
	e.preventDefault();
	if($("#pass1").val() == '' || $("#pass2").val() == '')
	{
		window.alert("La contraseña no puede estar en blanco");
	}else if($("#pass1").val().localeCompare($("#pass2").val()) != 0)
	{
		window.alert("Las contraseñas no coinciden")
	}
	else{
		var user = new Object();
		user.userid = getCookie("userid");
		user.username=getCookie("username");
		user.userpass = ($("#pass1").val());
		user.name = getCookie("name");
		user.age = getCookie("userage");
		user.email = getCookie("mail")
		cambiarContra(user)
	}

});

$("#button_cambiar_mail").click(function(e){
	e.preventDefault();
	if($("#mail").val() == '')
	{
		window.alert("Introduce el correo");
	}
	else{
		var user = new Object();
		user.userid = getCookie("userid");
		user.username=getCookie("username");
		user.userpass = (getCookie("password")); 
		user.name = getCookie("username");
		user.age = getCookie("userage");
		user.email = $("#mail").val();
		cambiarMail(user);
		getUserIndex(user);
	}
});

function cambiarMail(user)
{
	var url = API_BASE_URL + '/users/' +getCookie("userid");
	var data = JSON.stringify(user);
	console.log("URL DE CAMBIAR MAIL : " + url);
	console.log(user.email);
		$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : data,
		contentType : 'application/vnd.calendapp.api.user+json',
	}).done(function(data, status, jqxhr) {
			window.alert("Mail camabiada correctamente");
			getUserIndex(data.username);
  	}).fail(function() {
		window.alert("Error al cambiar la mail");
	});	
}
function cambiarContra(abc)
{
	var url = API_BASE_URL + '/users/' +getCookie("userid");
	var data = JSON.stringify(abc);
	console.log(abc);
		$.ajax({
		url : url,
		type : 'PUT',
		crossDomain : true,
		dataType : 'json',
		data : data,
		contentType : 'application/vnd.calendapp.api.user+json',
	}).done(function(data, status, jqxhr) {
			window.alert("Contraseña camabiada correctamente");
			setCookie("password", ($("#pass1").val()));
			getUserIndex(data.username);
			location.reload();
  	}).fail(function() {
		window.alert("Error al cambiar la contraseña");
	});
	
}
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


//Cargar DOC
$(document).ready(function(){
		var x = getCookie("usuario");
	console.log(x);
	getUserIndex(x);
	var usernombre = getCookie("name");
	var usermail = getCookie("mail");
	var usernamez =getCookie("username");
	var useredad = getCookie("userage");
	var f = getCookie("mail");
	console.log("Edad : " +useredad);
	$(' <label class="col-sm-10">Nombre Completo :</label><strong>' + usernombre + '<strong>').appendTo($('#name_full'));	
    $(' <label class="col-sm-10">Nombre Usuario :</label><strong>' + usernamez + '<strong>').appendTo($('#name_user'));	
	$(' <label class="col-sm-10">Edad :</label><strong>' + useredad + '<strong>').appendTo($('#age'));	
	$(' <label class="col-sm-10">E-mail :</label><strong>' + usermail + '<strong>').appendTo($('#email'));	

});


function getUserIndex(username){
		$("#user_name").text('');
		$('<h5 class="centered" id="user_name"> Bienvenido, '+ username+'</h5>').appendTo($('#user_name'));
		var url = API_BASE_URL + '/users/' + username;
		console.log("URL DE GETUSERINDEX : " +url);
		$.ajax({
			url : url,
			type : 'GET',
			crossDomain : true,
			dataType : 'json',
		}).done(function(data,status,jqxhr)
		{
			setCookie("username", data.username);
			setCookie("mail", data.email);
			setCookie("userid", data.userid);
			setCookie("userage", data.age);
			setCookie("name", data.name);	
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

