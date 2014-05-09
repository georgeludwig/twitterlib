package com.sixbuilder.twitterlib.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import com.sixbuilder.twitterlib.TweetEngagementConstants;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.helpers.Tweet;

/**
 * Component that shows the tweet actions and queue. 
 */
@Events({
	TweetEngagementConstants.ACTION_TWEETS_EVENT,
	TweetEngagementConstants.QUEUED_TWEETS_EVENT,
	TweetEngagementConstants.CLEAR_TWEET_EVENT,
	TweetEngagementConstants.DELETE_TWEET_EVENT,
	TweetEngagementConstants.FAVORITE_TWEET_EVENT,
	TweetEngagementConstants.FOLLOW_TWEET_EVENT,
	TweetEngagementConstants.LIST_TWEET_EVENT,
	TweetEngagementConstants.LOAD_TWEET_EVENT,
	TweetEngagementConstants.REPLY_ALL_TWEET_EVENT,
	TweetEngagementConstants.REPLY_TWEET_EVENT,
	TweetEngagementConstants.RETWEET_TWEET_EVENT})
public class TweetEngagementDisplay {
	
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	@InjectComponent
	private Zone actionZone;
	
	@InjectComponent
	private Zone queueZone;
	
	@Persist
	@Property
	private List<Tweet> actions;
	
	@Persist
	@Property
	private List<Tweet> queue;

	@Property
	private Tweet tweet;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
	@Inject
	private AlertManager alertManager;
	
	@Inject
	private ComponentResources resources;
	
	void setupRender() {
		if (actions == null || actions.isEmpty()) {
			actions = new ArrayList<Tweet>(getActionTweets());
			queue = new ArrayList<Tweet>(getQueuedTweets());
		}
	}

	@SuppressWarnings("unchecked")
	@Cached
	public List<Tweet> getActionTweets() {
		return (List<Tweet>) triggerEvent(TweetEngagementConstants.ACTION_TWEETS_EVENT);
	}
	
	@SuppressWarnings("unchecked")
	@Cached
	public List<Tweet> getQueuedTweets() {
		return (List<Tweet>) triggerEvent(TweetEngagementConstants.QUEUED_TWEETS_EVENT);
	}
	
	private Object triggerEvent(final String event) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, EMPTY_OBJECT_ARRAY, callback);
		return callback.getResult();
	}
	
}