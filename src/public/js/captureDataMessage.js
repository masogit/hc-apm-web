var CaptureDataMessageJS = (function() {
	var beginImage = '<img';
	var beginImageLength = beginImage.length;
	var endImage = '</img>';
	var endImageLength = endImage.length;
	var brTag = '<br>';
	var contentEditor = null;
	var contentEditorMessage = null;
	var imageTextTempContent = null;
	var uploadElement = null;
	var contentEditorTextArea = null;
	var initCaptureDataMessageParamBln = true;
	return {
		initCaptureDataMessageParam : function() {
			initCaptureDataMessageParamBln = true;
		},
		captureDataMessageParseImage : function(str) {
			var strArr = [];
			var start = 0;
			var end = 0;
			if (str) {
				while ((start = str.indexOf(beginImage)) > -1) {
					if (start != 0) {
						strArr.push(str.substr(0, start));
						str = str.substring(start, str.length);
						start = 0;
					}
					end = str.indexOf('">');
					if (end > -1) {
						// ignore image
						if (end + 1 != str.length) {
							str = str.substring(end + 2, str.length);
						} else {
							str = "";
						}
					}
				}
				if (str) {
					strArr.push(str);
				}
			}
			return strArr;
		},
		showHide : function(a) {
			var type0Span = document.getElementById("type0Span");
			if (a.value) {
				if (a.value == 0) {
					type0Span.setAttribute("style", "display:block;");
				} else {
					type0Span.setAttribute("style", "display:none;");
				}
			}
		},
		setCaptureDataMessageValue : function() {
			if (initCaptureDataMessageParamBln == false) {
				return;
			}
			contentEditor = $("[id='captureDataMessagesEditForm:idContentEditor'] iframe")
					.contents().find("html body");
			contentEditorMessage = $("#idContentEditorMessage");
			contentEditorTextArea = $("[id='captureDataMessagesEditForm:idContentEditor_input']");
			imageTextTempContent = $("[id='captureDataMessagesEditForm:imageTextTempContent']");
			uploadElement = $("[id^='captureDataMessagesEditForm'] .ui-fileupload input");
			initCaptureDataMessageParamBln = false;
		},
		contentEditorChange : function() {
			var noError = true;
			var errorString = "请注意！";
			this.setCaptureDataMessageValue();
			var contentStr = contentEditor.html();
			if (contentStr) {
				var strArr = this.captureDataMessageParseImage(contentStr);
				var strCount = 0;
				for ( var obj in strArr) {
					strCount += strArr[obj].length;
				}
				if (strCount > 120) {
					errorString += "连续输入120个字符将被强制分页。";
					noError = false;
				}
				if (contentStr.indexOf(brTag) > -1) {
					errorString += "不允许输入回车符。";
					noError = false;
					contentStr = contentStr.replace(/<br>/g, "");
					contentEditor.html(contentStr);
				}
				if (noError) {
					contentEditorMessage.html("").css("display", "none");
				} else {
					contentEditorMessage.html(errorString).css("display", "block");
				}
			}
		},
		setImageTextTempContent : function() {
			initCaptureDataMessageParamBln = true;
			this.setCaptureDataMessageValue();
			var htmlStr=imageTextTempContent.html();
			htmlStr=htmlStr.replace(/&lt;/,'<');
			htmlStr=htmlStr.replace(/&gt;/,'>');
			contentEditor.html(contentEditor.html() + htmlStr);
			contentEditorTextArea.val(contentEditorTextArea.val() + htmlStr);
		},
		triggerUpload : function() {
			initCaptureDataMessageParamBln = true;
			this.setCaptureDataMessageValue();
			uploadElement.trigger("click");
		}
	};
})();
