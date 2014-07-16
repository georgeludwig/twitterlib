package com.sixbuilder.pages;

import javax.inject.Inject;

import org.apache.tapestry5.annotations.Property;
import org.lightcouch.NoDocumentException;

import com.sixbuilder.twitterlib.services.QueueManager;

//@Exclude(stylesheet = {"core"})  //If you do not want Tapestry CSS
//@Import(stylesheet = {
//    "context:/bootstrap/css/bootstrap.css",
//    "context:/bootstrap/css/bootstrap-responsive.css",
//    "context:/font-awesome/css/font-awesome.css",
//    "context:/style.css"
//}
//,
//    library = {
//        "context:/bootstrap/js/bootstrap.js"
//    }
//)
public class QueueTestPage {

    @Property
    private String queueId;

    @Inject
    private QueueManager queueManager;

    void onActivate() {
        try {
            queueId = "queue-test";
            queueManager.get(queueId);
        } catch (NoDocumentException ignore) {
            queueManager.create(queueId);
        }
    }

}
