package com.sixbuilder.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.got5.tapestry5.jquery.JQuerySymbolConstants;

import com.sixbuilder.actionqueue.AbstractTest;
import com.sixbuilder.twitterlib.services.TweetItemDAO;
import com.sixbuilder.twitterlib.services.TwitterLibModule;

/**
 * This module is automatically included as part of the Tapestry IoC Registry, it's a good place to
 * configure and extend Tapestry, or to place your own service definitions.
 */
@SubModule(TwitterLibModule.class)
public class AppModule
{
    public static void bind(ServiceBinder binder)
    {
         binder.bind(TweetItemDAO.class, TweetItemDAOImpl.class);
         binder.bind(TweetDAO.class, TweetDAOImpl.class);
    }

    public static void contributeFactoryDefaults(
            MappedConfiguration<String, Object> configuration)
    {
        // The application version number is incorprated into URLs for some
        // assets. Web browsers will cache assets because of the far future expires
        // header. If existing assets are changed, the version number should also
        // change, to force the browser to download new versions. This overrides Tapesty's default
        // (a random hexadecimal number), but may be further overriden by DevelopmentModule or
        // QaModule.
        configuration.override(SymbolConstants.APPLICATION_VERSION, "1.0-SNAPSHOT");
        configuration.override(SymbolConstants.HMAC_PASSPHRASE, "sadjlhsadsdilçahsdfajklçjfh");
    }

    public static void contributeApplicationDefaults(
            MappedConfiguration<String, Object> configuration)
    {
        // Contributions to ApplicationDefaults will override any contributions to
        // FactoryDefaults (with the same key). Here we're restricting the supported
        // locales to just "en" (English). As you add localised message catalogs and other assets,
        // you can extend this list of locales (it's a comma separated series of locale names;
        // the first locale name is the default when there's no reasonable match).
    	configuration.add(JQuerySymbolConstants.SUPPRESS_PROTOTYPE, "false");
    	configuration.add(JQuerySymbolConstants.JQUERY_ALIAS, "$j");
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
    }
    
    public static CouchDbConnector buildCBC() throws Exception {
    	HttpClient httpClient = new StdHttpClient.Builder()
			.host(AbstractTest.DBACCOUNT+".cloudant.com").port(443).username(AbstractTest.DBACCOUNT)
			.password(AbstractTest.DBPWD).enableSSL(true)
			.relaxedSSLSettings(true).build();
    	
    	CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector db = new StdCouchDbConnector(AbstractTest.QUEUE_TEST_DB_NAME, dbInstance);
		db.createDatabaseIfNotExists();
		return db;
    }
    
}