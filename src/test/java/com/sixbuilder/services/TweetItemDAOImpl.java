package com.sixbuilder.services;

import java.util.ArrayList;
import java.util.List;

import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * {@link TweetItemDAO} implementation
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class TweetItemDAOImpl implements TweetItemDAO {

	private List<TweetItem> items;
	
	public TweetItemDAOImpl() {
		
		items = new ArrayList<TweetItem>();
		
		for (int i = 0; i < 10; i++) {
			items.add(create(i));
		}
		
	}

	public List<TweetItem> getAll() {
		return items;
	}
	
	private TweetItem create(int i) {
		TweetItem item = new TweetItem();
		item.setAttachSnapshot(i % 2 == 0);
		item.setDateTweeted(2987349278342L + i * 4500);
//		item.setPublish(i % 2 == 0);
		item.setRecommendedHashtags("#hashtag" + i + " #cool" + i+ " #twitter" + i + " #another" + i+ " #something" + i);
		item.setScore(10 - i);
		item.setSearchName("searchName" + i);
//		item.setShortenedUrl("http://bit.ly/tweet" + i);
		item.setSnapshotUrl("http://lorempixel.com/75/75/sports/" + (i + 1));
		item.setUrl("http://www.6builder.com/url/" + i + "/something/long/just/for/testing");
		item.setSummary("Summary #" + i + ". Some cool web page somewhere in the Internet. " + item.getUrl());
		item.setTweetId(String.valueOf(i));
		return item;
	}

	public void delete(TweetItem tweetItem) {
		items.remove(tweetItem);
	}
	
	public TweetItem findById(String id) {
		TweetItem tweetItem = null;
		for (TweetItem item : items) {
			if (item.getTweetId().equals(id)) {
				tweetItem = item;
				break;
			}
		}
		return tweetItem;
	}

	public void update(TweetItem tweetItem) {
	}

}
