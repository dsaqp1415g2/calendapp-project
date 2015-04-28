var API_BASE_URL = "https://api.github.com";
var USERNAME = "jordieetac";
var PASSWORD = "jordi21";

$.ajaxSetup({
    headers: { 'Authorization': "Basic "+ btoa(USERNAME+':'+PASSWORD) }
});


$("#boton_login").click(function(e) {
	e.preventDefault();

	if($('#user').val() == "" || $('#password').val()== ""){
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

				var repo = data;
		$("#login_result").text("Logeado");
		window.location= "login.html";
		
		
		}).fail(function() {
		$("#login_result").text("Este usuario no existe");
	});
}


$("#boton_mostrar_repos").click(function(e) {
	e.preventDefault();
	getrepos();
});

$("#boton_mostrar_repos_id").click(function(e) {
	e.preventDefault();
	var id_repo = $("#id_repo").val();
	getrepoName(id_repo);
});


$("#boton_crear_repo").click(function(e) {
	e.preventDefault();
	
    	var newRepo = new Object();
		newRepo.description = $("#description_repo").val();
		newRepo.name = $("#file_name_repo").val();
		newRepo.homepage = "https://github.com";
		newRepo.private = false;
		newRepo.has_issues = true;
		newRepo.has_wiki = true;
		newRepo.has_downloads = true;
	

	createrepo(newRepo);

});

$("#boton_borrar_repo_id").click(function(e) {
	e.preventDefault();
	$('#borrar_repo').text('');
	var id_repo = $("#id_repo").val();
	deleterepo(id_repo);

});



$("#boton_editar_repo").click(function(e) {
	e.preventDefault();
	$('#update_result').text('');

	var updateRepo = new Object();
	updateRepo.name = $("#nombre_fichero_nuevo_git").val();
	updateRepo.description = $("#description_repo").val();
	updateRepo.id = $('#id_repo').val();
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


function getrepos(){
var url = API_BASE_URL+'/users/'+USERNAME+'/repos';

	$("#resultadorepos").text('');
	
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				var repos = data;
				
				$.each(repos, function(i, v) {
					var repo = v;

					$('<br>    </br>').appendTo($('#resultadorepos'));
					$('<strong> ID: </strong> ' + repo.id + '<br>').appendTo($('#resultadorepos'));
					$('<strong> URL: </strong> ' + repo.html_url + '<br>').appendTo($('#resultadorepos'));
					$('<strong> Description: </strong> ' + repo.description + '<br>').appendTo($('#resultadorepos'));
					$('<br>    </br>').appendTo($('#resultadorepos'));
				});
				
	
	}).fail(function() {
		$("#resultadorepos").text("No repositories.");
	});



}


function getrepoName(id) {
	var url = API_BASE_URL + '/repos/' + USERNAME + '/' + id;
	//$("#resultadorepo").text('');
	
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				var repos = data;
				


					$('<br>    </br>').appendTo($('#resultadorepo'));
					$('<strong> ID: </strong> ' + repos.id + '<br>').appendTo($('#resultadorepo'));
					$('<strong> URL: </strong> ' + repos.html_url + '<br>').appendTo($('#resultadorepo'));
					$('<strong> Description: </strong> ' + repos.description + '<br>').appendTo($('#resultadorepo'));
					$('<br>    </br>').appendTo($('#resultadorepo'));
			
				

	}).fail(function() {
		$("#resultadorepo").text("No repositories.");
	});

}



function createrepo(repo){
	
	var url = API_BASE_URL + '/user/repos';
	var data = JSON.stringify(repo);
	$("#repos_result").text('');

	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> repo Created</div>').appendTo($("#repos_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#repos_result"));
	});

}




function editarrepo(repo, id){
	var url = API_BASE_URL + '/repos/' + id;
	var data = JSON.stringify(repo);

	$("#update_result").text('');

	$.ajax({
		url : url,
		type : 'PATCH',
		crossDomain : true,
		dataType : 'json',
		data : data,
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> repo Updated</div>').appendTo($("#update_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#update_result"));
	});

}



function deleterepo(id){

	var url = API_BASE_URL + '/repos/' + USERNAME + '/' + id; 
	$("#borrar_repo").text('');

	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Repository Deleted</div>').appendTo($("#borrar_repo"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#borrar_repo"));
	});

	
}




function repoCollection(repoCollection){
	this.repos = repoCollection;

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
		$.each(this.repos, function(i, v) {
			var repos = v;
			html = html.concat('<br><strong> Description: ' + repos.description + '</strong><br>');
			html = html.concat('<br><strong> ID: ' + repos.id + '</strong><br>');
		});
		
		html = html.concat(' <br> ');

                var prev = this.getLink('prev');
		if (prev.length == 1) {
			html = html.concat(' <a onClick="getreposURL(\'' + prev[0].href + '\');" class="btn btn-info" type="button">Anterior</a> ');
		}
                var next = this.getLink('next');
		if (next.length == 1) {
			html = html.concat(' <a onClick="getreposURL(\'' + next[0].href + '\');" class="btn btn-info" type="button">Siguiente</a> ');
		}

 		return html;	
	}

}



function getreposURL(url) {
	$("#resultado_paginable_repo").text('');

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
        	var response = data;
		var repoCollection = new repoCollection(response);
                var linkHeader = jqxhr.getResponseHeader('Link');
                repoCollection.buildLinks(linkHeader);

		var html = repoCollection.toHTML();
		$("#resultado_paginable_repo").html(html);

	}).fail(function(jqXHR, textStatus) {
		console.log(textStatus);
	});

}
