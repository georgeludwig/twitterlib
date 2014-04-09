package com.sixbuilder.twitterlib.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.LibraryMapping;

/**
 * Twitter Lib module class.
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class TwitterLibModule {
	
	public static void bind(ServiceBinder binder) {
		binder.bind(FontAwesomeService.class, FontAwesomeServiceImpl.class).scope(ScopeConstants.PERTHREAD);
	}

	@Contribute(ComponentClassResolver.class)
	public static void addLibrary(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("twitterlib", "com.sixbuilder.twitterlib"));
	}
	
}
