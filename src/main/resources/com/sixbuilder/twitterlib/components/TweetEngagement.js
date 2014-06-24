function initializeTweetEngagement(options) {

	var TWEET_COUNT_WARNING = 'tweetCharacterCountWarning';
	var TWEET_COUNT_ERROR = 'tweetCharacterCountError'; 
	
	var outerDiv = $j('#' + options.id);
	var actionsZone = outerDiv.find('ul.tweetEngagementActions');
	var actionLinks = actionsZone.find('a');
	var replyDiv = outerDiv.find('div.tweetEngagementReply');
	var replyTypeHidden = outerDiv.find('div.tweetEngagementReply input[name=replyType]');
	var replyClientIdHidden = outerDiv.find('div.tweetEngagementReply input[name=clientId]');
	var tweetId = outerDiv.attr('data-tweet-id');
	var textArea = replyDiv.find('textarea');
	var characterCount = replyDiv.find('span.tweetCharacterCount');
	
	actionLinks.each(function() {
		var action = $j(this).attr('data-action');
		if (action == 'FOLLOW' || action == 'RETWEET' || action == 'FAVORITE' || action == 'DELETE') {
			var url = options.actionUrl + "?id=" + tweetId + "&action=" + action + "&clientId=" + options.id;
			T5.initializers.updateZoneOnEvent('click', $j(this).attr('id'), '^', url)
		}
		else if (action == 'REPLY') {
			$j(this).click(function(event) {
				handleReply(event, 'REPLY', '@' + outerDiv.attr('data-tweet-author') + ' ');
			});
		}
		else if (action == 'REPLY_ALL') {
			$j(this).click(function(event) {
				handleReply(event, 'REPLY_ALL', '');			
			});
		}
		// FIXME: implement list
	});
	
	// character count
	textArea.keyup(updateCharacterCount);
	
	function handleReply(event, hiddenValue, initialContent) {
		replyTypeHidden.val(hiddenValue);
		replyClientIdHidden.val(outerDiv.parents('div.tweetEngagement').attr('id'));
		textArea.val(initialContent);
		updateCharacterCount();
		replyDiv.show();
		textArea.focus();
		event.preventDefault();
		var area = textArea[0];
        if (area.setSelectionRange) {
            area.focus();
            area.setSelectionRange(initialContent.length, initialContent.length);
        } else if (area.createTextRange) {
            var range = area.createTextRange();
            range.collapse(true);
            range.moveEnd('character', initialContent.length);
            range.moveStart('character', initialContent.length);
            range.select();
        }
	}
	
	function updateCharacterCount() {
		var count = twttr.txt.getTweetLength(textArea.val());
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
	
}