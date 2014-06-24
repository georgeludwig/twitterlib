package com.sixbuilder.twitterlib.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import com.sixbuilder.twitterlib.TweetEngagementConstants;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.helpers.Tweet;

/**
 * Component that renders a tweet or its conversation.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
@Import(stylesheet={"common.css", "TweetEngagement.css"}, library={"TweetEngagement.js", "twitter-text-1.8.0.min.js"})
@Events({TweetEngagementConstants.CLEAR_TWEET_EVENT,
		TweetEngagementConstants.DELETE_TWEET_EVENT,
		TweetEngagementConstants.FAVORITE_TWEET_EVENT,
		TweetEngagementConstants.FOLLOW_TWEET_EVENT,
		TweetEngagementConstants.LIST_TWEET_EVENT,
		TweetEngagementConstants.LOAD_TWEET_EVENT,
		TweetEngagementConstants.REPLY_ALL_TWEET_EVENT,
		TweetEngagementConstants.REPLY_TWEET_EVENT,
		TweetEngagementConstants.RETWEET_TWEET_EVENT})
public class TweetEngagement implements ClientElement {
	
	/**
	 * Name of the event triggered by {@link RenderTweet} when the user clicks
	 * one of the conversation icons. Internal use only.
	 */
	public static final String SHOW_CONVERSATION = "showConversation";

	/**
	 * Name of the event triggered by {@link RenderTweet} when the user replys or replys-all
	 */
	public static final String REPLY = "reply";

	@Parameter(required = true, allowNull = false)
	@Property
	private Tweet tweet;
	
	@Property
	private Tweet conversationTweet;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@Inject
	private Block singleTweet;

	@Inject
	private Block conversation;
	
	@InjectComponent
	private Zone zone;
	
	private String zoneId;

	@Property
	private boolean showConversation;
	
	private String clientId;
	
	public void setupRender() {
		clientId = javaScriptSupport.allocateClientId(resources);
		zoneId = null;
		getZoneId();
	}
	
	public Object onReply(Tweet tweet, String event, String replyContent, String clientId) {
		this.clientId = clientId;
		this.tweet = tweet;
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, new Object[]{tweet, replyContent}, callback);
		return zone.getBody();		
	}
	
	public Object onShowConversation(String id, boolean showConversation, String zoneId) {
		tweet = findById(id);
		this.showConversation = showConversation;
		this.zoneId = zoneId;
		return zone.getBody();
	}
	
	public String getClientId() {
		return clientId;
	}

	public String getZoneId() {
		if (zoneId == null) {
			zoneId = getClientId() + "-conversation-zone";
		}
		return zoneId;
	}

	public Block getBlock() {
		return showConversation ? conversation : singleTweet; 
	}
	
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
	
	private Tweet findById(String id) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(TweetEngagementConstants.LOAD_TWEET_EVENT, new Object[]{id}, callback);
		return (Tweet) callback.getResult();
	}

	/**
	 * Returns the value of the zone field.
	 * @return a {@link Zone}.
	 */
	public Zone getZone() {
		return zone;
	}
	
	public boolean isInsideConversation() {
		return showConversation && tweet != conversationTweet;
	}
	
}
