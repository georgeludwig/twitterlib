package com.sixbuilder.helpers;

public class TestPage {

	public static String getTestRoot() {
		String osName=System.getProperty("os.name");
		String path=null;
		if(osName.startsWith("Windows")) {
			path="C:\\javatests\\twitterlib\\";
		} else {
			path="/javatests/twitterlib/";
		}
		return path;
	}
}
