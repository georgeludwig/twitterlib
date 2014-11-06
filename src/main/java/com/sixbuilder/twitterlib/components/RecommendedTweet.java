package com.sixbuilder.twitterlib.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import com.georgeludwigtech.common.util.CompressionUtil;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;

/**
 * Component that renders a tweet to be curated or published, plus triggers some events.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
//@Import(stylesheet={"common.css", "RecommendedTweet.css"}, library={"RecommendedTweet.js", "twitter-text-1.8.0.min.js"})
@Import(library={"jqB64.js", "RecommendedTweet.js", "twitter-text-1.8.0.min.js"})
@Events({
	RecommendedTweetConstants.PUBLISH_TWEET_EVENT, 
	RecommendedTweetConstants.DELETE_TWEET_EVENT,
	RecommendedTweetConstants.SHORTEN_URL_EVENT, 
	RecommendedTweetConstants.SAVE_TWEET_EVENT,
	RecommendedTweetConstants.LOAD_TWEET_EVENT,
	RecommendedTweetConstants.MEH_TWEET_EVENT})
public class RecommendedTweet implements ClientElement {

	@Parameter(required = true, allowNull = false)
	@Property
	private TweetItem tweet;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@Property
	private String summary;
	
	@Property
	private boolean attachSnapshot;
	
	private String clientId;
	
	private boolean wasMehButtonClicked;
	
	@SuppressWarnings("unchecked")
	public List<String> getRecommendedHashtags() {
		final String hashtagsAsSingleString = tweet.getRecommendedHashtags();
		List<String> hashtags;
		if (hashtagsAsSingleString != null) {
			hashtags = new ArrayList<String>();
			final String[] strings = hashtagsAsSingleString.split("[\\s]+");
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
		summary = tweet.getSummary();
		attachSnapshot = tweet.isAttachSnapshot();
		clientId = javaScriptSupport.allocateClientId(resources);
		JSONObject options = new JSONObject();
		options.put("id", clientId);
		options.put("publishUrl", resources.createEventLink("publish", tweet.getTweetId()).toAbsoluteURI());
		options.put("shortenUrlUrl", resources.createEventLink("shortenUrl", "6BUILDERTOKEN").toAbsoluteURI());
		//options.put("shortenUrlUrl", resources.createEventLink("shortenUrl", tweet.getTweetId()).toAbsoluteURI());
		//Link linky=resources.createEventLink("shortenUrl", "6BUILDERTOKEN");
		//String l=linky.toAbsoluteURI();
		//options.put("shortenUrlUrl", l);
		//String dud="initializeRecommendedTweet(%s);";
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
	
	void onSelectedFromMeh() {
		wasMehButtonClicked = true;
	}
	
	void onSelectedFromQueue() {
		wasMehButtonClicked = false;
	}

	/**
	 * Handles the queue and meh buttons
	 */
	public Object onSuccess(String id) {
		final TweetItem item = findById(id);
		item.setSummary(summary);
		item.setAttachSnapshot(attachSnapshot);
		if (wasMehButtonClicked) {
			return meh(item);
		} else {
			return queue(item);
		}
	}
	
	private Object queue(TweetItem item) {
		item.setPublish(true);
		// 
		return triggerEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT, item);
	}
	
	private Object meh(TweetItem item) {	
		item.setPublish(false);
		return triggerEvent(RecommendedTweetConstants.MEH_TWEET_EVENT, item);
	}
	
	/**
	 * Handles the shorten URL event.
	 */
	public JSONObject onShortenUrl(String id) {
//		boolean dud=true;
//		String s=null;
//		try {
//			s=(String) CompressionUtil.inflateObjectFromB64(id);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		StringBuilder b = new StringBuilder();
		for(int i=0;i<id.length();i=i+16) {
			// get next char string
			String s=id.substring(i,i+16);
			char c=(char)Integer.parseInt(s, 2);
			b.append(c);
		}
		String url=b.toString();
		TweetItem dud;
		try {
			dud = new TweetItem();
			dud.setUrl(url);
			String shortUrl = (String) triggerEvent(RecommendedTweetConstants.SHORTEN_URL_EVENT, dud);
			return new JSONObject("url", shortUrl);
		} catch (Exception e) {
			return new JSONObject("url", url);
		}
		
	}
	
	private Object triggerEvent(final String event, final String id) {
		return triggerEvent(event, findById(id));
	}

	private Object triggerEvent(final String event, final TweetItem item) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, new Object[]{item}, callback);
		return callback.getResult();
	}
	
	private TweetItem findById(String id) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(RecommendedTweetConstants.LOAD_TWEET_EVENT, new Object[]{id}, callback);
		return (TweetItem) callback.getResult();
	}
	
	public boolean isPublish() {
		return tweet.isPublish();
	}
	
}
