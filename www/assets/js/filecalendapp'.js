var API_BASE_URL = "https://api.github.com";
var USERNAME = "ajarac";
var PASSWORD = "Ibertime1";

$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});




//Funcion que se carga cuando se abre el documento
$(document).ready(function(){
	var url = API_BASE_URL + '/gists?per_page=3';
	getGistsURL(url);
});



//Login

$("#boton_login").click(function(e) {
	e.preventDefault();

	if($('#user').val() == "" || $('#password').val()== ""){
		console.log("hola");
		$('<div class="alert alert-danger"> <strong>Error!</strong> Escribe usuario y contraseña </div>').appendTo($("#login_result"));
	}else{
	USERNAME = $("#user").val();
	PASSWORD = $("#password").val();
	localStorage.nombre = document.getElementById("user").value;
	localStorage.password = document.getElementById("password").value;
	
	getUser();
	}
	
});

function getUser() {	

	var url = API_URL + '/users/' + USERNAME;
	$("#login_result").text('');
	
	$.ajax({
		headers : {
			'Authorization' : "Basic " + btoa(USERNAME + ':' + PASSWORD)
		},
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(
		function(data, status, jqxhr) {

				var gist = data;
		$("#login_result").text("Logeado");
		window.location= "login.html";
		
		
		}).fail(function() {
		$("#login_result").text("Este usuario no existe");
	});
}
$('login.html').ready(function(){
		USERNAME= localStorage.nombre;
		$('<strong> Bienvenido, </strong> ' + USERNAME + '<br>').appendTo($('#login_result'));

});


//Mostrar todos los gists
$("#boton_mostrar_gists").click(function(e) {
	e.preventDefault();
	getGists();
});

//Mostrar gist por id
$("#boton_mostrar_gists_id").click(function(e) {
	e.preventDefault();
	var id_gist = $("#id_gist").val();
	getGistID(id_gist);
});

//Crear gist
$("#boton_crear_gist").click(function(e) {
	e.preventDefault();
	$("#gists_result").text('');
	
    	var newGist;
	newGist = { 
		"description" : $('#description_gist').val(),
		"public" : true,
		"files" : {
			"file1" :  {
				"content" : $('#comentario_gist').val()
				}
			}
		}
	

	createGist(newGist);

});

//Editar gist
$("#boton_borrar_gists_id").click(function(e) {
	e.preventDefault();
	$('#borrar_gist').text('');

	var id_gist = $("#id_gist").val();
	deleteGist(id_gist);
			


});

//Editar Gist
/*
$("#boton_editar_gist").click(function(e) {
	e.preventDefault();
	$('#update_result').text('');

	var updateGist;
	updateGist = {
		"description" : $('#description_gist').val(),
		"files" : {
			"file1" : {
				"content" : $('#comentario_gist').val()
				},
			"file1" : {
				"filename" : $('#nombre_fichero_nuevo_gist').val(),
				"content" : $('#comentario_gist').val()
				},
			"new file" : {
				"content" : $('#comentario_gist').val()
				},
			"delete_this_file.txt" : null
			}
	}

	var id_gist = $('#id_gist').val();
	editarGist(updateGist, id_gist);			
			


});


*/

$("#boton_editar_gist").click(function(e) {
	e.preventDefault();
	$('#update_result').text('');

	
   	var updateGist = new Object();
	updateGist.description = $("#description_gist").val()
	updateGist.content = $('#comentario_gist').val()
	
	
	var id_gist = $('#id_gist').val();
	editarGist(updateGist, id_gist);

});


//Mostrar todos los gists
function getGists(){
var url = API_BASE_URL + '/gists';
	$("#resultadoGists").text('');
	
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				var gists = data;
				var collect = new gistCollection(gists);
				var linkHeader = jqxhr.getResponseHeader('Link');
				var html = collect.toHTML();
				$("resultadoGists").html(html);
				
				

	}).fail(function() {
		$("#resultadoGists").text("No repositories.");
	});



}

//Mostrar gist por id
function getGistID(id) {
	var url = API_BASE_URL + '/gists/'+ id;
	$("#resultadoGist").text('');
	
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				var gists = data;
				


					$('<br>    </br>').appendTo($('#resultadoGist'));
					$('<strong> ID: </strong> ' + gists.id + '<br>').appendTo($('#resultadoGist'));
					$('<strong> URL: </strong> ' + gists.html_url + '<br>').appendTo($('#resultadoGist'));
					$('<strong> Description: </strong> ' + gists.description + '<br>').appendTo($('#resultadoGist'));
					$('<br>    </br>').appendTo($('#resultadoGist'));
			
				

	}).fail(function() {
		$("#resultadoGist").text("No repositories.");
	});

}


//Crear gist
function createGist(gist){

	var url = API_BASE_URL + '/gists';
	var data = JSON.stringify(gist);

	$("#gists_result").text('');

	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Gist Created</div>').appendTo($("#gists_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#gists_result"));
	});

}


//Editar gist

function editarGist(gist, id){
	var url = API_BASE_URL + '/gists/' + id;
	var data = JSON.stringify(gist);

	$("#update_result").text('');

	$.ajax({
		url : url,
		type : 'PATCH',
		crossDomain : true,
		dataType : 'json',
		data : data,
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Gist Updated</div>').appendTo($("#update_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#update_result"));
	});

}

//Borrar gist

function deleteGist(id){

	var url = API_BASE_URL + '/gists/' + id;
	$("#borrar_gist").text('');

	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Gist Deleted</div>').appendTo($("#borrar_gist"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#borrar_gist"));
	});

	
}




function GistCollection(gistCollection){
	this.gists = gistCollection;

	var instance = this;

	this.buildLinks = function(header){
		if (header != null ) {
			this.links = weblinking.parseHeader(header);
		} else {
			this.links = weblinking.parseHeader('');
		}
	}

	this.getLink = function(rel){
                return this.links.getLinkValuesByRel(rel);
	}

	this.toHTML = function(){
		var html = '';
		$.each(this.gists, function(i, v) {
			var gists = v;
			html = html.concat('<br><strong> Description: ' + gists.description + '</strong><br>');
			html = html.concat('<br><strong> ID: ' + gists.id + '</strong><br>');
		});
		
		html = html.concat(' <br> ');

                var prev = this.getLink('prev');
		if (prev.length == 1) {
			html = html.concat(' <a onClick="getGistsURL(\'' + prev[0].href + '\');" class="btn btn-info" type="button">Anterior</a> ');
		}
                var next = this.getLink('next');
		if (next.length == 1) {
			html = html.concat(' <a onClick="getGistsURL(\'' + next[0].href + '\');" class="btn btn-info" type="button">Siguiente</a> ');
		}

 		return html;	
	}

}



function getGistsURL(url) {
	$("#resultado_paginable_gist").text('');

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
        	var response = data;
		var gistCollection = new GistCollection(response);
                var linkHeader = jqxhr.getResponseHeader('Link');
                gistCollection.buildLinks(linkHeader);

		var html = gistCollection.toHTML();
		$("#resultado_paginable_gist").html(html);

	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus);
	});

}


















































