package com.sixbuilder.twitterlib;

import com.sixbuilder.twitterlib.components.TweetEngagement;
import com.sixbuilder.twitterlib.helpers.Tweet;

/**
 * Class that holds constants related to the Tweet Engagement component.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
final public class TweetEngagementConstants {

	private TweetEngagementConstants() {
	}

	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user requests a tweet
	 * to be favorited. The event context will be the {@link Tweet}.
	 */
	public static final String FAVORITE_TWEET_EVENT = "favoriteTweet";

	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user requests a tweet
	 * to be retwitted. The event context will be the {@link Tweet}.
	 */
	public static final String LIST_TWEET_EVENT = "listTweet";

	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user requests a tweet
	 * to be retwitted. The event context will be the {@link Tweet}.
	 */
	public static final String RETWEET_TWEET_EVENT = "retwitTweet";

	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user requests a tweet
	 * to be replied all. The event context will be the {@link Tweet}.
	 */
	public static final String REPLY_ALL_TWEET_EVENT = "replyAllTweet";

	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user requests a tweet
	 * to be replied. The event context will be the {@link Tweet}.
	 */
	public static final String REPLY_TWEET_EVENT = "replyTweet";

	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user requests a tweet
	 * author to be followed. The event context will be the {@link Tweet}.
	 */
	public static final String FOLLOW_TWEET_EVENT = "deleteTweet";

	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user requests a tweet
	 * to be deleted. The event context will be the {@link Tweet} to be removed.
	 */
	public static final String DELETE_TWEET_EVENT = "deleteTweet";

	/**
	 * Name of the event triggered by {@link TweetEngagement} when the user clicks on the Save button.
	 * The event context will be the tweet item. 
	 */
	public static final String SAVE_TWEET_EVENT = "saveTweet";

	/**
	 * Name of the event triggered by {@link TweetEngagement}
	 */
	public static final String LOAD_TWEET_EVENT = "loadTweet";

}
