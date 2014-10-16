package com.sixbuilder.twitterlib.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sixbuilder.twitterlib.helpers.Action;
import com.sixbuilder.twitterlib.helpers.ActionState;
import com.sixbuilder.twitterlib.helpers.Tweet;

/**
 * {@link TweetDAO} implementation
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class TweetDAOImpl implements TweetDAO {

	private List<Tweet> items;
	
	public TweetDAOImpl() {
		
		items = new ArrayList<Tweet>();
		
		for (int i = 0; i < 10; i++) {
			items.add(create(i));
		}
		
		int count = 0;
		
		for (Tweet tweet : items) {
			if (count++ % 2 == 0) {
				for (Tweet other : items) {
					if (!tweet.equals(other)) {
						tweet.getConversation().add(other);
					}
				}
			}
		}
		
	}

	public List<Tweet> getAll() {
		return items;
	}
	
	private Tweet create(int i) {
		Tweet tweet = new Tweet();
		tweet.setClearEnabled(i % 2 == 0);
		tweet.setPosted(new Date(2987349278342L + i * 4500));
		tweet.setTwitterClientId("Unknown Twitter Client");
		tweet.setTwitterUsername("GeorgeLudwigTwitter");
		tweet.setUsername("GeorgeLudwing6BU");
		for (Action action : Action.values()) {
			ActionState state;
			switch (i % 4) {
				case 0: state = ActionState.QUEUED; break;
				case 1: state = ActionState.COMPLETED; break;
				case 2: state = ActionState.ENABLED; break;
				case 3: state = ActionState.DISABLED; break;
				default: throw new RuntimeException("Shouldn't happen");
			}
			state.set(tweet, action);
		}
		tweet.setRetweet(i % 2 == 0);
		if (tweet.isRetweet()) {
			tweet.setRetwittedDate(new Date(tweet.getPosted().getTime() - 3 * 60 * 60));
			tweet.setRetweetedId(tweet.getId() + 1000000);
			tweet.setRetwittedTwitterUsername("IWasRetwitted");
		}
		tweet.setProfilePictureUrl("http://lorempixel.com/75/75/sports/" + (i + 1));
		tweet.setContent("#" + i + ". Some cool web page somewhere in the Internet. " + tweet.getProfilePictureUrl());
		tweet.setId(String.valueOf(i));
		return tweet;
	}
	
	public void delete(Tweet Tweet) {
		items.remove(Tweet);
	}
	
	public Tweet findById(String id) {
		Tweet tweet = null;
		for (Tweet item : items) {
			if (item.getId().equals(id)) {
				tweet = item;
				break;
			}
		}
		return tweet;
	}

	public void update(Tweet Tweet) {
	}

}
