package com.sixbuilder.twitterlib.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sixbuilder.datatypes.account.AccountManager;
import com.sixbuilder.datatypes.persistence.PendingTweetFileUtil;
import com.sixbuilder.datatypes.twitter.TweetItem;

/**
 * {@link com.sixbuilder.twitterlib.services.TweetItemDAO} implementation
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class TweetItemDAOImpl implements TweetItemDAO {
	
	public TweetItemDAOImpl() throws Exception {}

	public List<TweetItem>getAll(File accountsRoot,String userId) throws Exception {
		String accountPath=AccountManager.getAccountPath(accountsRoot.toString(),userId);
		PendingTweetFileUtil util=new PendingTweetFileUtil(accountPath+PendingTweetFileUtil.FILENAME);
		List<TweetItem>itemList=new ArrayList<TweetItem>();
		itemList.addAll(util.getPendingTweetMap().values());
		return itemList;
	}

	public void delete(File accountsRoot,String userId,TweetItem tweetItem) throws Exception {
		// don't bother deleting from the pending tweet file; the set managers take care of the display
		// for now, we just remove it from both set managers...handled by ReccommendedTweetDisplay
//		pendingTweetFileUtil.getPendingTweetMap().remove(tweetItem.getUrl());
//		pendingTweetFileUtil.serialize();
	}
	
	public TweetItem findById(File accountsRoot,String userId,String id) throws Exception {
		return findById(getAll(accountsRoot,userId),id);
	}
	
	private TweetItem findById(List<TweetItem>tiList,String id) throws Exception {
		TweetItem tweetItem = null;
		for (TweetItem item : tiList) {
			if (item.getTweetId().equals(id)) {
				tweetItem = item;
				break;
			}
		}
		return tweetItem;
	}

	public boolean deleteById(File accountsRoot,String userId,String id) throws Exception {
		String accountPath=AccountManager.getAccountPath(accountsRoot.toString(),userId);
		PendingTweetFileUtil util=new PendingTweetFileUtil(accountPath+PendingTweetFileUtil.FILENAME);
		List<TweetItem>tiList=new ArrayList<TweetItem>();
		tiList.addAll(util.getPendingTweetMap().values());
		TweetItem ti=findById(tiList,id);
		if(ti==null)
			return false;
		Map<String, TweetItem>tiMap=util.getPendingTweetMap();
		if(!tiMap.containsKey(ti.getUrl()))
			return false;
		tiMap.remove(ti.getUrl());
		util.serialize();
		return true;
	}
	
	public void update(File accountsRoot,String userId,TweetItem tweetItem) throws Exception {
		try {
			// we save all at once
			String accountPath=AccountManager.getAccountPath(accountsRoot.toString(),userId);
			PendingTweetFileUtil util=new PendingTweetFileUtil(accountPath+PendingTweetFileUtil.FILENAME);
			util.addRecordToCollection(tweetItem);
			util.serialize();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
