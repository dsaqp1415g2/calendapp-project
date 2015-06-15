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
//GET EVENTOS
$("#button_get_eventos").click(function(e){
	e.preventDefault();
	//console.log("Entramos función listar eventos, buscamos por grupo default = 2");
	var id = getCookie("eventid");
	getEvents(id);
	
});
$("#button_delete_event").click(function(e){
	e.preventDefault();
	var event = new Object();
	event.eventid = $("#event_name").val();
	deleteEvent(event);
	
});
/*$("#button_create_event").click(function(e){
	e.preventDefault();
	var event = new Object();
	var z = document.getElementById("opts");
	var opt = z.options[z.selectedIndex].value;
	event.name = $("#group_name").val();
	//var initialTS = ($("#inicio").val()).split("-");
	//console.log("FECHA INitiAL " + initialTS);
	//var initTS = initialTS[1]+","+initialTS[0]+","+initialTS[2];
	//var xyz = new Date(initTS).getTime();
	
	event.dateInitial = 1433669675000;
	//var finalTS = ($("#final").val()).split("-");
	//var finTS = finalTS[1]+","+finalTS[0]+","+finalTS[2];
	event.dateFinish = 1433669675000;
	event.groupid = opt;
	event.userid = getCookie("username");
	createEvent(event);
	//console.log("Entramos función listar eventos, buscamos por grupo default = 2");

	//FALTA REStRIngIR INPUT
});
*/
$("#button_add_comment").click(function(e){
	e.preventDefault();
	console.log()
	if ($("#comment_content").val() == ''){
		window.alert("El comentario no puede estar vacío");
	}else{
		var creator = getCookie("usuario");
		var text = ($("#comment_content").val());
		console.log("TEXTO: " +text);
		addComent(text, creator);
	}
});

//Cargar DOC
$(document).ready(function(){
		var x = getCookie("usuario");
	//console.log(x);
	getUserIndex(x);
	var z = getCookie("eventid");
	var y = getCookie("isprivate");
	getEvent(z);
	getJoin(z);
	loadComments(z);
	if (y==0)
	{
		  console.log("Detectamos evento privado");
		$("#caja_comentarios").hide();
		$('<textarea type="text" class="form-control" id="comment_content" rows="3" disabled></textarea> disabled').appendTo($("#event_comment"));
		$('<button class="btn btn-info" type="button" id="button_add_comment" disabled>Enviar comentario</button>').appendTo($("#button_add_comment"));
		
		console.log("AAAAAAA");
	}
	else{
		console.log("detectamos evento publico");
		$('<textarea type="text" class="form-control" id="comment_content" maxlengt="170" placeholder="Introduce to comentario con un máximo de 170 carácteres" "></textarea>').appendTo($("#event_comment"));
		$('<button class="btn btn-info" type="button" id="button_add_comment">Enviar comentario</button>').appendTo($("#button_add_comment"));
	}
		
});
function TStoDate(abc)
{
	var date = new Date(abc);
	var day = date.getDate();
	var month = date.getMonth(); 
	var year = date.getFullYear();
	var completeDate =  day+'/'+month+'/'+year;
	return completeDate;
}

function loadComments(eventID){
	eventID = getCookie("eventid");
	var url = API_BASE_URL +'/comments/' + eventID;
	console.log("URL DE EVENTOS " +url);
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers:{
		Accept : 'application/vnd.calendapp.api.comment.collection+json',
		},
		statusCode :{
			400 : function() {window.alert("Este evento no tiene comentarios");}
		}
	}).done(function(data, status, jqxhr) {
						$.each(data, function(i, v) {
							var msg = v;
								$.each(v, function(z,k){
									var mens = k;
										if(typeof k.content !== 'undefined'){
						$('<div class="alert alert-info"> <strong>'+k.content+'. <br></strong> Por '+k.username+' el  '+TStoDate(k.creationTimestamp)+' </div>').appendTo($("#cargar_comentarios "));
										}
						});
						//$('<div class="alert alert-danger"> <strong>Oh!</strong> Grupo no encontrado </div>').appendTo($("#cargar_comentarios "));
						});
			}).fail(function() {
				$('<div class="alert alert-danger"> <strong>Oh!</strong> Grupo no encontrado </div>').appendTo($("#get_repo_result"));
	});

}
	
	

function createEvent(event){
	var url = API_BASE_URL + '/events';
	var data = JSON.stringify(event);
	$("#get_repo_result").text('');
	console.log(event);
	$.ajax({
		url : url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
		headers : {
		Accept : 'application/vnd.calendapp.api.event+json',
		"Content-Type" : 'application/vnd.calendapp.api.event+json'
		}
	}).done(function(data, status, jqxhr) {
		$('<div class="alert alert-success"> <strong>Ok!</strong> Event Created</div>').appendTo($("#create_result"));				
  	}).fail(function() {
		$('<div class="alert alert-danger"> <strong>Oh!</strong> Error </div>').appendTo($("#create_result"));
	});
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
function getEvent(eventid){
	var url = API_BASE_URL + '/events/' + eventid;
	console.log("La url getEvent: "+url);
	$("repos_result").text('');
		$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		headers :  {
		Accept : 'application/vnd.calendapp.api.event+json',
		}
	}).done(function(data, status, jqxhr) {
				var repos = data;

					if (typeof repos.name !== 'undefined'){
						$('<h3 class="mb"><b class="fa fa-angle-right"></b>' +repos.name+ '</h3><br><b>Duración : </b>' +TStoDate(repos.dateInitial)+'-'+TStoDate(repos.dateFinish)+'<br><b>Último cambio : </b>' +TStoDate(repos.lastModified)+ '<br><b>Ultima modificación : </b>' +TStoDate(repos.lastModified)+ '<br>').appendTo($('#group_name'));
					}

	}).fail(function() {
					window.alert("Se ha producido un error al cargar el evento");
	});
	
}

function getJoin(eventid){
	var url = API_BASE_URL + '/events/state/' + eventid + '/join';
	var lista = [];
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		dataType : 'json',
		Accept : 'application/vnd.calendapp.api.event.collection+json',
		statusCode :{
			403 : function() {window.alert("Este evento es privado");}
		}
	}).done(function(data, status, jqxhr) {
				var repos = data;
				$.each(repos, function(i, v) {
					var repo = v;
					$.each(repo, function(i, z) {
					console.log(z);
					if (typeof z.name !== 'undefined'){
						lista.push(z.name);
					}

					});
				});
				console.log(lista);
			$('<b>Asistentes : </b>' +lista+ '<br>').appendTo($('#group_name'));

				

	}).fail(function() {
		window.alert("Error al cargar lista de personas que atenderán al evento");
	});
	
	
}

function addComent(text, creator){
	var url = API_BASE_URL +'/comments';
	var comment = new Object();
	comment.content = text;
	comment.eventid = getCookie("eventid");
	comment.username = creator;
	var data = JSON.stringify(comment);
	console.log(data);
	$.ajax({
		url: url,
		type : 'POST',
		crossDomain : true,
		dataType : 'json',
		data : data,
		headers : {
		Accept : 'application/vnd.calendapp.api.comment+json'
		"Content-Type" : 'application/vnd.calendapp.api.comment+json',
		}
	}).done(function(data, status, jqxhr) {
		window.alert("Comment creado");
  	}).fail(function() {
		window.alert("Error al añadir comentario");
	});
	
	
}