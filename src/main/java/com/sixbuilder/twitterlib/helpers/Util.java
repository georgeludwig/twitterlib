package com.sixbuilder.twitterlib.helpers;

import com.georgeludwigtech.concurrent.threadpool.ThreadPool;
import com.georgeludwigtech.concurrent.threadpool.ThreadPoolFactory;

public abstract class Util {
	
	public static final int DEFAULT_UI_THREADPOOL_SIZE=2;
	private static ThreadPool uiThreadPool;
	private static Integer uiTpSem=new Integer(0);
	public static ThreadPool getUiThreadPool() {
		if(uiThreadPool==null) {
			synchronized(uiTpSem) {
				if(uiThreadPool==null) {
					int count=DEFAULT_UI_THREADPOOL_SIZE;
					String cnt=System.getProperty("twitterlib.UiThreadpoolCount");
					if(cnt!=null) {
						try {
							count=Integer.parseInt(cnt);
							System.out.println("twitterlib.UiThreadpoolCount = "+cnt);
						} catch(Exception e) {
							System.out.println("error parsing twitterlib.UiThreadpoolCount, value was "+cnt);
						}
					}
					uiThreadPool=ThreadPoolFactory.getNewInstance("UiThreadPool", count);
				}
			}
		}
		return uiThreadPool;
	}

}
