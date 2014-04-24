function initializeTweetEngagement(options) {

	var SUMMARY_MODE = 'summary';
	var DETAIL_MODE = 'detail';
	var MODE_ATTRIBUTE = 'data-mode';
	var DIRTY_ATTRIBUTE = 'data-dirty';
	var TWEET_COUNT_WARNING = 'tweetCharacterCountWarning';
	var TWEET_COUNT_ERROR = 'tweetCharacterCountError'; 
	
	var outerDiv = $('#' + options.id);
	var actionsZone = outerDiv.find('ul.tweetEngagementActions');
	var actionLinks = actionsZone.find('a');
	var tweetId = outerDiv.attr('data-tweet-id');
	
	// updateCharacterCount();
	
	actionLinks.each(function() {
		var action = $(this).attr('data-action');
		if (action == 'FOLLOW' || action == 'RETWEET' || action == 'FAVORITE' || action == 'DELETE') {
			var url = options.actionUrl + "?id=" + tweetId + "&action=" + action + "&clientId=" + options.id;
			T5.initializers.updateZoneOnEvent('click', $(this).attr('id'), '^', url)
		}
		// FIXME: implement list, reply and reply_all
	});
	
	// character count
	// textarea.keyup(handleSummaryChange);
	
	/*
	function save() {
		handleSummaryChange();
		var data = { summary : textarea.val(), attachSnapshot : attachSnapshotsCheckbox[0].checked };
		$.ajax(options.saveUrl, { data : data }).done(function(result) {
			// FIXME: what to do now? nothing? show some confirmation?
			modeSummary();
			enablePublishCheckbox();
		});
	}
	
	function handleSummaryChange() {
		publishCheckbox[0].disabled = true;
		summaryText.text(textarea.val());
		updateCharacterCount();
	}
	
	function updateCharacterCount() {
		var count = twttr.txt.getTweetLength(textarea.val());
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
	}
	
	function modeSummary() {
		outerDiv.attr(MODE_ATTRIBUTE, SUMMARY_MODE);
	}
	
	function modeDetail() {
		outerDiv.attr(MODE_ATTRIBUTE, DETAIL_MODE);
	}
	*/
	
}