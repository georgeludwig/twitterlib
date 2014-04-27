package com.sixbuilder.twitterlib.components;

import java.util.List;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
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
@Events({TweetEngagementConstants.CLEAR_TWEET_EVENT,
	TweetEngagementConstants.DELETE_TWEET_EVENT,
	TweetEngagementConstants.FAVORITE_TWEET_EVENT,
	TweetEngagementConstants.FOLLOW_TWEET_EVENT,
	TweetEngagementConstants.LIST_TWEET_EVENT,
	TweetEngagementConstants.LOAD_TWEET_EVENT,
	TweetEngagementConstants.REPLY_ALL_TWEET_EVENT,
	TweetEngagementConstants.REPLY_TWEET_EVENT,
	TweetEngagementConstants.RETWEET_TWEET_EVENT})
public class RenderTweet implements ClientElement {
	
	/**
	 * Name of the event triggered by {@link RenderTweet} when the user clicks
	 * one of the conversation icons. Internal use only.
	 */
	private static final String ACTION_EVENT = "tweetAction";

	/**
	 * {@link Tweet} to be rendered.
	 */
	@Parameter(required = true, allowNull = false)
	@Property
	private Tweet tweet;
	
	/**
	 * Id of the zone to be updated when the conversation link is clicked.
	 */
	@Parameter(required = true, allowNull = false)
	private String zoneId;
	
	/**
	 * Tells whether this rendering is inside a conversation or not.
	 */
	@Parameter
	private boolean insideConversation;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
	@InjectComponent
	private Zone actionsZone;
	
	@Inject
	private Request request;
	
	@Inject
	private Environment environment;

	@Property
	private boolean showConversation;
	
	@Property
	private Action action;
	
	@Property
	private List<Tweet> conversationTweets;
	
	@Property
	private String replyContent;

	private String clientId;
	
	void setupRender() {
		if (environment.peek(JavaScriptSupport.class) == null) {
			JavaScriptCallback callback = new JavaScriptCallback() {
				public void run(JavaScriptSupport javascriptSupport) {
					setupJavaScript(javascriptSupport);
				}
			};
			ajaxResponseRenderer.addCallback(callback);
		}
		else {
			clientId = javaScriptSupport.allocateClientId(resources);
			setupJavaScript(javaScriptSupport);
		}
	}

	private JSONObject setupJavaScript(JavaScriptSupport javaScriptSupport) {
		final JSONObject options = new JSONObject();
		options.put("id", clientId);
		options.put("actionUrl", getEventLink().toAbsoluteURI());
		javaScriptSupport.addScript(String.format("initializeTweetEngagement(%s);", options));
		return options;
	}
	
	public Link getEventLink() {
		return resources.createEventLink(ACTION_EVENT);
	}
	
	public Object onClear(String id) {
		return triggerEvent(TweetEngagementConstants.CLEAR_TWEET_EVENT, findById(id), null);
	}
	
	@OnEvent(value = EventConstants.SUCCESS, component = "reply")
	public Object handleReply(String id, @RequestParameter("replyType") String replyType) {
		tweet = findById(id);
		final String event;
		if (replyType.equalsIgnoreCase("reply")) {
			event = TweetEngagementConstants.REPLY_TWEET_EVENT;
		}
		else {
			event = TweetEngagementConstants.REPLY_ALL_TWEET_EVENT;
		}
		
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(TweetEngagement.REPLY, new Object[]{tweet, event, replyContent}, callback);
		return callback.getResult();
	}
	
	@OnEvent(ACTION_EVENT)
	public Object handleActionLinks(
			@RequestParameter("id") String tweetId,
			@RequestParameter("action") String actionName,
			@RequestParameter("clientId") String clientId,
			@RequestParameter(value = "content", allowBlank = true) String content) {

		final Object returnValue;
		final Action action = Action.valueOf(actionName);
		tweet = findById(tweetId);
		this.clientId = clientId;
		triggerEvent(action.getEventName(), tweet, content);
		switch (action) {
			case FOLLOW:
			case RETWEET:
			case FAVORITE:
			case DELETE:
				returnValue = actionsZone.getBody();
				setupRender();
				break;
			default:
				returnValue = null;
		}
		return returnValue;
	}
	
	private Object triggerEvent(final String event, final Tweet tweet, String content) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, new Object[]{tweet, content}, callback);
		return callback.getResult();
	}
	
	private Tweet findById(String id) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(TweetEngagementConstants.LOAD_TWEET_EVENT, new Object[]{id}, callback);
		return (Tweet) callback.getResult();
	}
	
	public Object onConversation(String id, boolean show) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(TweetEngagement.SHOW_CONVERSATION, new Object[]{id, show}, callback);
		return callback.getResult();
	}

	public Action[] getActions() {
		return Action.values();
	}

	public String getZoneId() {
		return zoneId;
	}

	public String getActionsZoneId() {
		return getClientId() + "-actions-zone";
	}

	public String getClientId() {
		return clientId;
	}
	
	public String getActionCssClass() {
		return action.getCssClass(tweet) + " " + action.getState(tweet).getColor();
	}
	
	public boolean isRenderConversation() {
		return !insideConversation && !tweet.getConversation().isEmpty(); 
	}

}
