package com.sixbuilder.twitterlib.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import com.sixbuilder.twitterlib.helpers.Tweet;

/**
 * Component that renders an actual, existing tweet with many links that trigger actions on them.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
@Import(stylesheet={"common.css", "TweetEngagement.css"}, library={"TweetEngagement.js"})
@Events(value = {})
public class TweetEngagement implements ClientElement{

	@Parameter(required = true, allowNull = false)
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
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	private boolean showConversation;

	public String getZoneId() {
		return getClientId() + "-conversation-zone";
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
	
	public Block getBlock() {
		return showConversation ? conversation : singleTweet; 
	}
	
	@OnEvent()
	void showConversation() {
		ajaxResponseRenderer.addRender(zone);
	}
	
}
