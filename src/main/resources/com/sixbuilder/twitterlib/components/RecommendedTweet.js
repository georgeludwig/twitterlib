function initializeRecommendedTweet(options) {

	var SUMMARY_MODE = 'summary';
	var DETAIL_MODE = 'detail';
	var MODE_ATTRIBUTE = 'data-mode';
	var DIRTY_ATTRIBUTE = 'data-dirty';
	
	var outerDiv = $('#' + options.id);
	
	var publishCheckbox = outerDiv.find('input.publishTweet');
	var shortenUrlButton = outerDiv.find('input.tweetShortenUrl');
	var textarea = outerDiv.find('div.tweetText textarea');
	var summaryText = outerDiv.find('div.tweetText p');
	var characterCount = outerDiv.find('.tweetCharacterCount');
	var summaryView = outerDiv.find('div.tweetSummaryColumn');
	var hashtags = outerDiv.find('ul.tweetSuggestedHashtags li');
	
	// publish checkbox
	T5.initializers.updateZoneOnEvent('click', publishCheckbox.attr('id'), '^', options.publishUrl);
	
	outerDiv.find('input.tweetCancel').click(function(event) {
		modeSummary();
		enablePublishCheckbox();
		event.preventDefault();
	});
	
	outerDiv.find('input.tweetShortenUrl').click(function(event) {
		$.ajax(options.shortenUrlUrl).done(function(result) {
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
		save();
		event.preventDefault();
	});
	
	// switch to detail view
	summaryView.click(function(event) {
		if (outerDiv.attr(MODE_ATTRIBUTE) === SUMMARY_MODE) {
			// expand into detail mode
			outerDiv.attr(MODE_ATTRIBUTE, DETAIL_MODE);
			disablePublishCheckbox();
		}
		event.preventDefault();
	});
	
	// character count
	textarea.keyup(handleSummaryChange);
	
	hashtags.click(function(event) {
		var hashtag = $(this).text();
		var summary = textarea.val().trim();
		if (summary.indexOf(hashtag) < 0) {
			summary = summary + ' ' + hashtag;
			textarea.val(summary);
			handleSummaryChange();
		}
		event.preventDefault();
	});
	
	function save() {
		handleSummaryChange();
		$.ajax(options.saveUrl, { data : { summary : textarea.text() } }).done(function(result) {
			// FIXME: what to do now? nothing? show some confirmation?
			modeSummary();
			enablePublishCheckbox();
		});
	}
	
	function handleSummaryChange() {
		publishCheckbox[0].disabled = true;
		summaryText.text(textarea.val());
		characterCount.text(textarea.val().length);
	}
	
	function modeSummary() {
		outerDiv.attr(MODE_ATTRIBUTE, SUMMARY_MODE);
	}
	
	function modeDetail() {
		outerDiv.attr(MODE_ATTRIBUTE, DETAIL_MODE);
	}
	
	function enablePublishCheckbox() {
		if (outerDiv.attr('data-publish') != 'true') {
			publishCheckbox[0].disabled = false;
		}
	}
	
	function disablePublishCheckbox() {
		publishCheckbox[0].disabled = true;
	}
	
}