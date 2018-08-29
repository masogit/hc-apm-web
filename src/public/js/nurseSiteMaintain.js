var NurseSiteMaintain = (function() {
	let running=false;
	let span1;
	let span2;
	let span3;
	
	return {	
		on:function(){
			running=true;
		},
		off:function(){
			running=false;
		},
		showHide : function(a) {
			if(running){
				span1=document.getElementById("type1Span");
				span2=document.getElementById("type2Span");
				span3=document.getElementById("type3Span");
				if (a.value) {
					if (a.value == 1) {
						span1.setAttribute("style", "display:block;");
						span2.setAttribute("style", "display:none;");
						span3.setAttribute("style", "display:none;");
					} else if(a.value==2) {
						span1.setAttribute("style", "display:none;");
						span2.setAttribute("style", "display:block;");
						span3.setAttribute("style", "display:none;");
					}
					else if(a.value==3) {
						span1.setAttribute("style", "display:none;");
						span2.setAttribute("style", "display:none;");
						span3.setAttribute("style", "display:block;");
					}
				}
			}
		}
	};
})();
