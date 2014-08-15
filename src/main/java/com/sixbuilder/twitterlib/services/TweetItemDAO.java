package com.sixbuilder.twitterlib.services;

import java.util.List;

import com.sixbuilder.datatypes.twitter.TweetItem;

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
	List<TweetItem> getAll() throws Exception;
	
	/**
	 * Deletes a tweet.
	 * @param tweetItem a {@link TweetItem}.
	 * @throws Exception 
	 */
	public void delete(TweetItem tweetItem) throws Exception;
	
	/**
	 * Finds a tweet by id.
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	TweetItem findById(String id) throws Exception;

	/**
	 * Updates a tweet in the database.
	 * @param tweetItem a {@link TweetItem}.
	 */
	void update(TweetItem tweetItem) throws Exception;
	
}
