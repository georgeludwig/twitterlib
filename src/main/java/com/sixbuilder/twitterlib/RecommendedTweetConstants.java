package com.sixbuilder.twitterlib;

import com.sixbuilder.twitterlib.components.RecommendedTweet;
import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * Class that holds constants related to the Recommended Tweets component.
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
final public class RecommendedTweetConstants {

	private RecommendedTweetConstants(){}
	
	/**
	 * Name of the event triggered by {@link RecommendedTweet} when the user requests a tweet
	 * to be deleted. The event context will be the {@link TweetItem} to be removed.
	 */
	public static final String DELETE_TWEET_EVENT = "deleteTweet";

	/**
	 * Name of the event triggered by {@link RecommendedTweet} when the user requests a tweet
	 * to be published. The event context will be the {@link TweetItem} to be published.
	 */
	public static final String PUBLISH_TWEET_EVENT = "publishTweet";

	/**
	 * Name of the event triggered by {@link RecommendedTweet} when the user requests an URL
	 * to be shortened. The event context will be the tweet item to have its URL shortened.
	 * The event handler must return the tweet item with its "shortenedUrl" attribute
	 * set, as it's its value which will be sent to the page.
	 */
	public static final String SHORTEN_URL_EVENT = "shortenTweetUrl";
	
	/**
	 * Name of the event triggered by {@link RecommendedTweet} when the user clicks on the Save button.
	 * The event context will be the tweet item. 
	 */
	public static final String SAVE_TWEET_EVENT = "saveTweet";

	/**
	 * Name of the event triggered by {@link RecommendedTweet} when it needs to load a {@link TweetItem}
	 * based on its id. The event context will be the tweet id and the handler method must
	 * return a {@link TweetItem} instance. 
	 */
	public static final String LOAD_TWEET_EVENT = "loadTweet";

}
