 package com.sixbuilder.twitterlib.components;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import com.georgeludwigtech.common.setmanager.SetItem;
import com.georgeludwigtech.common.setmanager.SetItemImpl;
import com.georgeludwigtech.common.setmanager.SetManager;
import com.georgeludwigtech.urlsnapshotserviceclient.UrlSnapshotServiceClient;
import com.georgeludwigtech.urlsnapshotserviceclient.UrlSnapshotServiceRequest;
import com.georgeludwigtech.urlsnapshotserviceclient.UrlSnapshotServiceResponse;
import com.rosaloves.bitlyj.BitlyException;
import com.rosaloves.bitlyj.Url;
import com.sixbuilder.datatypes.account.AccountManager;
import com.sixbuilder.datatypes.account.User;
import com.sixbuilder.datatypes.persistence.PersistenceUtil;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;
import com.sixbuilder.twitterlib.services.TweetItemDAO;

/**
 * Component that renders a tweet to be curated or published, plus triggers some events.
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
//@Import(stylesheet={"common.css", "RecommendedTweet.css"}, library={"RecommendedTweet.js", "twitter-text-1.8.0.min.js"})
@Import(library={"RecommendedTweet.js", "twitter-text-1.8.0.min.js"})
@Events({
	RecommendedTweetConstants.PUBLISH_TWEET_EVENT, 
	RecommendedTweetConstants.DELETE_TWEET_EVENT,
	RecommendedTweetConstants.SAVE_TWEET_EVENT,
	RecommendedTweetConstants.LOAD_TWEET_EVENT,
	RecommendedTweetConstants.MEH_TWEET_EVENT})
public class RecommendedTweet implements ClientElement {

	@Parameter(required = true, allowNull = false)
	@Property
	private TweetItem tweet;
	
	@Parameter(required = true, allowNull = false)
	@Property
	private File accountsRoot;
	
	@Parameter(required = true, allowNull = false)
	@Property
	private String userId;
	
	@Inject
	TweetItemDAO tweetItemDAO;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@Property
	private String summary;
	
	@Property
	private boolean attachSnapshot;
	
	@Property
	private int imgIdx;
	
	@Property
	private String url;
	
	@Property
	private String isPublish;
	
	private String clientId;
	
	private boolean wasMehButtonClicked;
	
	private boolean wasQueueButtonClicked;
	
	private boolean wasShortenButtonClicked;
	
	@SuppressWarnings("unchecked")
	public List<String> getRecommendedHashtags() {
		final String hashtagsAsSingleString = tweet.getRecommendedHashtags();
		List<String> hashtags;
		if (hashtagsAsSingleString != null) {
			hashtags = new ArrayList<String>();
			final String[] strings = hashtagsAsSingleString.split("[\\s]+");
			for (String string : strings) {
				string = string.trim();
				if (string.length() > 0) {
					hashtags.add(string);
				}
			}
		}
		else {
			hashtags = Collections.EMPTY_LIST;
		}
		return hashtags;
	}
	
	void setupRender() {
		summary = tweet.getSummary();
		imgIdx=tweet.getImgIdx();
		isPublish=(tweet.isPublish()) ? "true":"false";
		attachSnapshot = tweet.isAttachSnapshot(); 
		clientId = javaScriptSupport.allocateClientId(resources);
		url=tweet.getUrl();
		JSONObject options = new JSONObject();
		options.put("id", clientId);
		options.put("imgCount", getImgCount());
		//options.put("publishUrl", resources.createEventLink("publish", tweet.getTweetId()).toAbsoluteURI());
		options.put("setDetailMode", resources.createEventLink("setDetailMode", tweet.getTweetId()).toAbsoluteURI());
		Object[] parm= { "6BUILDERTOKEN",tweet.getTweetId()};
		options.put("saveAttachSnapshot", resources.createEventLink("saveAttachSnapshot", parm).toAbsoluteURI());
		options.put("saveImgIdx", resources.createEventLink("saveImgIdx", parm).toAbsoluteURI());
		javaScriptSupport.addScript(String.format("initializeRecommendedTweet(%s);", options));
	}
	
	private int getImgCount() {
		int ret=0;
		if(tweet.getImgOneUrl()!=null&&tweet.getImgOneUrl().length()>0)
			ret=2;
		if(tweet.getImgTwoUrl()!=null&&tweet.getImgTwoUrl().length()>0)
			ret=3;
		if(tweet.getImgThreeUrl()!=null&&tweet.getImgThreeUrl().length()>0)
			ret=4;
		return ret;
	}
	
	/**
	 * Returns the value of the clientId field.
	 * @return a {@link String}.
	 */
	public String getClientId() {
		return clientId;
	}
	
	/**
	 * Handles the delete event.
	 */
	public Object onDelete(String id) {
		return triggerEvent(RecommendedTweetConstants.DELETE_TWEET_EVENT, id);
	}
	
	void onSelectedFromMeh() {
		wasMehButtonClicked = true;
		wasQueueButtonClicked = false;
		wasShortenButtonClicked = false;
	}
	
	void onSelectedFromQueue() {
		wasMehButtonClicked = false;
		wasQueueButtonClicked = true;
		wasShortenButtonClicked = false;
	}
	
	void onSelectedFromShorten() {
		wasMehButtonClicked = false;
		wasQueueButtonClicked = false;
		wasShortenButtonClicked = true;
	}

	/**
	 * Handles the queue and meh buttons
	 */
	public Object onSuccess(String id) {
		final TweetItem item = findById(id);
		item.setSummary(summary);
		item.setAttachSnapshot(attachSnapshot);
		item.setImgIdx(imgIdx);
		item.setUrl(url);
		if (wasMehButtonClicked)
			return meh(item);
		if (wasQueueButtonClicked)
			return queue(item);
		if(wasShortenButtonClicked)
			return revise(item);
		return null;
	}
	
	private Object queue(TweetItem item) {
		return triggerEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT, item);
	}
	
	private Object meh(TweetItem item) {	
		return triggerEvent(RecommendedTweetConstants.MEH_TWEET_EVENT, item);
	}
	
	public Object revise(TweetItem item) {
		return triggerEvent(RecommendedTweetConstants.REVISE_TWEET_EVENT, item);
	}
	
	public JSONObject onSaveImgIdx(String value, String id) throws Exception {
		TweetItem ti=tweetItemDAO.findById(accountsRoot, userId, id);
		ti.setImgIdx(Integer.parseInt(value));
		tweetItemDAO.update(accountsRoot, userId, ti);
		return null;
	}
	
	public JSONObject onSaveAttachSnapshot(String value,String id) throws Exception {
		TweetItem ti=tweetItemDAO.findById(accountsRoot, userId, id);
		ti.setAttachSnapshot(Boolean.parseBoolean(value));
		tweetItemDAO.update(accountsRoot, userId, ti);
		return null;
	}
	
	public void onSetDetailMode(String tweetId) {
		try {
			TweetItem ti=tweetItemDAO.findById(accountsRoot, userId, tweetId);
			ti.setDataMode(TweetItem.DATAMODE_DETAIL);
			tweetItemDAO.update(accountsRoot, userId, ti);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private Object triggerEvent(final String event, final String id) {
		return triggerEvent(event, findById(id));
	}

	private Object triggerEvent(final String event, final TweetItem item) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(event, new Object[]{item}, callback);
		return callback.getResult();
	}
	
	private TweetItem findById(String id) {
		final HolderComponentEventCallback<Object> callback = new HolderComponentEventCallback<Object>();
		resources.triggerEvent(RecommendedTweetConstants.LOAD_TWEET_EVENT, new Object[]{id}, callback);
		return (TweetItem) callback.getResult();
	}
	
	public boolean isPublish() {
		return tweet.isPublish();
	}
	
	public String getDataMode() {
		if(isPublish())
			return "detail";
		else return "summary";
	}
	
}
