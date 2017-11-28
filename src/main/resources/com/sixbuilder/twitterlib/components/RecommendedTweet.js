function initializeRecommendedTweet(options) {
	
	var SUMMARY_MODE = 'summary';
	var DETAIL_MODE = 'detail';
	var MODE_ATTRIBUTE = 'data-mode';
	var DIRTY_ATTRIBUTE = 'data-dirty';
	var TWEET_COUNT_WARNING = 'tweetCharacterCountWarning';
	var TWEET_COUNT_ERROR = 'tweetCharacterCountError'; 
	
	var outerDiv = $j('#' + options.id);
	var imgCount=options.imgCount;
//	var publishCheckbox = outerDiv.find('input.publishTweet');
	var isPublish = outerDiv.find('textarea.isPublish').text();
//	var shortenUrlButton = outerDiv.find('input.tweetShortenUrl');
//	var shortenUrlText = outerDiv.find('input.tweetUrl');
	var textarea = outerDiv.find('div.tweetText textarea');
	var summaryText = outerDiv.find('div.tweetText p');
	var characterCount = outerDiv.find('.tweetCharacterCount');
	var summaryView = outerDiv.find('div.tweetSummaryColumn');
	var summaryContainer = outerDiv.find('div.summaryContainer');
	var detailView = outerDiv.find('div.tweetDetailColumn');
	var hashtags = outerDiv.find('div.tweetSuggestedHashtags span');
	var attachSnapshotsCheckbox = outerDiv.find('input.tweetAttachSnapshotCheckbox');
	var imgSnapshotUrl= outerDiv.find('img.urlDetailSnapshot');
	var imgOneUrl = outerDiv.find('img.imgOneUrl');
	var imgTwoUrl = outerDiv.find('img.imgTwoUrl');
	var imgThreeUrl = outerDiv.find('img.imgThreeUrl');
	var imgIdx = outerDiv.find('textarea.imgIdx').text();
	var viewImg = outerDiv.find('i.fa-eye');
	var toggleImgLeft = outerDiv.find('i.fa-toggle-left');
	var toggleImgRight = outerDiv.find('i.fa-toggle-right');
	var btnMeh=outerDiv.find('input.tweetCancel');
	var btnSave=outerDiv.find('input.tweetSave');
	
	acknowledgeDataMode() ;
	
	selectImage();
	
	if(isPublish=="true") {
		btnSave.attr("value","Save");
		btnMeh.attr("value","Clear");
	} else {
		btnSave.attr("value","Queue");
		btnMeh.attr("value","Meh");
	}
	
	if(imgCount<=1) {
		//toggleImgLeft.attr("disabled", 'disabled');
		//toggleImgRight.attr("disabled", 'disabled');
		toggleImgLeft.css({ opacity: 0.5 });
		toggleImgRight.css({ opacity: 0.5 });
	}
	
	toggleImgLeft.click(function(event) {
		imgIdx--;
		if(imgIdx<0) {
			imgIdx=imgCount-1;
		}
		selectImage();
		// save
		// save
		var eventLink=options.saveImgIdx;
		eventLink=eventLink.replace("6BUILDERTOKEN",imgIdx);
		$j.ajax(eventLink).done(function(result) {
			
		});
		event.preventDefault();
	});
	
	toggleImgRight.click(function(event) {
		imgIdx++;
		if(imgIdx>=imgCount) {
			imgIdx=0;
		}
		selectImage();
		// save
		var eventLink=options.saveImgIdx;
		eventLink=eventLink.replace("6BUILDERTOKEN",imgIdx);
		$j.ajax(eventLink).done(function(result) {
			
		});
		event.preventDefault();
	});
	
	function selectImage() {
		var opacity=1;
		if(isPublish=="true"&&attachSnapshotsCheckbox[0]&&!attachSnapshotsCheckbox[0].checked) {
			opacity=.2;
		}
		var el=outerDiv.find('textarea.imgIdx');
		el.text(imgIdx);
		if(imgIdx==0) {
			imgSnapshotUrl.fadeTo(1,opacity);
			if(!imgSnapshotUrl.is(":visible")) {
				imgSnapshotUrl.show();
			}
			imgOneUrl.hide();
			imgTwoUrl.hide();
			imgThreeUrl.hide();
		}
		if(imgIdx==1) {
			imgSnapshotUrl.hide();
			imgOneUrl.fadeTo(1,opacity);
			if(!imgOneUrl.is(":visible")) {
				imgOneUrl.show();
			}
			imgTwoUrl.hide();
			imgThreeUrl.hide();
		}
		if(imgIdx==2) {
			imgSnapshotUrl.hide();
			imgOneUrl.hide();
			imgTwoUrl.fadeTo(1,opacity);
			if(!imgTwoUrl.is(":visible")) {
				imgTwoUrl.show();
			}
			imgThreeUrl.hide();
		}
		if(imgIdx==3) {
			imgSnapshotUrl.hide();
			imgOneUrl.hide();
			imgTwoUrl.hide();
			imgThreeUrl.fadeTo(1,opacity);
			if(!imgThreeUrl.is(":visible")) {
				imgThreeUrl.show();
			}
		}
	}
	
	// publish checkbox
	//T5.initializers.updateZoneOnEvent('click', publishCheckbox.attr('id'), '^', options.publishUrl);
	
	outerDiv.find('input.tweetSave').click(function(event) {
		modeDetail();
	});

	outerDiv.find('input.tweetCancel').click(function(event) {
		modeSummary();
//		if (options.inQueue == false) {
//			event.preventDefault();
//		}
	});
	
	// switch to detail view
	summaryView.click(function(event) {
		if (outerDiv.attr(MODE_ATTRIBUTE) === SUMMARY_MODE) {
			// expand into detail mode
			outerDiv.attr(MODE_ATTRIBUTE, DETAIL_MODE);
			updateCharacterCount();
			//summaryContainer.hide();
			acknowledgeDataMode();
		//	disablePublishCheckbox();
			var eventLink=options.setDetailMode;
			$j.ajax(eventLink).done(function(result) {
				
			});
		}
		event.preventDefault();
	});
	
	function acknowledgeDataMode() {
		if (outerDiv.attr(MODE_ATTRIBUTE) === SUMMARY_MODE) {
			summaryContainer.show();
			detailView.hide();
		} else {
			summaryContainer.hide();
			detailView.show();			
		}
	}
	
	attachSnapshotsCheckbox.click(function(event) {
		updateCharacterCount();
		// save
		var eventLink=options.saveAttachSnapshot;
		var el=outerDiv.find('input.tweetAttachSnapshotCheckbox');
		var val=el[0].checked;
		eventLink=eventLink.replace("6BUILDERTOKEN",val);
		$j.ajax(eventLink).done(function(result) {
			
		});
		//event.preventDefault();
	});
	
	// character count
	textarea.keyup(handleSummaryChange);
	
	hashtags.click(function(event) {
		var hashtag = $j(this).text();
		var summary = textarea.val().trim();
		if (summary.indexOf(hashtag) < 0) {
			summary = summary + ' ' + hashtag;
			textarea.val(summary);
			handleSummaryChange();
		}
		event.preventDefault();
	});
	
	/*
	function save() {
		handleSummaryChange();
		var data = { summary : textarea.val(), attachSnapshot : attachSnapshotsCheckbox[0].checked };
		$j.ajax(options.saveUrl, { data : data }).done(function(result) {
			// FIXME: what to do now? nothing? show some confirmation?
			modeSummary();
	//		enablePublishCheckbox();
		});
	}
	*/
	
	function handleSummaryChange() {
		var count = twttr.txt.getTweetLength(textarea.val());
		summaryText.text(textarea.val());
		updateCharacterCount();
	}
	
	function updateCharacterCount() {
		var count = twttr.txt.getTweetLength(textarea.val());
		if( attachSnapshotsCheckbox[0] && attachSnapshotsCheckbox[0].checked) {
			count=count+23;
		}
		characterCount.text(count);
		if (count < 277) {
			characterCount.removeClass(TWEET_COUNT_WARNING);
			characterCount.removeClass(TWEET_COUNT_ERROR);
		}
		else if (count <= 280) {
			characterCount.addClass(TWEET_COUNT_WARNING);
			characterCount.removeClass(TWEET_COUNT_ERROR);
		}
		else {
			characterCount.addClass(TWEET_COUNT_ERROR);
			characterCount.removeClass(TWEET_COUNT_WARNING);
		}
		
		var btn=outerDiv.find('.tweetSave');
		if(count>280) {
			btn.attr("disabled", 'disabled');
		} else {
			btn.removeAttr('disabled');
		}
		selectImage();
	}
	
	function modeSummary() {
		summaryContainer.show();
		outerDiv.attr(MODE_ATTRIBUTE, SUMMARY_MODE);
	}
	
	function modeDetail() {
		summaryContainer.hide();
		outerDiv.attr(MODE_ATTRIBUTE, DETAIL_MODE);
	}
	
	function string2Bin(str) {
	    return str.split("").map( function( val ) { 
	        return val.charCodeAt( 0 ); 
	    } );
	}
	
	function toBinaryString(inputString) {
	    var ret="";
	    // get the input string as series of integers
	    for(var i=0;i<inputString.length;i++) {
	        var charCode=inputString.charCodeAt(i);     
	        // for each integer, convert to binary string
	        var stringValue=charCode.toString(2);
	        // pad to 16 bits
	        if(stringValue.length<16) {
	        	var ll=16-stringValue.length;
	        	for(var ii=0;ii<ll;ii++) {
	        		stringValue="0"+stringValue;
	        	}
	        }
	        // concatenate
	        ret=ret+stringValue;
	    }
	    return ret;
	}
	
}
