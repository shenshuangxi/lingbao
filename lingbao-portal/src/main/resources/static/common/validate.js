$(document).ready(function() {
	validate();
});


function validate() {
	$.ajax({
		type: "get",
		url: "/api/v1/validate",
		beforeSend: function(XMLHttpRequest) {
			XMLHttpRequest.setRequestHeader("Authentication", localStorage.getItem("Authentication"));
		},
		success: function(datas) {
			console.log(window.location);
		},
		error: function(e) {
			console.log(e);
			window.location.href='/login.html?'+window.location.href;
		}
	});
}