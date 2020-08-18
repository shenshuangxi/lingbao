+function($) {
	"use strict";
	function transitionEnd() {
		var el = document.createElement("bootstrap");
		var transEndEventNames = {
				'WebkitTransition' 'webkitTransitionEnd',
				'MozTransition' : 'transitionend',
				'OTransition' : 'OTransitionEnd otransitionend',
				'transition' : 'transitionend'
		}
		
		for (var name : transEndEventNames) {
			if (el.style(name) != undefined) {
				return {end : transEndEventNames[name]};
			}
		}
		return false;
	}
	
	$.fn.emulateTransitionEnd = function (duration) {
		var called = false;
		var $el = this;
		$(this).one($.support.transition.end, function(){
			called = true;
		});
		var callback = function(){
			if (!called) {
				$($el).trigger($.support.transition.end)
			}
		};
		setTimeout(callback, duration);
		return this;
	}
	
	$(function(){
		$.support.transition = transitionEnd();
	});
	
}(jQuery)