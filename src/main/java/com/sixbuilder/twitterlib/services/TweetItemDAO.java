package com.sixbuilder.twitterlib.services;

import java.io.File;
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
	List<TweetItem> getAll(File accountsRoot,String userId) throws Exception;
	
	/**
	 * Deletes a tweet.
	 * @param tweetItem a {@link TweetItem}.
	 * @throws Exception 
	 */
	public void delete(File accountsRoot,String userId,TweetItem tweetItem) throws Exception;
	
	/**
	 * Finds a tweet by id.
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	TweetItem findById(File accountsRoot,String userId,String id) throws Exception;

	/**
	 * Updates a tweet in the database.
	 * @param tweetItem a {@link TweetItem}.
	 */
	void update(File accountsRoot,String userId,TweetItem tweetItem) throws Exception;
	
	boolean deleteById(File accountsRoot,String userId,String id) throws Exception;
	
}
