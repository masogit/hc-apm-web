var CommonUtilsJS = (function() {
	return {	
		multipleSelect:function(params){
			let arr=params.split(",");
			let formId=arr[0];
			let headerId=arr[1];
			let itemsId=arr[2];
			var selectAllHeader = $('[id$="'+headerId+'"] input')[0];
            var checked = selectAllHeader.checked;
            $('[id="'+formId+'"]').find('[id$="'+itemsId+'"]').each(function(index, obj) {
            	// console.info(obj);
                $(obj).find('.ui-chkbox-box').each(function(index,obj2){
                	if(checked){
                		$(obj2).addClass("ui-state-active");
                	}
                	else{
                		$(obj2).removeClass("ui-state-active");
                	}
                });
                $(obj).find('.ui-chkbox-box span').each(function(index,obj2){
                	if(checked){
	                    $(obj2).addClass("ui-icon-check").removeClass("ui-icon-blank");
	                }
	                else{
	                    $(obj2).addClass("ui-icon-blank").removeClass("ui-icon-check");
	                }
                });
            });
		}
	};
})();
