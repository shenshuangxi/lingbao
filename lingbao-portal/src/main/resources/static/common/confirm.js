(function($){
	$.lingbao_confirm = function(options) {
		
		var defaults = {
				title: '提示',
				close: true,
				borderRadius: '10px',
				border: "2px #cdcdcd solid",
				margin: '10px',
				width : "300px",
				height: "160px",
				bgColor:"#FCFCFC",
				fontColor: "#000000",
				fontSize: "20px",
				originTop: "20%",
				originLeft:"40%",
				moveTop:"20%",
				moveLeft:"80%",
				moveTime: 500,
				timeOut: 3000,
				align:"center",
				content: "OK",
				confirmButton: '确认',
				cancelButton:'取消',
				confirmButtonCallBack: null,
				cancelButtonCallBack: null
			};
		
		var opts = $.extend( {}, defaults, options||{});
		
		
		
		var confirm = $("<div class='row'>").css({
			"z-index":101,
			"position":"fixed",
			"top": opts.originTop,
			"left":opts.originLeft,
		}).appendTo($("body"));
		
		opts.okCallBack = function () {
			if (opts.confirmButtonCallBack) {
				opts.confirmButtonCallBack();
			}
			confirm.fadeOut(500, function() {
				confirm.remove()
			});
		}
		opts.cancleCallBack = function () {
			if (opts.cancelButtonCallBack) {
				opts.cancelButtonCallBack();
			}
			confirm.fadeOut(500, function() {
				confirm.remove()
			});
		}
		
		var marginConfirm = $("<div>").css({
			"width":opts.width,
			"height": opts.height,
			"background-color":opts.bgColor,
			"color":opts.fontColor,
			"font-size":opts.fontSize,
			"align": opts.align,
			"border": opts.border,
			"border-radius": opts.borderRadius
		});
		
		confirm.append(marginConfirm);
		
		var confirmDiv = $("<div>").css({
			"margin": opts.margin
			});
		
		marginConfirm.append(confirmDiv);
		
		var confirmTitle = "<strong>"+opts.title+"</strong>";
		var confirmClose = "<button type='button' class='close'>"
			+ "<span aria-hidden='true'>&times;</span>"
			+ "</button>";
		var confirmHeader = "<h4></h4>";
		var header = $(confirmHeader);
		header.append($(confirmTitle));
		if (opts.close) {
			header.append($(confirmClose).click(opts.cancleCallBack));
		}
		confirmDiv.append(header);
			
		if (opts.content.replace(/(^s*)|(s*$)/g, "").length > 0) {
			confirmDiv.append($("<div>"+opts.content+"</div>"))
		}
		
		var confirmCancel = "<button type='button' class='btn-sm btn-warning'>"+opts.cancelButton+"</button>";
		var confirmOk = "<button type='button' class='btn-sm btn-success'>"+opts.confirmButton+"</button>";
		var confirmFooter = "<div style='position: absolute; bottom: 10px; width: 90%;'></div>";
		var confirFooterStyle = "<span style='float: right; vertical-align: bottom;'></span>"
		
		var footer = $(confirmFooter);
		var footerStyle = $(confirFooterStyle);
		footer.append(footerStyle);
		
		
		var hasFooter = false;
		if (opts.cancelButton.replace(/(^s*)|(s*$)/g, "").length > 0) {
			var cancelButton = $(confirmCancel).css({
				"margin": "2px"
			});
			cancelButton.click(opts.cancleCallBack);
			footerStyle.append($(cancelButton));
			hasFooter = true;
		}
		if (opts.confirmButton.replace(/(^s*)|(s*$)/g, "").length > 0) {
			var okButton = $(confirmOk).css({
				"margin": "2px"
			});
			okButton.click(opts.okCallBack);
			footerStyle.append(okButton);
			hasFooter = true;
		}
		
		if (hasFooter) {
			confirmDiv.append(footer);
		}
		return confirm;
	}
})(jQuery);