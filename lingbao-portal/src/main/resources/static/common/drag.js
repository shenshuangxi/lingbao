(function($){
	
	$.fn.drag = function() {
		var x, y;
		var mouseDown = false;
		var pThis = this;
		
			pThis.bind("touchstart", function (event) {
				if(event.originalEvent.targetTouches.length > 1 || event.scale && event.scale !== 1) return;
				x =  event.originalEvent.touches[0].clientX;
				y =  event.originalEvent.touches[0].clientY;
			});
		
			pThis.bind("touchmove", function (event) {
				if(event.originalEvent.targetTouches.length > 1 || event.scale && event.scale !== 1) return;
				event.preventDefault();
				if (event.originalEvent.touches[0].clientX!=x || event.originalEvent.touches[0].clientY!=y) {
					var top = parseFloat(pThis.css("top").substr(0,pThis.css("top").length-2));
					var left = parseFloat(pThis.css("left").substr(0,pThis.css("left").length-2));
					top = top + event.originalEvent.touches[0].clientY - y ;
					left = left + event.originalEvent.touches[0].clientX - x;
					var clientHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;   //height
					var clientWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;      //width
					if (top>=0 && top<= clientHeight) {
						pThis.css("top", top+"px");
						y =  event.originalEvent.touches[0].clientY;
					}
					if (left>=0  && left<= clientWidth) {
						pThis.css("left", left+"px");
						x =  event.originalEvent.touches[0].clientX;
					}
				}
			});
		
		
		pThis.mousedown(function(event){
			mouseDown = true;
			x = event.clientX;
			y = event.clientY;
		});
		$(document).mousemove(function(event){
			if (mouseDown) {
				if (event.clientX!=x || event.clientY!=y) {
					var top = parseFloat(pThis.css("top").substr(0,pThis.css("top").length-2));
					var left = parseFloat(pThis.css("left").substr(0,pThis.css("left").length-2));
					top = top + event.clientY - y;
					left = left + event.clientX - x;
					var clientHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;   //height
					var clientWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;      //width
					if (top>=0 && top <= clientHeight) {
						pThis.css("top", top+"px");
						y = event.clientY;
					}
					if (left>=0 && left <= clientWidth) {
						pThis.css("left", left+"px");
						x = event.clientX;
					}
				}
			}
		});
		$(document).mouseup(function(vent){
			mouseDown = false;
		});
		return this;
	}
})(jQuery);