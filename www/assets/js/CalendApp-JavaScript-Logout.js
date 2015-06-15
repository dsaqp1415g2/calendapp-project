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

$("#logout_button").click(function(e){
	e.preventDefault();
	window.alert("Nos vemos " + getCookie("username"));
	setCookie('eventid', '', -1);
	setCookie('groupid', '', -1);
	setCookie('groupstatus', '', -1);
	setCookie('isprivate', '', -1);
	setCookie('mail', '', -1);
	setCookie('name', '', -1);
	setCookie('password', '', -1);
	setCookie('userage', '', -1);
	setCookie('userid', '', -1);
	setCookie('username', '', -1);
	setCookie('usuario', '', -1);
	window.location = "/Login.html";
	
});
