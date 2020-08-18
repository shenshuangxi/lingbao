/**
 * 需求: 定义一个框， 可以根据定义的方向进行移动  可以手动关闭，也可以在动画播放完毕后消失
 * 
 * 思路: 定义一个div 如果设置动画播放参数 则  设置好动画播放参数 $(selector).animate({params},speed,callback);
 * 		如果有 菜单点击栏则设置
 * 
 */

(function($){
	//1,使用严格ES5
	"use strict";
	
	//定义全局插件   定义局部对象插件 $.fn.lingbao-modal
	$.lingbao-modal = function(options) {
		var defaults = {
			width : 400px,
			height: 300px,
			background-color:gray,
			font-Color: "#0000",
			content: "OK"
		};
		var opts = $.extend( {}, defaults, options||{});
		var modal = $("<div>").css({
			"padding-left": ops.paddingL, 
			"padding-right": ops.paddingR, 
			"padding-top": ops.paddingT,
			"padding-bottom": ops.paddingB, 
			"border": "1px #000 solid",
			"position":"fixed",
			"left":"50%",
			"top":"50%",
			"background-color":ops.bgColor,
			"color":ops.fontColor,
			"font-size":ops.fontSize,
			"z-index":101 
			}).html(ops.cont).appendTo($("body"));
		modal.css({
			"margin-left":-(box.outerWidth(true)/2),
			"margin-top":-box.outerHeight(true)/2
			});
        return this;
    };
	
	
})(jQuery);