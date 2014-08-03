package com.sixbuilder.twitterlib.services;

import java.util.List;

import com.sixbuilder.twitterlib.helpers.TweetItem;

/**
 * DAO Interface for {@link TweetItem}. Intended for use in tests only. 
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public interface TweetItemDAO {

	/**
	 * Return all objects.
	 * @return
	 */
	List<TweetItem> getAll();
	
	/**
	 * Deletes a tweet.
	 * @param tweetItem a {@link TweetItem}.
	 */
	public void delete(TweetItem tweetItem);
	
	/**
	 * Finds a tweet by id.
	 * @param id
	 * @return
	 */
	TweetItem findById(String id);

	/**
	 * Updates a tweet in the database.
	 * @param tweetItem a {@link TweetItem}.
	 */
	void update(TweetItem tweetItem);
	
}
