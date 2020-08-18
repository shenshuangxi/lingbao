/**
 * 需求: 定义一个框， 可以根据定义的方向进行移动  可以手动关闭，也可以在动画播放完毕后消失
 * 
 * 思路: 定义一个div 如果设置动画播放参数 则  设置好动画播放参数 $(selector).animate({params},speed,callback);
 * 
 */

(function($){
	//1,使用严格ES5
	"use strict";
	//定义全局插件   定义局部对象插件 $.fn.lingbao-modal
	$.lingbao_popup = function(options) {
		var defaults = {
			padding: "20px",
			width : "200px",
			height: "60px",
			bgColor:"#F0F8FF",
			fontColor: "#000000",
			fontSize: "20px",
			content: "OK",
			originTop: "60%",
			originLeft:"80%",
			moveTop:"20%",
			moveLeft:"80%",
			moveTime: 500,
			timeOut: 3000,
			align:"center",
			className:""
		};
		var opts = $.extend( {}, defaults, options||{});
		var popup = $("<div>").css({
			"padding": opts.padding, 
			"width":opts.width,
			"height": opts.height,
			"background-color":opts.bgColor,
			"top": opts.originTop,
			"left":opts.originLeft,
			"color":opts.fontColor,
			"font-size":opts.fontSize,
			"align": opts.align,
			"border": "1px #C0C0C0 solid",
			"position":"fixed",
			"z-index":101 
			}).html(opts.content).appendTo($("body"));
		
		if (opts.className) {
			popup.addClass(opts.className);
		}
		
		popup.css({
			"margin-left":-(popup.outerWidth(true)/2),
			"margin-top":-popup.outerHeight(true)/2
			});
		popup.animate({
			top: opts.moveTop,
			right: opts.moveLeft
		},opts.speed,function(){
			popup.fadeOut(3000, function(){
				popup.remove();
			});
		});
        return this;
    };
	
})(jQuery);