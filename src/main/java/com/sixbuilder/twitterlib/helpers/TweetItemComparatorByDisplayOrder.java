package com.sixbuilder.twitterlib.helpers;

import java.util.Comparator;

import com.sixbuilder.datatypes.twitter.TweetItem;

public class TweetItemComparatorByDisplayOrder implements Comparator<TweetItem>{

	public int compare(TweetItem arg0, TweetItem arg1) {
		if(arg0.getDisplayOrder()>arg1.getDisplayOrder()) {
			return -1;
		}
		if(arg0.getDisplayOrder()<arg1.getDisplayOrder()) {
			return 1;
		}
		return 0;
	}

}
