function initializeRecommendedTweet(options) {
	
	var SUMMARY_MODE = 'summary';
	var DETAIL_MODE = 'detail';
	var MODE_ATTRIBUTE = 'data-mode';
	var DIRTY_ATTRIBUTE = 'data-dirty';
	var TWEET_COUNT_WARNING = 'tweetCharacterCountWarning';
	var TWEET_COUNT_ERROR = 'tweetCharacterCountError'; 
	
	var outerDiv = $j('#' + options.id);
//	var publishCheckbox = outerDiv.find('input.publishTweet');
	var shortenUrlButton = outerDiv.find('input.tweetShortenUrl');
	var shortenUrlText = outerDiv.find('input.tweetUrl');
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
	
	acknowledgeDataMode() ;
	
	selectImage();
	
	toggleImgLeft.click(function(event) {
		imgIdx--;
		if(imgIdx<0) {
			imgIdx=3;
		}
		selectImage();
	});
	
	toggleImgRight.click(function(event) {
		imgIdx++;
		if(imgIdx>3) {
			imgIdx=0;
		}
		selectImage();
	});
	
	function selectImage() {
		var el=outerDiv.find('textarea.imgIdx');
		el.text(imgIdx);
		if(imgIdx==0) {
			imgSnapshotUrl.show();
			imgOneUrl.hide();
			imgTwoUrl.hide();
			imgThreeUrl.hide();
		}
		if(imgIdx==1) {
			imgSnapshotUrl.hide();
			imgOneUrl.show();
			imgTwoUrl.hide();
			imgThreeUrl.hide();
		}
		if(imgIdx==2) {
			imgSnapshotUrl.hide();
			imgOneUrl.hide();
			imgTwoUrl.show();
			imgThreeUrl.hide();
		}
		if(imgIdx==3) {
			imgSnapshotUrl.hide();
			imgOneUrl.hide();
			imgTwoUrl.hide();
			imgThreeUrl.show();
		}
	}
	
	// publish checkbox
	//T5.initializers.updateZoneOnEvent('click', publishCheckbox.attr('id'), '^', options.publishUrl);
	
	shortenUrlButton.click(function(event) {
		var eventLink=options.shortenUrlUrl;
		var val=shortenUrlText[0].value;
		val=toBinaryString(val);
		eventLink=eventLink.replace("6BUILDERTOKEN",val);
		$j.ajax(eventLink).done(function(result) {
			var shortenedUrl = result.url;
			var newSummary = textarea.val().replace(outerDiv.attr('data-original-url'), shortenedUrl);
			if (newSummary.indexOf(shortenedUrl) < 0) {
				newSummary = newSummary + ' ' + shortenedUrl;
			}
			textarea.val(newSummary);
			summaryText.text(newSummary);
			handleSummaryChange();
		});
		event.preventDefault();
	});
	
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
		if (count < 137) {
			characterCount.removeClass(TWEET_COUNT_WARNING);
			characterCount.removeClass(TWEET_COUNT_ERROR);
		}
		else if (count <= 140) {
			characterCount.addClass(TWEET_COUNT_WARNING);
			characterCount.removeClass(TWEET_COUNT_ERROR);
		}
		else {
			characterCount.addClass(TWEET_COUNT_ERROR);
			characterCount.removeClass(TWEET_COUNT_WARNING);
		}
		
		var btn=outerDiv.find('.tweetSave');
		if(count>140) {
			btn.attr("disabled", 'disabled');
		} else {
			btn.removeAttr('disabled');
		}
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
