package com.sixbuilder.twitterlib.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import com.sixbuilder.services.TweetItemDAO;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * Component that renders a tweet to be curated or published, plus triggers some events.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
@Import(stylesheet="RecommendedTweet.css", library={"RecommendedTweet.js", "twitter-text-1.8.0.min.js"})
@Events({RecommendedTweetConstants.PUBLISH_TWEET_EVENT, RecommendedTweetConstants.DELETE_TWEET_EVENT,
	RecommendedTweetConstants.SHORTEN_URL_EVENT, RecommendedTweetConstants.SAVE_TWEET_EVENT})
public class RecommendedTweet implements ClientElement {

	@Parameter(required = true, allowNull = false)
	@Property
	private TweetItem tweet;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private TweetItemDAO dao;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	private String clientId;
	
	@SuppressWarnings("unchecked")
	public List<String> getRecommendedHashtags() {
		final String hashtagsAsSingleString = tweet.getRecommendedHashtags();
		List<String> hashtags;
		if (hashtagsAsSingleString != null) {
			hashtags = new ArrayList<String>();
			final String[] strings = hashtagsAsSingleString.split("[\\d]+");
			for (String string : strings) {
				string = string.trim();
				if (string.length() > 0) {
					hashtags.add(string);
				}
			}
		}
		else {
			hashtags = Collections.EMPTY_LIST;
		}
		return hashtags;
	}
	
	void setupRender() {
		clientId = javaScriptSupport.allocateClientId(resources);
		JSONObject options = new JSONObject();
		options.put("id", clientId);
		options.put("publishUrl", resources.createEventLink("publish", tweet.getTweetId()).toAbsoluteURI());
		options.put("shortenUrlUrl", resources.createEventLink("shortenUrl", tweet.getTweetId()).toAbsoluteURI());
		options.put("saveUrl", resources.createEventLink("save", tweet.getTweetId()).toAbsoluteURI());
		javaScriptSupport.addScript(String.format("initializeRecommendedTweet(%s);", options)); 
	}
	/**
	 * Returns the value of the clientId field.
	 * @return a {@link String}.
	 */
	public String getClientId() {
		return clientId;
	}
	
	/**
	 * Handles the delete event.
	 */
	public Object onDelete(String id) {
		return triggerEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT, id);
	}

	/**
	 * Handles the save event.
	 */
	public Object onSave(
			String id, 
			@RequestParameter("summary") String summary,
			@RequestParameter("attachSnapshot") boolean attachSnapshot) {
		final TweetItem item = dao.findById(id);
		item.setSummary(summary);
		item.setAttachSnapshot(attachSnapshot);
		return triggerEvent(RecommendedTweetConstants.SAVE_TWEET_EVENT, item);
	}

	/**
	 * Handles the publish event
	 */
	public Object onPublish(String id) {
		return triggerEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT, id);
	}
	
	/**
	 * Handles the shorten URL event.
	 */
	public JSONObject onShortenUrl(String id) {
		TweetItem tweetItem = (TweetItem) triggerEvent(RecommendedTweetConstants.SHORTEN_URL_EVENT, id);
		return new JSONObject("url", tweetItem.getShortenedUrl());
	}
	
	private Object triggerEvent(final String event, final String id) {
		return triggerEvent(event, dao.findById(id));
	}

	private Object triggerEvent(final String event, final TweetItem item) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, new Object[]{item}, callback);
		return callback.getResult();
	}
	
}
