function formatDateTime(inputTime) {  
    var date = new Date(inputTime);
    var y = date.getFullYear();  
    var m = date.getMonth() + 1;  
    m = m < 10 ? ('0' + m) : m;  
    var d = date.getDate();  
    d = d < 10 ? ('0' + d) : d;  
    var h = date.getHours();
    h = h < 10 ? ('0' + h) : h;
    var minute = date.getMinutes();
    var second = date.getSeconds();
    minute = minute < 10 ? ('0' + minute) : minute;  
    second = second < 10 ? ('0' + second) : second; 
    return y + '-' + m + '-' + d+' '+h+':'+minute+':'+second;  
};

function loadApp() {
	$.ajax({
		type: "get",
		url: "/api/v1/app",
		dataType:"json",
		beforeSend: function(XMLHttpRequest) {
			XMLHttpRequest.setRequestHeader("Authentication", localStorage.getItem("Authentication"));
		},
		success: function(datas) {
			console.log(datas);
			datas.forEach(function(currentValue, index, arr){
				var appComponent = "<div class='title bg-green' id='"+currentValue.appId+"'>"
				+"<span style='float: right; margin:4px;'>"
				+"	<i class='fa fa-pencil-square-o ' aria-hidden='true'  data-toggle='tooltip' data-placement='top' title='编辑' onclick='editAppProject("+JSON.stringify(currentValue)+")'></i>"
				+"	<i class='fa fa-times' aria-hidden='true' data-toggle='tooltip' data-placement='top' title='删除' onclick='deleteAppProject("+JSON.stringify(currentValue)+")'></i>"
				+"</span>"
				+"<span class='title-content' onclick='toApp("+JSON.stringify(currentValue)+")'>"
				+"<span data-toggle='tooltip' data-placement='top' title='"+JSON.stringify(currentValue)+"' ><h3>"+currentValue.name+"</h3></span>"
				+"</span>";
				
				$("#apps").append(appComponent);
			});
		},
		error: function(e) {
			console.log(e);
			$.lingbao_popup({
				content: e.responseText
			});
			if (e.status===401) {
				window.location.href='/login.html?'+window.location.href;
			} else {
				window.location.href='/index.html';
			}
		}
	});
}

function loadEnv() {
	$.ajax({
		type: "get",
		url: "/api/v1/env",
		dataType:"json",
		success: function(data) {
			console.log(data);
		},
		error: function(e) {
			console.log(e);
			$.lingbao_popup({
				content: e.responseText
			});
			if (e.status===401) {
				window.location.href='/login.html?'+window.location.href;
			} else {
				window.location.href='/index.html';
			}
		}
	});
}

function loadCluster(appId, envId) {
	$.ajax({
		type: "get",
		url: "/api/v1/app/"+appId+"/env/"+envId+"/cluster",
		dataType:"json",
		success: function(data) {
			console.log(data);
		}
	});
}


function loadFile(appId, envId, clusterId) {
	$.ajax({
		type: "get",
		url: "/api/v1/app/"+appId+"/env/"+envId+"/cluster/"+clusterId,
		dataType:"json",
		success: function(data) {
			console.log(data);
		}
	});
}

/*
 * 第一步 判断是否已存在text 如果不存在 则创建一个新的tab
 * 如果已有 则 重新打开 选中
 * 
 * */

function openAdminTab(text, url) {
	//第一步获取是否有同名的text
	if (isTabExists(text)) {
		$("#tabs").find("li>a.active").removeClass("active");
		$("#tabs").find("li#"+text+">a").addClass("active");
	} else {
		if (!!$("#tabs").find("li>a.active")) {
			$("#tabs").find("li>a.active").removeClass("active");
		}
		var li_element = document.createElement("li");
		li_element.id = text;
		li_element.setAttribute("class","nav-item");
		
		var a_element = document.createElement("a");
		a_element.innerText = text;
		a_element.href="#";
		a_element.setAttribute("class","nav-link active");
		
		
		li_element.appendChild(a_element);
		
		document.getElementById("tabs").appendChild(li_element);
	}
	$("#tabContent").load(url);
}

function isTabExists(title) {
	var existNode = $("#tabs").find("li#"+title);
	if (existNode.length > 0) {
		return true;
	} else {
		return false;
	}
}



/*(function ($) {
	"use strict";
	var Admin = {
		mainfunc: function() {
			$("#admin-side").metisMenu();
			$(window).bind("load resize", function () {
	            if ($(this).width() < 768) {
	                $('div.sidebar-collapse').addClass('collapse')
	            } else {
	                $('div.sidebar-collapse').removeClass('collapse')
	            }
	        });
		},
		initialization: function () {
			Admin.mainfunc();
	    }
	};
	$(document).ready(function () {
		Admin.mainfunc();
    });
}(jQuery));*/
