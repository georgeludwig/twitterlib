package com.sixbuilder.twitterlib.services;

import java.util.ArrayList;
import java.util.List;

import com.sixbuilder.AbstractTestSixBuilder;
import com.sixbuilder.datatypes.persistence.PendingTweetFileUtil;
import com.sixbuilder.datatypes.twitter.TweetItem;

/**
 * {@link com.sixbuilder.twitterlib.services.TweetItemDAO} implementation
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class TweetItemDAOImpl implements TweetItemDAO {
	
	public TweetItemDAOImpl() throws Exception {}

	public List<TweetItem>getAll() throws Exception {
//		List<TweetItem>itemList=new ArrayList<TweetItem>();
//		Collection<TweetItem> c=pendingTweetFileUtil.getPendingTweetMap().values();
//		itemList.addAll(c);
		PendingTweetFileUtil util=new PendingTweetFileUtil(AbstractTestSixBuilder.getTestUserPath()+PendingTweetFileUtil.FILENAME);
		List<TweetItem>itemList=new ArrayList<TweetItem>();
		itemList.addAll(util.getPendingTweetMap().values());
		return itemList;
	}

	public void delete(TweetItem tweetItem) throws Exception {
		// don't bother deleting from the pending tweet file; the set managers take care of the display
		// for now, we just remove it from both set managers...handled by ReccommendedTweetDisplay
//		pendingTweetFileUtil.getPendingTweetMap().remove(tweetItem.getUrl());
//		pendingTweetFileUtil.serialize();
	}
	
	public TweetItem findById(String id) throws Exception {
		TweetItem tweetItem = null;
		for (TweetItem item : getAll()) {
			if (item.getTweetId().equals(id)) {
				tweetItem = item;
				break;
			}
		}
		return tweetItem;
	}

	public void update(TweetItem tweetItem) throws Exception {
		// we save all at once
		PendingTweetFileUtil util=new PendingTweetFileUtil(AbstractTestSixBuilder.getTestUserPath()+PendingTweetFileUtil.FILENAME);
		util.addRecordToCollection(tweetItem);
		util.serialize();
	}

}
