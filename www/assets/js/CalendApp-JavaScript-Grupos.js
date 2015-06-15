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

//Cargar DOC
$(document).ready(function(){
		var x = getCookie("usuario");
		getGroups(getCookie("userid"));
});


$("#button_get_groups").click(function(e) {
	e.preventDefault();
	getGroups(getCookie("userid"));
});

$("#button_get_group").click(function(e) {
	e.preventDefault();
	getGrupo($("#nombre_grupo").val());
	console.log($("#nombre_grupo").val())
});

$("#button_delete_group").click(function(e){
	e.preventDefault();
	if ($("#group_name").val()==''){
		window.alert("No puedes dejar el campo en blanco");
	}
	else{
	var group =  $("#group_name").val();
	deleteGroup(group);
	}
	
})


function deleteGroup(abc){
	console.log(abc);
	var url = API_BASE_URL + '/groups/' + abc;
	var grupo = getGrupo($("#group_name").val());
	
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		dataType : 'json',
	}).done(function(data, status, jqxhr) {
				window.alert("El grupo ha sido borrado")
	}).fail(function() {
				window.alert("No se ha podido borrar el grupo");
	});

	
}

function test() {
var radios = document.getElementsByName("qasd");
var found = 1;
for (var i = 0; i < radios.length; i++) {       
    if (radios[i].checked) {
        return radios[i].value;
        found = 0;
        break;
    }
}
   if(found == 1)
   {
     alert("Please Select Radio");
   }    
}

$("#button_create_group").click(function(e) {
	e.preventDefault();
	var result_shared = test();
    var group = new Object();
	group.name = $("#group_name").val();
	group.admin= getCookie("usuario");
	group.description = $("#group_description").val();
	$('ASDASD').appendTo($("#create_result"));
	if(result_shared == 1)
	{
		console.log("este grupo deberia ser privado");
	group.shared = true;
	}else if(result_shared == 0){
		console.log("este grupo deberia ser publico");
		group.shared = false;
	}
	createGroup(group);
});

function getGroups(abc) {
	//console.log("variable recogida " + abc);
	var grupos =  new Array();
	var url = API_BASE_URL + '/groups/user/' + abc;
	$("#listar_grupos").text('');
						var cont = 0;
	//console.log(url);
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
				Accept : 'application/vnd.calendapp.api.group.collection+json',
		}
	}).done(function(data, status, jqxhr) {
						var groups = data;
				$.each(groups, function(i, v) {
					var group = v;
						$.each(group, function(i, z){
					var asd = z;
					//console.log(repo.name);
					
						//console.log(z);
						console.log("Objecto " + cont + " con nombre :" +z.name);
						if(typeof asd.name !== "undefined"){
							console.log("ENTRA");
							console.log(z);
							if (z.shared == 1){
					$('<a onClick="idgrup('+z.groupid+','+z.shared+')"><div class="form-panel"  id=""> <br><strong> Name: ' + z.name + '</strong><br><strong> Tipo : </strong> Compartido<br><strong> ID : </strong>'+z.groupid+'<br></div></a>').appendTo($('#listar_grupos'));
							}else{
					$('<a onClick="idgrup('+z.groupid+','+z.shared+')"><div class="form-panel"  id=""> <br><strong> Name: ' + z.name + '</strong><br><strong> Tipo : </strong> Privado<br><strong> ID : </strong>'+z.groupid+'<br></div></a>').appendTo($('#listar_grupos'));
							}
							
					grupos.push(z.groupid);
					console.log(grupos);
						}
			
					cont++;
						});
				});
				

	}).fail(function() {
		$("#listar_grupos").text("No repositories.");
	});

}

function idgrup(abc,def)
{
	setCookie("groupid", abc);
	setCookie("groupstatus", def);
	if($.cookie("groupid"))
	{
		$(location).attr('href', "/Detalle Grupo.html");
	}
	else{
		window.alert("Impossible obtener ID grupo");
	}
	
}

/*function getGrupo(groupid) {
	var url = API_BASE_URL + '/groups/' +groupid;
	$("#get_repo_result").text('');

	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			Accept : 'application/vnd.calendapp.api.group+json',
		},
	}).done(function(data, status, jqxhr) {
			$.each(data, function(i, v) {
				var grupo = v;
	
						$.each(v, function(z,k){
								var j = k;
								if(typeof k.name !== "undefined"){
							$("#get_repo_result").text('');
							$('<strong> Nombre: </strong> ' + k.name + '<br>').appendTo($('#get_repo_result'));
							$('<strong> Administrador: ' + k.admin + '<br>').appendTo($('#get_repo_result'));
							$('<strong> Descripcion: </strong> ' + k.description + '<br>').appendTo($('#get_repo_result'));
							$('<br><br>').appendTo($('get_repo_result'));
								}

						});
			});
			}).fail(function() {
				$('<div class="alert alert-danger"> <strong>Oh!</strong> Grupo no encontrado </div>').appendTo($("#get_repo_result"));
	});

}
*/

function getGrupoDel(groupid) {
	var url = API_BASE_URL +'/groups/'+groupid.name;
	$("#get_repo_result").text('');
	console.log("Buscamos el grupo con nombre " + groupid.name + "para borrar");
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers : {
			Accept : 'application/vnd.calendapp.api.group+json',
		}
	}).done(function(data, status, jqxhr) {
			$.each(data, function(i, v) {
				var grupo = v;
	
						$.each(v, function(z,k){
								var j = k;
								if(typeof k.name !== "undefined"){
										console.log("Ids de los grupos no undefineds " +k.id);
										if(k.name == groupid.name)
										{
											console.log("Todo el objeto" +k.id);
											console.log(k.name+"-"+groupid.name);
											deleteGroup(k.id);
												$('<div class="alert alert-danger"> <strong>Oh!</strong> Grupo borrado </div>').appendTo($("#get_repo_result"));
										}
								}
						});
			});
			}).fail(function() {
				$('<div class="alert alert-danger"> <strong>Oh!</strong> Grupo no encontrado </div>').appendTo($("#get_repo_result"));
	});

}
function createGroup(group) {
	var url = API_BASE_URL + '/groups';
	var data = JSON.stringify(group);
	console.log(data);
	$("#create_result").text('');

	$.ajax({
		url: url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
		headers : {
			Accept : 'application/vnd.calendapp.api.group+json',
			"Content-Type" : 'application/vnd.calendapp.api.group+json',
		}
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Grupo Creado</div>').appendTo($("#get_repo_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#get_repo_result"));
	});

}

