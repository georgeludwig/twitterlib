package com.sixbuilder.twitterlib.services;

import java.io.File;
import java.util.List;

import com.sixbuilder.datatypes.twitter.TwitterUser;

/**
 * DAO Interface for TwitterUser. 
 * 
 */
public interface TwitterUserDAO {

	/**
	 * Returns all TwitterUsers sorted by score (NOT Klout score).
	 * @return
	 */
	List<TwitterUser> getAll(File accountsRoot,String userId) throws Exception;
	
	/**
	 * Finds a TwitterUser by id.
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	TwitterUser findById(File accountsRoot,String userId,Long id) throws Exception;

	/**
	 * Updates a TwitterUser in the database.
	 * @param TwitterUser
	 */
	void update(File accountsRoot,String userId,TwitterUser twitterUser) throws Exception;
	
}
