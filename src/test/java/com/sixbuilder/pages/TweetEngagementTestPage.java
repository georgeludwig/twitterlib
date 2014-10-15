package com.sixbuilder.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import com.sixbuilder.twitterlib.services.TweetDAO;
import com.sixbuilder.twitterlib.TweetEngagementConstants;
import com.sixbuilder.twitterlib.components.TweetEngagement;
import com.sixbuilder.twitterlib.helpers.Tweet;

/**
 * A page just for testing and demonstrating the {@link TweetEngagement} component.
 */
public class TweetEngagementTestPage {
	
	@Inject
	private TweetDAO dao;
	
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
	
	void setupRender() {
		if (actions == null || actions.isEmpty()) {
			actions = new ArrayList<Tweet>(dao.getAll());
			queue = new ArrayList<Tweet>();
		}
	}

	@OnEvent(TweetEngagementConstants.ACTION_TWEETS_EVENT)
	public List<Tweet> getActionTweets() {
		return actions;
	}

	@OnEvent(TweetEngagementConstants.QUEUED_TWEETS_EVENT)
	public List<Tweet> getQueuedTweets() {
		return queue;
	}

	@OnEvent(TweetEngagementConstants.DELETE_TWEET_EVENT)
	public void delete(Tweet tweet) {
		tweet.setDeleteQueued(true);
		dao.update(tweet);
		alertManager.success(String.format("Message with id %s was successfully deleted", tweet.getId()));
	}

	@OnEvent(TweetEngagementConstants.CLEAR_TWEET_EVENT)
	public void clear(Tweet tweet) {
		actions.remove(tweet);
		alertManager.success(String.format("Message with id %s was successfully cleared", tweet.getId()));
	}

	@OnEvent(TweetEngagementConstants.FOLLOW_TWEET_EVENT)
	public void follow(Tweet tweet) {
		tweet.setFollowQueued(true);
		dao.update(tweet);
		alertManager.success(String.format("Message with id %s was successfully followed", tweet.getId()));
	}
	
	@OnEvent(TweetEngagementConstants.FAVORITE_TWEET_EVENT)
	public void favorite(Tweet tweet) {
		tweet.setFavoriteQueued(true);
		dao.update(tweet);
		alertManager.success(String.format("Message with id %s was successfully favorited", tweet.getId()));
	}

	@OnEvent(TweetEngagementConstants.RETWEET_TWEET_EVENT)
	public void retweet(Tweet tweet) {
		tweet.setRetweetQueued(true);
		dao.update(tweet);
		alertManager.success(String.format("Message with id %s was successfully retweeted", tweet.getId()));
	}
	
	@OnEvent(TweetEngagementConstants.REPLY_TWEET_EVENT)
	public void reply(Tweet tweet, String content) {
		tweet.setReplyQueued(true);
		dao.update(tweet);
		alertManager.success(String.format("Message with id %s was successfully replied: %s", tweet.getId(), content));
	}

	@OnEvent(TweetEngagementConstants.REPLY_ALL_TWEET_EVENT)
	public void replyAll(Tweet tweet, String content) {
		tweet.setReplyAllQueued(true);
		dao.update(tweet);
		alertManager.success(String.format("Message with id %s was successfully replied all: %s", tweet.getId(), content));
	}

	@OnEvent(TweetEngagementConstants.LOAD_TWEET_EVENT)
	public Tweet load(String id) {
		return dao.findById(id);
	}
	
}