package com.sixbuilder.twitterlib.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.Ajax;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.services.LibraryMapping;

import com.google.gson.JsonObject;

/**
 * Twitter Lib module class.
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class TwitterLibModule {
	
	public static void bind(ServiceBinder binder) {
		binder.bind(FontAwesomeService.class, FontAwesomeServiceImpl.class).scope(ScopeConstants.PERTHREAD);
		binder.bind(QueueManager.class, QueueManagerImpl.class);
	}

//	@Contribute(ComponentClassResolver.class)
//	public static void addLibrary(Configuration<LibraryMapping> configuration) {
//		configuration.add(new LibraryMapping("twitterlib", "com.sixbuilder.twitterlib"));
//	}
	
    public static void contributeComponentClassResolver(
        Configuration<LibraryMapping> configuration) {
        configuration.add(new LibraryMapping("twitterlib", "com.sixbuilder.twitterlib"));
    }

    @Contribute(ComponentEventResultProcessor.class)
    @Ajax
    public static void provideAjaxComponentEventResultProcessors(
        @SuppressWarnings("rawtypes")
        MappedConfiguration<Class, ComponentEventResultProcessor> configuration) {
        configuration.addInstance(JsonObject.class, JsonObjectEventResultProcessor.class);
    }
}
