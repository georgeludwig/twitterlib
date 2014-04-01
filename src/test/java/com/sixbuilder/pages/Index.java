package com.sixbuilder.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import com.sixbuilder.services.TweetItemDAO;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * Start page of application twitterlib.
 */
public class Index {
	
//	@Inject
//	private TweetItemDAO tweetItemDAO;
//	
//	@InjectComponent
//	private Zone curateZone;
//	
//	@InjectComponent
//	private Zone publishingZone;
//	
//	@InjectComponent
//	private Zone publishedZone;
//
//	@Persist
//	@Property
//	private List<TweetItem> curating;
//
//	@Persist
//	@Property
//	private List<TweetItem> publishing;
//
//	@Persist
//	@Property
//	private List<TweetItem> published;
//	
//	@Property
//	private TweetItem tweet;
//
//	@Inject
//	private AjaxResponseRenderer ajaxResponseRenderer;
//	
//	@Inject
//	private AlertManager alertManager;
//	
//	void setupRender() {
//		if (curating == null || curating.isEmpty()) {
//			curating = new ArrayList<TweetItem>(getTweetItems());
//			publishing = new ArrayList<TweetItem>();
//			published = new ArrayList<TweetItem>();
//		}
//	}
//
//	public List<TweetItem> getTweetItems() {
//		return tweetItemDAO.getAll();
//	}
//	
//	@OnEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT)
//	public void delete(TweetItem tweetItem) {
//		curating.remove(tweetItem);
//		publishing.remove(tweetItem);
//		tweetItemDAO.delete(tweetItem);
//		alertManager.success(String.format("Message with id %s was successfully deleted", tweetItem.getTweetId()));
//		ajaxResponseRenderer.addRender(curateZone);
//		ajaxResponseRenderer.addRender(publishingZone);
//	}
//
//	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
//	public void publish(TweetItem tweetItem) {
//		curating.remove(tweetItem);
//		publishing.add(tweetItem);
//		tweetItem.setPublish(true);
//		tweetItemDAO.update(tweetItem);
//		alertManager.success(String.format("Message with id %s was successfully selected to be published", tweetItem.getTweetId()));
//		ajaxResponseRenderer.addRender(curateZone);
//		ajaxResponseRenderer.addRender(publishingZone);
//	}
//	
//	@OnEvent(RecommendedTweetConstants.SHORTEN_URL_EVENT)
//	public TweetItem shortenUrl(TweetItem tweetItem) {
//		tweetItem.setShortenedUrl(shortenUrlUsingBitly(tweetItem.getUrl()));
//		tweetItemDAO.update(tweetItem);
//		return tweetItem;
//	}
//	
//	@OnEvent(RecommendedTweetConstants.SAVE_TWEET_EVENT)
//	public void save(TweetItem tweetItem) {
//		tweetItemDAO.update(tweetItem);
//	}
//	
//	@OnEvent(RecommendedTweetConstants.SHORTEN_URL_EVENT)
//	
//	private String shortenUrlUsingBitly(String url) {
//		return "http://bitly/tweet";
//	}

}
