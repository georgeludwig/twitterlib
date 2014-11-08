package com.sixbuilder.twitterlib.components;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;

import java.io.File;
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

import com.rosaloves.bitlyj.BitlyException;
import com.rosaloves.bitlyj.Url;
import com.sixbuilder.datatypes.account.AccountManager;
import com.sixbuilder.datatypes.account.User;
import com.sixbuilder.datatypes.twitter.TweetItem;
import com.sixbuilder.twitterlib.RecommendedTweetConstants;
import com.sixbuilder.twitterlib.helpers.HolderComponentEventCallback;

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
	private ComponentResources resources;
	
	@Inject
	private JavaScriptSupport javaScriptSupport;
	
	@Property
	private String summary;
	
	@Property
	private boolean attachSnapshot;
	
	@Property
	private int imgIdx;
	
	private String clientId;
	
	private boolean wasMehButtonClicked;
	
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
		attachSnapshot = tweet.isAttachSnapshot();
		clientId = javaScriptSupport.allocateClientId(resources);
		JSONObject options = new JSONObject();
		options.put("id", clientId);
		options.put("publishUrl", resources.createEventLink("publish", tweet.getTweetId()).toAbsoluteURI());
		options.put("shortenUrlUrl", resources.createEventLink("shortenUrl", "6BUILDERTOKEN").toAbsoluteURI());
		Object[] parm= { "6BUILDERTOKEN",tweet.getTweetId()};
		options.put("selectImage", resources.createEventLink("selectImage",parm).toAbsoluteURI());
		javaScriptSupport.addScript(String.format("initializeRecommendedTweet(%s);", options)); 
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
	}
	
	void onSelectedFromQueue() {
		wasMehButtonClicked = false;
	}

	/**
	 * Handles the queue and meh buttons
	 */
	public Object onSuccess(String id) {
		final TweetItem item = findById(id);
		item.setSummary(summary);
		item.setAttachSnapshot(attachSnapshot);
		item.setImgIdx(imgIdx);
		if (wasMehButtonClicked) {
			return meh(item);
		} else {
			return queue(item);
		}
	}
	
	private Object queue(TweetItem item) {
		item.setPublish(true);
		// 
		return triggerEvent(RecommendedTweetConstants.PUBLISH_TWEET_EVENT, item);
	}
	
	private Object meh(TweetItem item) {	
		item.setPublish(false);
		return triggerEvent(RecommendedTweetConstants.MEH_TWEET_EVENT, item);
	}
	
	/**
	 * Handles the shorten URL event.
	 */
	public JSONObject onShortenUrl(String id) {
		// decode id from binary string
		StringBuilder b = new StringBuilder();
		for(int i=0;i<id.length();i=i+16) {
			// get next char string
			String s=id.substring(i,i+16);
			char c=(char)Integer.parseInt(s, 2);
			b.append(c);
		}
		String url=b.toString();
		try {
			String accountPath=AccountManager.getAccountPath(accountsRoot.toString(), userId);
			User user=new User(AccountManager.getUserFile(accountPath));
			String shortUrl=shortenUrlUsingBitly(user,url);
			return new JSONObject("url", shortUrl);
		} catch (Exception e) {
			return new JSONObject("url", url);
		}
	}
	
	private String shortenUrlUsingBitly(User user,String url) throws Exception {
		// bitly encode url
		String bitlyUserName = user.getBitlyUserName();
		String bitlyApiKey = user.getBitlyApiKey();
		if((bitlyUserName==null||bitlyUserName.trim().length()==0) ||
				(bitlyApiKey==null||bitlyApiKey.trim().length()==0)) {
			bitlyUserName=User.DEFAULT_BITLY_USERNAME;
			bitlyApiKey=User.DEFAULT_BITLY_APIKEY;
			System.out.println("encoding bitly using default bitly credentials");
		}
		try {
			Url u = as(bitlyUserName, bitlyApiKey).call(shorten(url));
			String shortUrl=u.getShortUrl();
			return shortUrl;
		} catch(BitlyException e) {
			return url;
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
