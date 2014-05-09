package com.sixbuilder.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import com.sixbuilder.services.TweetItemDAO;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.components.RecommendedTweet;
import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * A page just for testing the {@link RecommendedTweet} component.
 */
public class RecommendedTweetTestPage {
	
	@Inject
	private TweetItemDAO tweetItemDAO;
	
	@Persist
	private List<TweetItem> curating;

	@Persist
	private List<TweetItem> publishing;

	@Persist
	private List<TweetItem> published;
	
	@Property
	private TweetItem tweet;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
	@Inject
	private AlertManager alertManager;
	
	void setupRender() {
		if (curating == null || curating.isEmpty()) {
			curating = new ArrayList<TweetItem>(getTweetItems());
			publishing = new ArrayList<TweetItem>();
			published = new ArrayList<TweetItem>();
		}
	}
	
	@OnEvent(RecommendedTweetConstants.CURATING_TWEETS_EVENT)
	public List<TweetItem> getCurating() {
		return curating;
	}

	@OnEvent(RecommendedTweetConstants.PUBLISHING_TWEETS_EVENT)
	public List<TweetItem> getPublishing() {
		return publishing;
	}

	@OnEvent(RecommendedTweetConstants.PUBLISHED_TWEETS_EVENT)
	public List<TweetItem> getPublished() {
		return published;
	}

	public List<TweetItem> getTweetItems() {
		return tweetItemDAO.getAll();
	}
	
	@OnEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT)
	public void delete(TweetItem tweetItem) {
		curating.remove(tweetItem);
		publishing.remove(tweetItem);
		tweetItemDAO.delete(tweetItem);
		alertManager.success(String.format("Message with id %s was successfully deleted", tweetItem.getTweetId()));
	}

	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) {
		if (tweetItem.isPublish()) {
			curating.remove(tweetItem);
			publishing.add(tweetItem);
		}
		else {
			publishing.remove(tweetItem);
			curating.add(tweetItem);
		}
		tweetItemDAO.update(tweetItem);
		alertManager.success(String.format("Message with id %s was successfully selected to be published", tweetItem.getTweetId()));
	}
	
	@OnEvent(RecommendedTweetConstants.SHORTEN_URL_EVENT)
	public TweetItem shortenUrl(TweetItem tweetItem) {
		tweetItem.setShortenedUrl(shortenUrlUsingBitly(tweetItem.getUrl()));
		tweetItemDAO.update(tweetItem);
		return tweetItem;
	}
	
	@OnEvent(RecommendedTweetConstants.SAVE_TWEET_EVENT)
	public void save(TweetItem tweetItem) {
		tweetItemDAO.update(tweetItem);
	}
	
	@OnEvent(RecommendedTweetConstants.LOAD_TWEET_EVENT)
	public TweetItem load(String id) {
		return tweetItemDAO.findById(id);
	}
	
	private String shortenUrlUsingBitly(String url) {
		return "http://bitly/tweet";
	}

}
