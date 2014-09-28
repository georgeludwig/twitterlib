package com.sixbuilder;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import twitter4j.Status;
import twitter4j.UserList;

import com.georgeludwigtech.common.util.FileUtil;
import com.georgeludwigtechnology.twitterutil.TwitterUtil;
import com.sixbuilder.datatypes.account.AccountManager;
import com.sixbuilder.datatypes.account.User;
import com.sixbuilder.datatypes.twitter.TwitterSearch;
import com.sixbuilder.jobsequence.Context;

public abstract class AbstractTestSixBuilder {
	
	public static final String FILE_SEPARATOR=System.getProperty("file.separator");
	
	// credentials for GLTTest
	public static final String TWITTER_APIKEY="XMO426f9NIMXCcchf2XsnQ";
	public static final String TWITTER_CONSUMERSECRET="E0usARndyWSROlR15d0wjQGDgUShe6oMKt6yez7Go6c";
	public static final String PRIMARY_TEST_USER_NAME="Francois150";
	public static final String SECONDARY_TEST_USER_NAME="Francois250";
	
	// for the actionqueue
	public static final String DBACCOUNT="6btest";
	public static final String DBPWD="6btesttacofranchisescope";
//	public static final String QUEUE_TEST_DB_NAME="queueTest";
//	public static final String QUEUE_SETTINGS_TEST_DB_NAME="queueSettingsTest";
	
	// klout credentials
	public static final String KLOUT_APIKEY="vtk3p75vudw7tskb8zkr5v35 ";
	
	// bitly credentials Francois150
	public static final String BITLY_USERNAME="o_7eo2s8jsbl";
	public static final String BITLY_APIKEY="R_29b1d138c0afa741c3231cc41b29cfea";
	
	public static void setUp() throws Exception {
		// get path to test user data
		String userPath=getTestUserPath();
		setUp(userPath);
	}
	
	public static void setUp(String testUserPath) throws Exception {
		// delete prior test files
		cleanBasicFiles(testUserPath);
		// create basic system files
		setUpBasicFiles(testUserPath);
	}

	public static String getTestUserPath() throws Exception {
		return getTestUserPath(new File(getTestRoot()),PRIMARY_TEST_USER_NAME);
	}
	
	/**
	 * sets up the basic system files needed to run the system
	 * @param userPath
	 */
	public static void setUpBasicFiles(String userPath) throws Exception {
		// create a search params object
		TwitterSearch params=new TwitterSearch();
		// serialize the params
		File f=new File(userPath);
		f.mkdirs();
		params.serializeToFile(new File(userPath,Context.SEARCH_XML_FILENAME));
		//ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
		// copy authToken to current directory
		InputStream is = classLoader.getResourceAsStream ("authToken.properties");
		File authFile=new File(userPath+"authToken.properties");
		FileUtil.copy(is,authFile);
		File uf=new File(userPath+User.DEFAULT_FILENAME);
		uf.createNewFile();
		// create User
		User u=new User();
		u.setTwitterApiKey(TWITTER_APIKEY);
		u.setTwitterConsumerSecret(TWITTER_CONSUMERSECRET);
		u.setTwitterUserName(PRIMARY_TEST_USER_NAME);
		u.setAcceptedTos(true);
		u.serializeToFile(uf);
	}
	
	protected static void cleanRelationship(TwitterUtil util1,TwitterUtil util2) throws Exception {
		// convenient handles to names
		String n1=util1.getScreenName();
		String n2=util2.getScreenName();	
		// make sure they don't follow each other
		util1.stopFollowing(n2);
		util2.stopFollowing(n1);
		// make sure they don't list each other
		util1.delistUser(n2);
		util2.delistUser(n1);
	}
	
	protected static void cleanTweets(TwitterUtil util) throws Exception {
		List<Status>statusList=util.getMyTimeline(1000);
		for(Status s:statusList) {
			util.destroyStatus(s.getId());
		}
		// wait one minute for update to propagate
		pause(60000);
	}
	
