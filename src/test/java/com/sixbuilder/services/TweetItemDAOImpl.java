package com.sixbuilder.services;

import com.sixbuilder.datatypes.persistence.PendingTweetFileUtil;
import com.sixbuilder.helpers.TestPage;
import com.sixbuilder.twitterlib.services.TweetItemDAO;
import com.sixbuilder.datatypes.twitter.TweetItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link com.sixbuilder.twitterlib.services.TweetItemDAO} implementation
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class TweetItemDAOImpl implements TweetItemDAO {

	private PendingTweetFileUtil pendingTweetFileUtil;
	
	public TweetItemDAOImpl() throws Exception {
		if(pendingTweetFileUtil==null) {
			pendingTweetFileUtil=new PendingTweetFileUtil(TestPage.getTestRoot()+PendingTweetFileUtil.FILENAME);	
			// now add tweet Ids && hashtags...these are missing from the test data
			int i=0;
			for(TweetItem item:pendingTweetFileUtil.getPendingTweetMap().values()) {
				item.setTweetId(String.valueOf(i));
				i++;
				item.setRecommendedHashtags("#hashtag1 #hashtag2 #hashtag3");
			}
			pendingTweetFileUtil.serialize();
		}
	}

	public List<TweetItem>getAll() {
		List<TweetItem>itemList=new ArrayList<TweetItem>();
		Collection<TweetItem> c=pendingTweetFileUtil.getPendingTweetMap().values();
		itemList.addAll(c);
		return itemList;
				
	}

	public void delete(TweetItem tweetItem) throws Exception {
//		pendingTweetFileUtil.getPendingTweetMap().remove(tweetItem.getUrl());
//		pendingTweetFileUtil.serialize();
		// for now, we just remove it from both set managers...handled by ReccommendedTweetDisplay
	}
	
	public TweetItem findById(String id) throws Exception {
		TweetItem tweetItem = null;
		for (TweetItem item : pendingTweetFileUtil.getPendingTweetMap().values()) {
			if (item.getTweetId().equals(id)) {
				tweetItem = item;
				break;
			}
		}
		return tweetItem;
	}

	public void update(TweetItem tweetItem) throws Exception {
		// here we don't bother with saving anything
		pendingTweetFileUtil.serialize();
	}

}
