package com.sixbuilder.helpers;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sixbuilder.twitterlib.helpers.TweetItem;
import com.sixbuilder.twitterlib.helpers.TweetItemComparatorPrevious;
import com.georgeludwigtech.common.util.SerializableRecordHelper;

public class TweetItemTest {
	
	String DELIM=TweetItem.FIELDDELIM;

	@Before
	public void setUp() throws Exception {
		System.out.println("/////////////////////// TweetItemTest ///////////////////////");
	}
	
	@Test
	public void testApp() throws Exception{
		long date=System.currentTimeMillis();
		// test for theoretical string consistency
		TweetItem tweetItem=new TweetItem();
		String actualItemString=tweetItem.toString();
		String theoreticalItemString=
				TweetItem.UNINIT_TWEET_ID+DELIM
				+false+DELIM+
				TweetItem.UNINIT_SEARCH_NAME+DELIM+
				TweetItem.UNINIT_SCORE+DELIM+
				TweetItem.UNINIT_URL+DELIM+
				TweetItem.UNINIT_SHORTENED_URL+DELIM+
				TweetItem.UNINIT_SNAPSHOT_URL+DELIM+
				false+DELIM+
				TweetItem.UNINIT_RECOMMENDED_HASHTAGS+DELIM+
				SerializableRecordHelper.getDateTimeString(0)+DELIM+
				TweetItem.UNINIT_SUMMARY;	
		
		assertTrue(actualItemString.equals(theoreticalItemString));
		// text string constructor consistency
		tweetItem=new TweetItem(actualItemString);
		actualItemString=tweetItem.toString();
		assertTrue(actualItemString.equals(theoreticalItemString));
		// test explicit value consistency
		tweetItem.setPublish(true);
		tweetItem.setSearchName("testSearch");
		tweetItem.setScore(3.0);
		tweetItem.setUrl("http://url.com");
		tweetItem.setShortenedUrl("http://shortUrl.bit.ly");
		tweetItem.setDateTweeted(date);
		tweetItem.setSummary("this summary has it's own punctuation");
		actualItemString=tweetItem.toString();
		theoreticalItemString=
			TweetItem.UNINIT_TWEET_ID+DELIM
			+true+DELIM+
			"testSearch"+DELIM+
			"3.0"+DELIM+
			"http://url.com"+DELIM+
			"http://shortUrl.bit.ly"+DELIM+
			TweetItem.UNINIT_SNAPSHOT_URL+DELIM+
			false+DELIM+
			TweetItem.UNINIT_RECOMMENDED_HASHTAGS+DELIM+
			SerializableRecordHelper.getDateTimeString(date)+DELIM+
			"this summary has it's own punctuation";

		assertTrue(actualItemString.equals(theoreticalItemString));
		// text string constructor consistency with explicit values
		tweetItem=new TweetItem(actualItemString);
		actualItemString=tweetItem.toString();
		assertTrue(actualItemString.equals(theoreticalItemString));
		String summary="Going to #SanFrancisco and #NapaValley one week today. Anyone have any advice or suggestions re: places to eat, things to see?";
		// test hashtags
		tweetItem.setSummary(summary);
		List<String>hashtags=tweetItem.getHashtags();
		assertTrue(hashtags.contains("#sanfrancisco"));
		assertTrue(hashtags.contains("#napavalley"));
		// test comparator
		List<TweetItem>itemList=new ArrayList<TweetItem>();
		TweetItemComparatorPrevious comp=new TweetItemComparatorPrevious();
		TweetItem i1=new TweetItem();
		TweetItem i2=new TweetItem();
		itemList.add(i1);
		itemList.add(i2);
		//
		i1.setDateTweeted((long)100);
		i1.setScore((double)0);
		i1.setSearchName("u");
		i2.setDateTweeted((long)0);
		i2.setScore((double)0);
		i2.setSearchName("u");
		Collections.sort(itemList,comp);
		assertTrue(itemList.get(0)==i2);
		//
		i1.setDateTweeted((long)0);
		i1.setScore((double)0);
		i1.setSearchName("u");
		i2.setDateTweeted((long)0);
		i2.setScore((double)2);
		i2.setSearchName("u");
		Collections.sort(itemList,comp);
		assertTrue(itemList.get(0)==i2);
	}
	
	@After
	public void tearDown() throws Exception {}
	
}
