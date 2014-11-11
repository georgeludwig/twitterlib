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
	private String isPublish;
	
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
		isPublish=(tweet.isPublish()) ? "true":"false";
		attachSnapshot = tweet.isAttachSnapshot();
		clientId = javaScriptSupport.allocateClientId(resources);
		JSONObject options = new JSONObject();
		options.put("id", clientId);
		options.put("publishUrl", resources.createEventLink("publish", tweet.getTweetId()).toAbsoluteURI());
		Object[] parm= { "6BUILDERTOKEN",tweet.getTweetId()};
		//options.put("selectImage", resources.createEventLink("selectImage",parm).toAbsoluteURI());
		options.put("shortenUrlUrl", resources.createEventLink("shortenUrl", parm).toAbsoluteURI());
		options.put("saveAttachSnapshot", resources.createEventLink("saveAttachSnapshot", parm).toAbsoluteURI());
		options.put("saveImgIdx", resources.createEventLink("saveImgIdx", parm).toAbsoluteURI());
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
	public JSONObject onShortenUrl(String value, String id) {
		// decode id from binary string
		StringBuilder b = new StringBuilder();
		for(int i=0;i<value.length();i=i+16) {
			// get next char string
			String s=value.substring(i,i+16);
			char c=(char)Integer.parseInt(s, 2);
			b.append(c);
		}
		String url=b.toString();
		try {
			URL uri=new URL(url);
		} catch(Exception e) {
			return new JSONObject("url", "invalid URL");
		}
		// start thread for images
		getNewSnapshotWorker worker=new getNewSnapshotWorker(url);
		Thread t=new Thread(worker);
		t.start();
		try {
			String accountPath=AccountManager.getAccountPath(accountsRoot.toString(), userId);
			User user=new User(AccountManager.getUserFile(accountPath));
			String shortUrl=shortenUrlUsingBitly(user,url);
			JSONObject ret=new JSONObject();
			t.join();
			TweetItem ti=tweetItemDAO.findById(accountsRoot, userId, id);
			ti.setUrl(url);
			String oldId=ti.getTweetId();
			ti.setTweetId(String.valueOf(url.hashCode()));
			ti.setShortenedUrl(shortUrl);
			ret.append("url", shortUrl);
			if(worker.resp.getSnapshotUrl()!=null) {
				ret.append("snapshotUrl",worker.resp.getSnapshotUrl());
				ti.setSnapshotUrl(worker.resp.getSnapshotUrl());
			}
			List<String>imgList=worker.resp.getImageUrlList();
			if(imgList!=null) {
				if(imgList.size()>0) {
					ret.append("imgOne", imgList.get(0));
					ti.setImgOneUrl(imgList.get(0));
				}
				if(imgList.size()>1) {
					ret.append("imgTwo", imgList.get(1));
					ti.setImgTwoUrl(imgList.get(1));
				}
				if(imgList.size()>2) {
					ret.append("imgThree", imgList.get(2));
					ti.setImgThreeUrl(imgList.get(2));
				}
			}
			tweetItemDAO.update(accountsRoot, userId, ti);
			if(ti.isPublish()) {
				SetManager qm=PersistenceUtil.getQueuedSetManager(accountsRoot, userId);
				qm.removeSetItem(oldId);
				qm.addSetItem(new SetItemImpl(ti.getTweetId()));
			} else {
				SetManager cm=PersistenceUtil.getCurationSetManager(accountsRoot, userId);
				cm.removeSetItem(oldId);
				cm.addSetItem(new SetItemImpl(ti.getTweetId()));
			}
			return ret;
		} catch (Exception e) {
			return new JSONObject("url", "invalid URL");
		}
	}
	
	class getNewSnapshotWorker implements Runnable {
		
		private String url;
		public UrlSnapshotServiceResponse resp;
		
		getNewSnapshotWorker(String url) {
			this.url=url;
		}

		@Override
		public void run() {
			try {
				UrlSnapshotServiceRequest req=new UrlSnapshotServiceRequest();
				req.setWidth(1280);
				req.setHeight(1024);
				req.setTargetUrl(url);
				req.setServiceUrl("http://54.191.249.251:3001");
				resp=UrlSnapshotServiceClient.snap(req);
			} catch(Exception e) {
				// 
			}
		}
		
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
