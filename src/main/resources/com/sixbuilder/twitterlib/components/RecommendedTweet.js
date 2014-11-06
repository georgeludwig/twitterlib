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
	var imgOneUrl =  outerDiv.find('img.imgOneUrl');
	var imgTwoUrl =  outerDiv.find('img.imgTwoUrl');
	var imgThreeUrl =  outerDiv.find('img.imgThreeUrl');
	var imgIndex=0;
	var viewImg =  outerDiv.find('i.fa-eye');
	var toggleImgLeft = outerDiv.find('i.fa-toggle-left');
	var toggleImgRight = outerDiv.find('i.fa-toggle-right');
	
	imgOneUrl.hide();
	imgTwoUrl.hide();
	imgThreeUrl.hide();
	
	toggleImgLeft.click(function(event) {
		if ($(imgSnapshotUrl).is(":visible")) {
			imgSnapshotUrl.hide();
			imgThreeUrl.show();
			imgIndex=3;
		} else if ($(imgOneUrl).is(":visible")) {
			imgSnapshotUrl.show();
			imgOneUrl.hide();
			imgIndex=0;
		} else if ($(imgTwoUrl).is(":visible")) {
			imgOneUrl.show();
			imgTwoUrl.hide();
			imgIndex=1;
		} else if ($(imgThreeUrl).is(":visible")) {
			imgTwoUrl.show();
			imgThreeUrl.hide();
			imgIndex=2;
		}
	});
	
	toggleImgRight.click(function(event) {
		if ($(imgSnapshotUrl).is(":visible")) {
			imgSnapshotUrl.hide();
			imgOneUrl.show();
			imgIndex=1;
		} else if ($(imgOneUrl).is(":visible")) {
			imgTwoUrl.show();
			imgOneUrl.hide();
			imgIndex=2;
		} else if ($(imgTwoUrl).is(":visible")) {
			imgThreeUrl.show();
			imgTwoUrl.hide();
			imgIndex=3;
		} else if ($(imgThreeUrl).is(":visible")) {
			imgSnapshotUrl.show();
			imgThreeUrl.hide();
			imgIndex=0;
		}
	});
	
	// publish checkbox
	//T5.initializers.updateZoneOnEvent('click', publishCheckbox.attr('id'), '^', options.publishUrl);
	
	//outerDiv.find('input.tweetShortenUrl').click(function(event) {
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
		modeSummary();
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
			summaryContainer.hide();
		//	disablePublishCheckbox();
		}
		event.preventDefault();
	});
	
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
	//	publishCheckbox[0].disabled = true;
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
	
//	function enablePublishCheckbox() {
//		if (outerDiv.attr('data-publish') != 'true') {
//			publishCheckbox[0].disabled = false;
//		}
//	}
//	
//	function disablePublishCheckbox() {
//		publishCheckbox[0].disabled = true;
//	}
	
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
	
	function StringBuffer()
	{ 
	    this.buffer = []; 
	} 

	StringBuffer.prototype.append = function append(string)
	{ 
	    this.buffer.push(string); 
	    return this; 
	}; 

	StringBuffer.prototype.toString = function toString()
	{ 
	    return this.buffer.join(""); 
	}; 

	var Base64 =
	{
	    codex : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

	    encode : function (input)
	    {
	        var output = new StringBuffer();

	        var enumerator = new Utf8EncodeEnumerator(input);
	        while (enumerator.moveNext())
	        {
	            var chr1 = enumerator.current;

	            enumerator.moveNext();
	            var chr2 = enumerator.current;

	            enumerator.moveNext();
	            var chr3 = enumerator.current;

	            var enc1 = chr1 >> 2;
	            var enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
	            var enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
	            var enc4 = chr3 & 63;

	            if (isNaN(chr2))
	            {
	                enc3 = enc4 = 64;
	            }
	            else if (isNaN(chr3))
	            {
	                enc4 = 64;
	            }

	            output.append(this.codex.charAt(enc1) + this.codex.charAt(enc2) + this.codex.charAt(enc3) + this.codex.charAt(enc4));
	        }

	        return output.toString();
	    },

	    decode : function (input)
	    {
	        var output = new StringBuffer();

	        var enumerator = new Base64DecodeEnumerator(input);
	        while (enumerator.moveNext())
	        {
	            var charCode = enumerator.current;

	            if (charCode < 128)
	                output.append(String.fromCharCode(charCode));
	            else if ((charCode > 191) && (charCode < 224))
	            {
	                enumerator.moveNext();
	                var charCode2 = enumerator.current;

	                output.append(String.fromCharCode(((charCode & 31) << 6) | (charCode2 & 63)));
	            }
	            else
	            {
	                enumerator.moveNext();
	                var charCode2 = enumerator.current;

	                enumerator.moveNext();
	                var charCode3 = enumerator.current;

	                output.append(String.fromCharCode(((charCode & 15) << 12) | ((charCode2 & 63) << 6) | (charCode3 & 63)));
	            }
	        }

	        return output.toString();
	    }
	}


	function Utf8EncodeEnumerator(input)
	{
	    this._input = input;
	    this._index = -1;
	    this._buffer = [];
	}

	Utf8EncodeEnumerator.prototype =
	{
	    current: Number.NaN,

	    moveNext: function()
	    {
	        if (this._buffer.length > 0)
	        {
	            this.current = this._buffer.shift();
	            return true;
	        }
	        else if (this._index >= (this._input.length - 1))
	        {
	            this.current = Number.NaN;
	            return false;
	        }
	        else
	        {
	            var charCode = this._input.charCodeAt(++this._index);

	            // "\r\n" -> "\n"
	            //
	            if ((charCode == 13) && (this._input.charCodeAt(this._index + 1) == 10))
	            {
	                charCode = 10;
	                this._index += 2;
	            }

	            if (charCode < 128)
	            {
	                this.current = charCode;
	            }
	            else if ((charCode > 127) && (charCode < 2048))
	            {
	                this.current = (charCode >> 6) | 192;
	                this._buffer.push((charCode & 63) | 128);
	            }
	            else
	            {
	                this.current = (charCode >> 12) | 224;
	                this._buffer.push(((charCode >> 6) & 63) | 128);
	                this._buffer.push((charCode & 63) | 128);
	            }

	            return true;
	        }
	    }
	}

	function Base64DecodeEnumerator(input)
	{
	    this._input = input;
	    this._index = -1;
	    this._buffer = [];
	}

	Base64DecodeEnumerator.prototype =
	{
	    current: 64,

	    moveNext: function()
	    {
	        if (this._buffer.length > 0)
	        {
	            this.current = this._buffer.shift();
	            return true;
	        }
	        else if (this._index >= (this._input.length - 1))
	        {
	            this.current = 64;
	            return false;
	        }
	        else
	        {
	            var enc1 = Base64.codex.indexOf(this._input.charAt(++this._index));
	            var enc2 = Base64.codex.indexOf(this._input.charAt(++this._index));
	            var enc3 = Base64.codex.indexOf(this._input.charAt(++this._index));
	            var enc4 = Base64.codex.indexOf(this._input.charAt(++this._index));

	            var chr1 = (enc1 << 2) | (enc2 >> 4);
	            var chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
	            var chr3 = ((enc3 & 3) << 6) | enc4;

	            this.current = chr1;

	            if (enc3 != 64)
	                this._buffer.push(chr2);

	            if (enc4 != 64)
	                this._buffer.push(chr3);

	            return true;
	        }
	    }
	};

	
}