	protected static void ensureList(TwitterUtil one,TwitterUtil two) throws Exception {
		// user one
		// check if two is already listed
		if(!one.isUserListedByMe(two.showUser().getId())) {
			UserList list=null;
			// get user lists
			List<UserList> lists=one.getMyUserLists();
			if(lists.size()==0)
				list=createTestList(one);
			else list=lists.get(0);
			one.addUserListMember(list.getId(), two.showUser().getId());
		}	
		// user two
		// check if one is already listed
		if(!two.isUserListedByMe(one.showUser().getId())) {
			UserList list=null;
			// get user lists
			List<UserList> lists=two.getMyUserLists();
			if(lists.size()==0)
				list=createTestList(two);
			else list=lists.get(0);
			two.addUserListMember(list.getId(), one.showUser().getId());
		}	
	}
	
	static UserList createTestList(TwitterUtil util) throws Exception {
		// create a test list
		UserList list=util.createUserList("prefere", true, "mes amis prefere");
		return list;
	}
	
	public static String getTestUserPath(File accountsRoot,String user) throws Exception {
		String s=AccountManager.getAccountPath(accountsRoot.toString(),user);
		return s;
	}
	
	public static String getTestRoot() throws Exception {
		String osName=System.getProperty("os.name");
		String path=null;
		if(osName.equals("Mac OS X")) {
			path="/javatests/sixbuilder/";
		}
		if(osName.startsWith("Windows")) {
			path="C:\\javatests\\sixbuilder\\";
		}
		if(path==null)
			throw new Exception("unable to identify OS: "+osName);
		return path;
	}
	 
	/**
	 * deletes the user directory
	 * @param userPath
	 * @throws Exception
	 */
	public static void cleanBasicFiles(String userPath) throws Exception {
		System.out.println("cleaning test directory: "+userPath);
		// delete prior test files
		File file=new File(userPath);
		boolean clean=FileUtil.clearDirectory(file);
		if(!clean)
			throw new Exception("unable to clean directory "+userPath);
		file.mkdirs();
	}
	
	private static Integer pauseSem=new Integer(0);
	public static void pause(long time) {
		synchronized(pauseSem) {
			try {
				pauseSem.wait(time);
			} catch (InterruptedException e) {
				//
			}
		}
	}
	
	/**
	 * gets a twitter util, looking for auth token in class path of dir
	 * example: getTwitterUtil(PRIMARY_TEST_USERNAME);
	 */
	public static TwitterUtil getTwitterUtil(String userName,String dir) throws Exception {
		// copy authToken to current directory
//		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//		InputStream is = classLoader.getResourceAsStream (userName+"/authToken.obj");
//		File authFile=new File(dir+"/authToken.obj");
//		FileUtil.copy(is,authFile);
		if(!dir.endsWith(FILE_SEPARATOR))
			dir=dir+FILE_SEPARATOR;
		if(!userName.endsWith(FILE_SEPARATOR))
			userName=userName+FILE_SEPARATOR;
		copyFileToDir(userName+"authToken.properties",dir+"authToken.properties");
		// get a twitter util
		TwitterUtil tUtil=new TwitterUtil(dir,TWITTER_APIKEY,TWITTER_CONSUMERSECRET);
		return tUtil;
	}
	
	public static void copyFileToDir(String inFile, String outFile) throws Exception {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		InputStream is = classLoader.getResourceAsStream (inFile);
		File out=new File(outFile);
		FileUtil.copy(is,out);
	}

	/**
	 * convenience method for primary test user util
	 * @return
	 * @throws Exception
	 */
	public static TwitterUtil getPrimaryTwitterUtil() throws Exception {
		String path=getTestUserPath();
		return getTwitterUtil(PRIMARY_TEST_USER_NAME,path);
	}
	
	/**
	 * convenience method for secondary test user util
	 * @return
	 * @throws Exception
	 */
	public static TwitterUtil getSecondaryTwitterUtil() throws Exception {
		String path=getTestUserPath(new File(getTestRoot()),SECONDARY_TEST_USER_NAME);
		return getTwitterUtil(SECONDARY_TEST_USER_NAME,path);
	}
}
