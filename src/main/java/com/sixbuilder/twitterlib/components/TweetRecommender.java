package com.sixbuilder.twitterlib.components;

import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.services.TweetItemDAO;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Component that renders a tweet to be curated or published, plus triggers some events.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
//@Import(stylesheet="RecommendedTweet.css", library="RecommendedTweet.js")
//@Events({RecommendedTweetConstants.PUBLISH_TWEET_EVENT, RecommendedTweetConstants.DELETE_TWEET_EVENT,
//	RecommendedTweetConstants.SHORTEN_URL_EVENT, RecommendedTweetConstants.SAVE_TWEET_EVENT})
public class TweetRecommender {

	@Parameter(required = true, allowNull = false)
	private File accountsRoot;
	
	@Parameter(required = true, allowNull = false)
	@Property
	private String userId;
	
	@Inject
	private TweetItemDAO tweetItemDAO;
	
	@InjectComponent
	private Zone curateZone;
	
	@InjectComponent
	private Zone publishingZone;
	
	@Persist
	@Property
	private List<TweetItem> curating;

	@Persist
	@Property
	private List<TweetItem> publishing;

	@Persist
	@Property
	private List<TweetItem> published;
	
	@Property
	private TweetItem tweet;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
	@Inject
	private AlertManager alertManager;
	
	void setupRender() throws Exception {
		if (curating == null || curating.isEmpty()) {
			curating = new ArrayList<TweetItem>(getTweetItems());
			publishing = new ArrayList<TweetItem>();
		}
	}

	public List<TweetItem> getTweetItems() throws Exception {
		return tweetItemDAO.getAll(accountsRoot,userId);
	}
	
	@OnEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT)
	public void delete(TweetItem tweetItem) throws Exception {
		curating.remove(tweetItem);
		publishing.remove(tweetItem);
		tweetItemDAO.delete(accountsRoot,userId,tweetItem);
		alertManager.success(String.format("Message with id %s was successfully deleted", tweetItem.getTweetId()));
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}

	@OnEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT)
	public void publish(TweetItem tweetItem) throws Exception {
		curating.remove(tweetItem);
		publishing.add(tweetItem);
		tweetItem.setPublish(true);
		tweetItemDAO.update(accountsRoot,userId,tweetItem);
		alertManager.success(String.format("Message with id %s was successfully selected to be published", tweetItem.getTweetId()));
		ajaxResponseRenderer.addRender(curateZone);
		ajaxResponseRenderer.addRender(publishingZone);
	}
	
	@OnEvent(RecommendedTweetConstants.SAVE_TWEET_EVENT)
	public void save(TweetItem tweetItem) throws Exception {
		tweetItemDAO.update(accountsRoot,userId,tweetItem);
	}
	
}
