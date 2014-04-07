package com.sixbuilder.services;

import java.util.List;

import com.sixbuilder.twitterlib.helpers.Tweet;

/**
 * DAO Interface for {@link Tweet}. Intended for use in tests only. 
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public interface TweetDAO {

	/**
	 * Return all objects.
	 * @return
	 */
	List<Tweet> getAll();
	
	/**
	 * Deletes a tweet.
	 * @param Tweet a {@link Tweet}.
	 */
	public void delete(Tweet Tweet);
	
	/**
	 * Finds a tweet by id.
	 * @param id
	 * @return
	 */
	Tweet findById(String id);

	/**
	 * Updates a tweet in the database.
	 * @param Tweet a {@link Tweet}.
	 */
	void update(Tweet Tweet);
	
}
