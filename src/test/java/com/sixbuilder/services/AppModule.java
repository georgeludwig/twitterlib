package com.sixbuilder.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.got5.tapestry5.jquery.JQuerySymbolConstants;

import com.sixbuilder.twitterlib.services.QueueItemDAO;
import com.sixbuilder.twitterlib.services.QueueItemDAOImpl;
import com.sixbuilder.twitterlib.services.QueueSettingsDAO;
import com.sixbuilder.twitterlib.services.QueueSettingsDAOImpl;
import com.sixbuilder.twitterlib.services.TweetDAO;
import com.sixbuilder.twitterlib.services.TweetDAOImpl;
import com.sixbuilder.twitterlib.services.TweetItemDAO;
import com.sixbuilder.twitterlib.services.TweetItemDAOImpl;
import com.sixbuilder.twitterlib.services.TwitterLibModule;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
@SubModule(TwitterLibModule.class)
public class AppModule {
	
	public static void bind(ServiceBinder binder) {
		binder.bind(TweetItemDAO.class, TweetItemDAOImpl.class);
		binder.bind(TweetDAO.class, TweetDAOImpl.class);
		binder.bind(QueueSettingsDAO.class, QueueSettingsDAOImpl.class);
		binder.bind(QueueItemDAO.class, QueueItemDAOImpl.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
		// The application version number is incorprated into URLs for some
		// assets. Web browsers will cache assets because of the far future
		// expires
		// header. If existing assets are changed, the version number should
		// also
		// change, to force the browser to download new versions. This overrides
		// Tapesty's default
		// (a random hexadecimal number), but may be further overriden by
		// DevelopmentModule or
		// QaModule.
		configuration.override(SymbolConstants.APPLICATION_VERSION, "1.0-SNAPSHOT-a");
		configuration.override(SymbolConstants.HMAC_PASSPHRASE, "sadjlhsadsdilçahsdfajklçjfh");
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		// Contributions to ApplicationDefaults will override any contributions
		// to
		// FactoryDefaults (with the same key). Here we're restricting the
		// supported
		// locales to just "en" (English). As you add localised message catalogs
		// and other assets,
		// you can extend this list of locales (it's a comma separated series of
		// locale names;
		// the first locale name is the default when there's no reasonable
		// match).
		configuration.add(JQuerySymbolConstants.SUPPRESS_PROTOTYPE, "false");
		configuration.add(JQuerySymbolConstants.JQUERY_ALIAS, "$j");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
	}

//	public static CouchDbConnector buildCouchDbConnector() throws Exception {
//		HttpClient httpClient = new StdHttpClient.Builder().host(AbstractTestSixBuilder.DBACCOUNT + ".cloudant.com").port(443).username(AbstractTestSixBuilder.DBACCOUNT).password(AbstractTestSixBuilder.DBPWD).enableSSL(true).relaxedSSLSettings(true)
//				.build();
//		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
//		CouchDbConnector db = new StdCouchDbConnector(QueueSettingsRepository.DBNAME, dbInstance);
//		db.createDatabaseIfNotExists();
//		return db;
//	}
//
//	public static QueueItemRepository buildQueueItemRepository() {
//		HttpClient httpClient = new StdHttpClient.Builder().host(AbstractTestSixBuilder.DBACCOUNT + ".cloudant.com").port(443).username(AbstractTestSixBuilder.DBACCOUNT).password(AbstractTestSixBuilder.DBPWD).enableSSL(true).relaxedSSLSettings(true)
//				.build();
//		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
//		CouchDbConnector db = new StdCouchDbConnector(QueueItemRepository.DBNAME, dbInstance);
//		db.createDatabaseIfNotExists();
//		QueueItemRepository r = new QueueItemRepository(db);
//		return r;
//	}
//
//	QueueSettingsRepository buildQueueSettingsRepository() {
//		HttpClient httpClient = new StdHttpClient.Builder().host(AbstractTestSixBuilder.DBACCOUNT + ".cloudant.com").port(443).username(AbstractTestSixBuilder.DBACCOUNT).password(AbstractTestSixBuilder.DBPWD).enableSSL(true).relaxedSSLSettings(true)
//				.build();
//		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
//		CouchDbConnector db = new StdCouchDbConnector(QueueSettingsRepository.DBNAME, dbInstance);
//		db.createDatabaseIfNotExists();
//		QueueSettingsRepository r = new QueueSettingsRepository(db);
//		return r;
//	}

}