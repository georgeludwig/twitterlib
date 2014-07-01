package com.sixbuilder.twitterlib.helpers;

import java.util.Comparator;

public class TweetItemComparatorPrevious implements Comparator<TweetItem>{

	/**
	 * sorts descending on date tweeted, score, and search name
	 */
	public int compare(TweetItem o1, TweetItem o2) {
		if(o1.getDateTweeted()<o2.getDateTweeted())
			return -1;
		if(o1.getDateTweeted()>o2.getDateTweeted())
			return 1;
		if(o1.getScore()<o2.getScore())
			return 1;
		if(o1.getScore()>o2.getScore())
			return -11;
		return o1.getSearchName().compareTo(o2.getSearchName());
	}

}
