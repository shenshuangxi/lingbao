(function($){
	$.fn.lingbao_file = function(options) {
		var defaults = {
				appId : "",
				envId : "",
				clusterId : "",
				fileId : "",
				fileName: '文件名',
				contents: "",
				lineHeight: 23,
				needButtons: true
			};
		var opts = $.extend( {}, defaults, options||{});
		
		var random = new Date().getTime()+"-"+parseInt(Math.random()*100000);
		
		var container = $("<div class='container-fluid'></div>").appendTo(this);
		var buttonsRow = $("<div class='row no-gutter'></div>").appendTo(container);
		var buttonsCol = $("<div class='col-12'>").appendTo(buttonsRow);
		
		if (opts.needButtons) {
			var badge = $("<div style='position:absolute;margin-top: 7px; margin-left: 40px;'></div>").appendTo(buttonsCol);
			var badgeButton = $("<a href='javascript:void(0);' class='badge badge-pill badge-success'><i id='"+random+"-file-badge' class='fa fa-angle-double-down' aria-hidden='true'></i>&nbsp;&nbsp;"+opts.fileName+"&nbsp;&nbsp;</a>").appendTo(badge);
			var buttonsDiv = $("<div style='text-align: right; background-color:#ffffff;'></div>").appendTo(buttonsCol);
			var editRemove = $("<div></div>").appendTo(buttonsDiv);
			var editButton = $("<a href='javascript:void(0);' class='btn btn-success btn-sm'><i class='fa fa-pencil-square-o'>编辑</i></a>").appendTo(editRemove);
			$("<span>&nbsp;</span>").appendTo(editRemove);
			var removeButton = $("<a href='javascript:void(0);' class='btn btn-danger btn-sm'><i class='fa fa-times'>删除</i></a>").appendTo(editRemove);
			var cancelSave = $("<div style='display:none'></div>").appendTo(buttonsDiv);
			var cancelButton = $("<a href='javascript:void(0);' class='btn btn-info btn-sm'><i class='fa fa-times'>取消</i></a>").appendTo(cancelSave);
			$("<span>&nbsp;</span>").appendTo(cancelSave);
			var saveButton = $("<a href='javascript:void(0);' class='btn btn-warning btn-sm'><i class='fa fa-check'>保存</i></a>").appendTo(cancelSave);
		} else {
			var badge = $("<div style='text-align: center; background-color:#ffffff;'></div>").appendTo(buttonsCol);
			var badgeButton = $("<a href='javascript:void(0);' class='badge badge-pill badge-success'><i id='"+random+"-file-badge' class='fa fa-angle-double-down' aria-hidden='true'></i>&nbsp;&nbsp;"+opts.fileName+"&nbsp;&nbsp;</a>").appendTo(badge);
		}
		
		
		var fileRow = $("<div class='row no-gutter'></div>").appendTo(container);
		var fileCol = $("<div class='col-12'>").appendTo(fileRow);
		var file = $("<div  class='collapse' id='"+random+"-file' style='width: 100%; min-height: 24px; background-color: #dcdcdc; border: 2px solid #BDBDBD;'></div>").appendTo(fileCol);
		var line = $("<div style='position:absolute; width:30px; height: 100%;'></div>").append($("<div style='margin:1px 2px 1px 10px; height: 23px; color: #6E6E6E;'>1</div>")).appendTo(file);
		var contentParent = $("<div style='margin:0 0 0 30px; border-left: 1px solid #848484; width:100%; height: 100%;'></div>").appendTo(file);
		var content = $("<div contenteditable='false'></div> ").appendTo(contentParent)
		var contentMaskParent = $("<div style='display:none; margin:0 0 0 30px; border-left: 1px solid #848484; width:100%; height: 100%;'></div>").appendTo(file);
		var contentMask = $("<div contenteditable='true'></div> ").appendTo(contentMaskParent);
		
		if(opts.contents.length>0) {
			var contents = opts.contents.split(",");
			var line_result = "";
			for(var i=0; i<contents.length; i++) {
				if (contents[i].length>0) {
					$("<div></div>").text(contents[i]).appendTo(content);
				} else {
					$("<div><br/></div>").appendTo(content);
				}
				line_result += "<div id='line-"+(i+1)+"' style='margin:1px 2px 1px 10px; height: "+opts.lineHeight+"px; color: #6E6E6E;'>"+(i+1)+"</div>";
			}
			line.html(line_result);
		}
		
		badgeButton.click(function(){
			if ($("#"+random+"-file-badge").attr("class")==="fa fa-angle-double-up") {
				$("#"+random+"-file-badge").attr("class","fa fa-angle-double-down");
			} else {
				$("#"+random+"-file-badge").attr("class","fa fa-angle-double-up");
			}
			$("#"+random+"-file").collapse('toggle');
		});
		
		if (opts.needButtons) {
			editButton.click(function(){
				console.log("editButton")
				editRemove.hide();
				cancelSave.show();
				contentParent.hide();
				contentMaskParent.show();
				
				//拷贝content-layer值到content-layer-master种
				contentMask.html(content.html());
				//回车执行添加行号
				contentMask.unbind("resize").bind("resize",function(e){
			        // 兼容FF和IE和Opera    
		    		var count = contentMask.height()/(opts.lineHeight+1);
			        count = parseInt(count);
			        if (count>0) {
			        	var line_result = "";
			       		for (var i=0; i<count; i++) {
			       			line_result += "<div id='line-"+(i+1)+"' style='margin:1px 2px 1px 10px; height: "+opts.lineHeight+"px; color: #6E6E6E;'>"+(i+1)+"</div>";
			    		}
			       		line.html(line_result);
			        }
			    });
			});
			
			removeButton.click(function(){
				$.lingbao_confirm({
					confirmButtonCallBack: function(){
						$.ajax({
							type: "delete",
							url: "/api/v1/app/"+opts.appId+"/env/"+opts.envId+"/cluster/"+opts.clusterId+"/file/"+opts.fileId,
							beforeSend: function(XMLHttpRequest) {
								XMLHttpRequest.setRequestHeader("Authentication", localStorage.getItem("Authentication"));
							},
							success:function(retData) {
								container.remove();
								$.lingbao_popup({
									content: retData.responseText
								});
							},
							error: function(e) {
								console.log(e);
								$.lingbao_popup({
									content: e.responseText
								});
								if (e.status===401) {
									window.location.href='/login.html?'+window.location.href;
								} 
							},
							complete: function(code) {
								console.log(code);
							}
						});
					}
				}).drag();
			});
			
			cancelButton.click(function(){
				console.log("cancelButton")
				editRemove.show();
				cancelSave.hide();
				contentMaskParent.hide();
				contentParent.show();
				var count = parseInt(content.height()/(opts.lineHeight+1));
				console.log(count);
		        if (count>0) {
		        	var line_result = "";
		       		for (var i=0; i<count; i++) {
		       			line_result += "<div id='line-"+(i+1)+"' style='margin:1px 2px 1px 10px; height: "+opts.lineHeight+"px; color: #6E6E6E;'>"+(i+1)+"</div>";
		    		}
		       		line.html(line_result);
		        } else {
		        	line.html("<div id='line-"+1+"' style='margin:1px 2px 1px 10px; height: "+opts.lineHeight+"px; color: #6E6E6E;'>"+1+"</div>");
		        }
			});

			saveButton.click(function(){
				console.log("saveButton")
				var htmls;
				if (contentMask.html().match(new RegExp("^<div>.*$"))) {
					htmls = contentMask.html().split("<div>");
					htmls.splice(0,1);
				} else {
					htmls = contentMask.html().split("<div>");
				}
				for (var i=0; i<htmls.length; i++) {
					htmls[i] = htmls[i].replace("</div>", "");
				}
				for (var i=0; i<htmls.length; i++) {
					htmls[i] = $("<div></div>").html(htmls[i]).text();
				}
				var contents = {};
				for (var i=0; i<htmls.length; i++) {
					contents[i+1] = htmls[i];
				}
				var data = JSON.stringify({
					appId: opts.appId,
					envId: opts.envId,
					clusterId: opts.clusterId,
					name : opts.fileName,
					contents:contents
				});
				$.ajax({
					type: "patch",
					url: "/api/v1/app/"+opts.appId+"/env/"+opts.envId+"/cluster/"+opts.clusterId+"/file/"+opts.fileId,
					contentType: "application/json;charset=UTF-8",
					dataType: "json",
					data: data,
					beforeSend: function(XMLHttpRequest) {
						XMLHttpRequest.setRequestHeader("Authentication", localStorage.getItem("Authentication"));
					},
					success:function(retData) {
						editRemove.show();
						cancelSave.hide();
						contentMaskParent.hide();
						contentParent.show();
						content.html(contentMask.html());
					},
					error: function(e) {
						console.log(e);
						$.lingbao_popup({
							content: e.responseText
						});
						if (e.status===401) {
							window.location.href='/login.html?'+window.location.href;
						}
					},
					complete: function(code) {
						console.log(code);
					}
				});
			});
		}
	}
})(jQuery);