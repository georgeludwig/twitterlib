package com.sixbuilder.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.TapestryIOCModule;
import org.apache.tapestry5.services.TapestryModule;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.sixbuilder.actionqueue.AbstractTest;
import com.sixbuilder.twitterlib.services.TwitterLibModule;

@SubModule({TapestryModule.class, TapestryIOCModule.class, TwitterLibModule.class})
public class TestModule {

    @Contribute(SymbolProvider.class)
    @ApplicationDefaults
    public static void setDefaults(MappedConfiguration<String, Object> defaults){
        defaults.add(SymbolConstants.PRODUCTION_MODE, false);
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
