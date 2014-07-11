package com.sixbuilder.helpers;

public class TestPage {

	public static String getTestRoot() throws Exception {
		String osName=System.getProperty("os.name");
		String path=null;
		if(osName.equals("Mac OS X")) {
			path="/javatests/georgeludwigtech/";
		}
		if(osName.startsWith("Windows")) {
			path="C:\\javatests\\georgeludwigtech\\";
		}
		if(path==null)
			throw new Exception("unable to identify OS: "+osName);
		return path;
	}
}
