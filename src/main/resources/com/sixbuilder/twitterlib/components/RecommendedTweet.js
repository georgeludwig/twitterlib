function initializeRecommendedTweet(options) {

	var SUMMARY_MODE = 'summary';
	var DETAIL_MODE = 'detail';
	var MODE_ATTRIBUTE = 'data-mode';
	
	var outerDiv = $('#' + options.id);
	
	// publish checkbox
	T5.initializers.updateZoneOnEvent('click', outerDiv.find('input.publishTweet').attr('id'), '^', options.publishUrl);
	
	
	var shortenUrlButton = outerDiv.find('input.tweetShortenUrl');
	var textarea = outerDiv.find('div.tweetText textarea');
	var summaryText = outerDiv.find('div.tweetText p');
	
	outerDiv.find('input.tweetCancel').click(function(event) {
		modeSummary();
		event.preventDefault();
	});
	
	outerDiv.find('input.tweetShortenUrl').click(function(event) {
		$.ajax(options.shortenUrlUrl).done(function(result) {
			var shortenedUrl = result.url;
			var newSummary = textarea.text() + ' ' + shortenedUrl; 
			textarea.text(newSummary);
			summaryText.text(newSummary);
			shortenUrlButton[0].disabled = true;
		});
		event.preventDefault();
	});
	
	outerDiv.find('input.tweetSave').click(function(event) {
		console.log('textarea: ' + textarea.text());
		$.ajax(options.saveUrl, { data : { summary : textarea.text() } }).done(function(result) {
			// FIXME: what to do now? nothing? show some confirmation?
			modeSummary();
		});
		event.preventDefault();
	});
	
	// switch to detail view
	var summaryView = outerDiv.find('div.tweetSummaryColumn');
	summaryView.click(function() {
	
		if (outerDiv.attr(MODE_ATTRIBUTE) === SUMMARY_MODE) {
		
			// expand into detail mode
			outerDiv.attr(MODE_ATTRIBUTE, DETAIL_MODE);
		}
		
		// else ignore
		
	});
	
	function modeSummary() {
		outerDiv.attr(MODE_ATTRIBUTE, SUMMARY_MODE);
	}
	
	function modeDetail() {
		outerDiv.attr(MODE_ATTRIBUTE, DETAIL_MODE);
	}
	
}