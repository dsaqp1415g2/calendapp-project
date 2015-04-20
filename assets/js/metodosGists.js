var API_BASE_URL = "https://api.github.com";
var USERNAME = "jordieetac";
var PASSWORD = "jordi21";

$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});


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


$("#boton_mostrar_gists").click(function(e) {
	e.preventDefault();
	getGists();
});

$("#boton_mostrar_gists_id").click(function(e) {
	e.preventDefault();
	var id_gist = $("#id_gist").val();
	getGistName(id_gist);
});


$("#boton_crear_gist").click(function(e) {
	e.preventDefault();
	
    	var newRepo = new Object();
		newRepo.description = $("#description_gist").val();
		newRepo.name = $("#file_name_gist").val();
		newRepo.homepage = "https://github.com";
		newRepo.private = false;
		newRepo.has_issues = true;
		newRepo.has_wiki = true;
		newRepo.has_downloads = true;
	

	createGist(newRepo);

});

$("#boton_borrar_repo_id").click(function(e) {
	e.preventDefault();
	$('#borrar_gist').text('');
	var id_gist = $("#id_gist").val();
	deleteGist(id_gist);

});



$("#boton_editar_gist").click(function(e) {
	e.preventDefault();
	$('#update_result').text('');

	var updateRepo = new Object();
	updateRepo.name = $("#nombre_fichero_nuevo_git").val();
	updateRepo.description = $("#description_gist").val();
	updateRepo.id = $('#id_gist').val();
	actualizarRepo(updateRepo);
			


});


function actualizarRepo(updateRepo)
{
	var url = API_BASE_URL + '/repos/' + USERNAME + '/' + updateRepo.name;
	var data = JSON.stringify(updateRepo);
	//$("#update_result").text(' ');
	$.ajax({
        headers : {
			'Authorization' : "Basic "
			+ btoa(USERNAME + ':' + PASSWORD)
						},
		url : url,
		type : 'PATCH',
		crossDomain : true,
		dataType : 'json',
		data : data,
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Repository Updated</div>').appendTo($("#update_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#update_result"));
	});
}


function getGists(){
var url = API_BASE_URL+'/users/'+USERNAME+'/repos';

	$("#resultadoGists").text('');
	
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				var gists = data;
				
				$.each(gists, function(i, v) {
					var gist = v;

					$('<br>    </br>').appendTo($('#resultadoGists'));
					$('<strong> ID: </strong> ' + gist.id + '<br>').appendTo($('#resultadoGists'));
					$('<strong> URL: </strong> ' + gist.html_url + '<br>').appendTo($('#resultadoGists'));
					$('<strong> Description: </strong> ' + gist.description + '<br>').appendTo($('#resultadoGists'));
					$('<br>    </br>').appendTo($('#resultadoGists'));
				});
				
	
	}).fail(function() {
		$("#resultadoGists").text("No repositories.");
	});



}


function getGistName(id) {
	var url = API_BASE_URL + '/repos/' + USERNAME + '/' + id;
	//$("#resultadoGist").text('');
	
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



function createGist(gist){
	
	var url = API_BASE_URL + '/user/repos';
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



function deleteGist(id){

	var url = API_BASE_URL + '/repos/' + USERNAME + '/' + id; 
	$("#borrar_gist").text('');

	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Repository Deleted</div>').appendTo($("#borrar_gist"));				
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
