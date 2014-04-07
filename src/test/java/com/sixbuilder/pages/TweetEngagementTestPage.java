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

import com.sixbuilder.services.TweetDAO;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.components.RecommendedTweet;
import com.sixbuilder.twitterlib.helpers.Tweet;

/**
 * A page just for testing the {@link RecommendedTweet} component.
 */
public class TweetEngagementTestPage {
	
	@Inject
	private TweetDAO dao;
	
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
	
	void setupRender() {
		if (actions == null || actions.isEmpty()) {
			actions = new ArrayList<Tweet>(dao.getAll());
			queue = new ArrayList<Tweet>();
		}
	}

	public List<Tweet> gettweets() {
		return dao.getAll();
	}
	
	@OnEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT)
	public void delete(Tweet tweet) {
		actions.remove(tweet);
		queue.remove(tweet);
		dao.delete(tweet);
		alertManager.success(String.format("Message with id %s was successfully deleted", tweet.getId()));
		ajaxResponseRenderer.addRender(actionZone);
		ajaxResponseRenderer.addRender(queueZone);
	}

	@OnEvent(RecommendedTweetConstants.SAVE_TWEET_EVENT)
	public void save(Tweet tweet) {
		dao.update(tweet);
	}
	
	@OnEvent(RecommendedTweetConstants.LOAD_TWEET_EVENT)
	public Tweet load(String id) {
		return dao.findById(id);
	}
	
}