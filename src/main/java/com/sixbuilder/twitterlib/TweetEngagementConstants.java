package com.sixbuilder.twitterlib;

import com.sixbuilder.twitterlib.components.RecommendedTweet;
import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * Class that holds constants related to the Tweet Engagement component.
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
final public class TweetEngagementConstants {

	private TweetEngagementConstants(){}
	
	/**
	 * Name of the event triggered by {@link RecommendedTweet} when the user requests a tweet
	 * to be deleted. The event context will be the {@link TweetItem} to be removed.
	 */
	public static final String DELETE_TWEET_EVENT = "deleteTweet";

}
