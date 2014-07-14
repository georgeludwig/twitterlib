package com.sixbuilder.services;

import com.sixbuilder.twitterlib.services.TwitterLibModule;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.TapestryIOCModule;
import org.apache.tapestry5.services.TapestryModule;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

@SubModule({TapestryModule.class, TapestryIOCModule.class, TwitterLibModule.class})
public class TestModule {

    @Contribute(SymbolProvider.class)
    @ApplicationDefaults
    public static void setDefaults(MappedConfiguration<String, Object> defaults){
        defaults.add(SymbolConstants.PRODUCTION_MODE, false);
    }

    public static CouchDbClient buildCouchDbClient(){
        CouchDbProperties properties = new CouchDbProperties()
            .setDbName("test")
            .setHost("tawus.cloudant.com")
            .setProtocol("https")
            .setPort(443)
            .setUsername("coneryouldistabitstolder")
            .setPassword("yPuMlWaQSSGN7KeA0tpxp64j");

        return new CouchDbClient(properties);
    }

}
