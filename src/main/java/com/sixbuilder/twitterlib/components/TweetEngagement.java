package com.sixbuilder.twitterlib.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import com.sixbuilder.twitterlib.TweetEngagementConstants;
import com.sixbuilder.twitterlib.helpers.Action;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.helpers.Tweet;

/**
 * Component that renders an actual, existing tweet with many links that trigger actions on them.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
@Import(stylesheet={"common.css", "TweetEngagement.css"}, library={"TweetEngagement.js"})
@Events(value = {})
public class TweetEngagement implements ClientElement {
	
	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user clicks
	 * the conversation icon. 
	 */
	private static final String SHOW_CONVERSATION_EVENT = "showConversation";

	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user clicks
	 * one of the conversation icons. Internal use only.
	 */
	private static final String ACTION_EVENT = "action";

	@Parameter(required = true, allowNull = false, name = "tweet")
	@Property
	private Tweet tweetParameter;
	
	@Property
	private Tweet tweet;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@Property
	private String conversationTweet;
	
	@Inject
	private Block singleTweet;

	@Inject
	private Block conversation;
	
	@InjectComponent
	private Zone zone;

	@InjectComponent
	private Zone actionsZone;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	private boolean showConversation;
	
	@Property
	private Action action;
	
	private String clientId;
	
	void setupRender() {
		tweet = tweetParameter;
		clientId = javaScriptSupport.allocateClientId(resources);
		JSONObject options = new JSONObject();
		options.put("eventUrl", getEventLink().toAbsoluteURI());
		javaScriptSupport.addScript(String.format("initializeTweetEngagement(%s);", options)); 
	}
	
	public Link getEventLink() {
		return resources.createEventLink(ACTION_EVENT);
	}
	
	@OnEvent(ACTION_EVENT)
	public Object handleActionLinks(
			@RequestParameter("action") String actionName, 
			@RequestParameter("id") String tweetId,
			@RequestParameter("content") String content) {
		
		final Action action = Action.valueOf(actionName);
		return triggerEvent(action.getEventName(), tweetId, content);
	}
	
	private Object triggerEvent(final String event, final String id, final String content) {
		return triggerEvent(event, findById(id), content);
	}

	private Object triggerEvent(final String event, final Tweet item, String content) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, new Object[]{item, content}, callback);
		return callback.getResult();
	}
	
	private Tweet findById(String id) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(TweetEngagementConstants.LOAD_TWEET_EVENT, new Object[]{id}, callback);
		return (Tweet) callback.getResult();
	}

	public Action[] getActions() {
		return Action.values();
	}

	public String getZoneId() {
		return getClientId() + "-conversation-zone";
	}

	public String getActionsZoneId() {
		return getClientId() + "-actions-zone";
	}

	public String getClientId() {
		return "tweet-" + tweet.getId();
	}
	
	/**
	 * Returns the conversation in which this tweet takes part.
	 * FIXME: add main tweet to list.
	 * @return
	 */
	public List<Tweet> getConversationTweets() {
		List<Tweet> conversation = new ArrayList<Tweet>(tweet.getConversation());
		if (!conversation.contains(tweet)) {
			int insertionIndex = -1;
			for (int i = 0; i < conversation.size(); i++) {
				if (tweet.getPosted().before(conversation.get(i).getPosted())) {
					insertionIndex = i;
					break;
				}
			}
			conversation.add(insertionIndex, tweet);
		}
		return conversation;
	}
	
	public String getCssClass() {
		return tweet.equals(conversationTweet) ? "main" : "other";
	}

	public String getActionCssClass() {
		return action.getCssClass(tweet) + " " + action.getState(tweet).getColor();
	}

	public Block getBlock() {
		return showConversation ? conversation : singleTweet; 
	}
	
	@OnEvent()
	void showConversation() {
		ajaxResponseRenderer.addRender(zone);
	}
	
}
