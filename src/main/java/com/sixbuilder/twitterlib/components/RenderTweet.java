package com.sixbuilder.twitterlib.components;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.helpers.Action;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.helpers.Tweet;
import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * Component used internally by {@link TweetEngagement} to render a single tweet with all actions.
 * This exists so {@link TweetEngagement} can render all the tweets in a conversation,
 * just showing the main one and hiding the others and just reviewing these when needed.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
@Import(stylesheet={"common.css", "TweetEngagement.css"}, library={"TweetEngagement.js"})
public class RenderTweet implements ClientElement {
	
	@Parameter(required = true, allowNull = false)
	@Property
	private Tweet tweet;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@Property
	private Tweet conversationTweet;
	
	@Property
	private Action action;
	
	private String clientId;
	
	void setupRender() {
		clientId = javaScriptSupport.allocateClientId(resources);
		JSONObject options = new JSONObject();
		options.put("id", clientId);
		final String id = tweet.getId();
		options.put("publishUrl", resources.createEventLink("publish", id).toAbsoluteURI());
		options.put("shortenUrlUrl", resources.createEventLink("shortenUrl", id).toAbsoluteURI());
		options.put("saveUrl", resources.createEventLink("save", id).toAbsoluteURI());
		javaScriptSupport.addScript(String.format("initializeRecommendedTweet(%s);", options)); 
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
		final TweetItem item = findById(id);
		item.setSummary(summary);
		item.setAttachSnapshot(attachSnapshot);
		return triggerEvent(RecommendedTweetConstants.SAVE_TWEET_EVENT, item);
	}

	/**
	 * Handles the publish event
	 */
	public Object onPublish(String id) {
		final TweetItem item = findById(id);
		item.setPublish(!item.isPublish());
		return triggerEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT, item);
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

	public String getClientId() {
		return clientId;
	}
	
	public Action[] getActions() {
		return Action.values();
	}

}
