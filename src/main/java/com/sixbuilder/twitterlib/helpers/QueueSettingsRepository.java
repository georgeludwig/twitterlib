package com.sixbuilder.twitterlib.helpers;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;

import com.sixbuilder.actionqueue.QueueType;
import com.sixbuilder.twitterlib.services.QueueSettings;

@Views({
	@View( name="all", map = "function(doc) { if (doc.startHour) { emit(doc.userName, doc) } }"),
	@View( name="curationByUser", map = "function(doc) { if (doc.queueType=='CURATION') { emit(doc.userId, doc) } }"),
	@View( name="engagementByUser", map = "function(doc) { if (doc.queueType=='ENGAGEMENT') { emit(doc.userId, doc) } }"),
	@View( name="testByUser", map = "function(doc) { if (doc.queueType=='TEST') { emit(doc.userId, doc) } }")
})
public class QueueSettingsRepository extends CouchDbRepositorySupport<QueueSettings> {
	
	public static final String DBNAME="actionqueuesettings";

	public static final String ALL="all";
	public static final String CURATIONBYUSER="curationByUser";
	public static final String ENGAGEMENTBYUSER="engagementByUser";
	public static final String TESTBYUSER="testByUser";

	public QueueSettingsRepository(CouchDbConnector db) {
        super(QueueSettings.class, db);
        initStandardDesignDocument();
	}
	
	public void delete(QueueSettings queueSettings) {
		db.delete(queueSettings);
	}
	
	public QueueSettings getQueueSettings(QueueType queueType,String userId) {
		QueueSettings s=null;
		if(queueType==QueueType.CURATION)
			s=getCurationSettings(userId);
		if(queueType==QueueType.ENGAGEMENT)
			s=getEngagementSettings(userId);
		if(queueType==QueueType.TEST)
			s=getTestSettings(userId);
		if(s==null) {
			s=new QueueSettings();
			s.setQueueType(queueType);
			s.setUserId(userId);
			add(s);
		}
		return s;
	}
	
	public QueueSettings getCurationSettings(String userId) {
		List<QueueSettings>settings=queryView(CURATIONBYUSER,userId);
		if(settings.size()>0)
			return settings.get(0);
		return null;
	}
	
	public QueueSettings getEngagementSettings(String userId) {
		List<QueueSettings>settings=queryView(ENGAGEMENTBYUSER,userId);
		if(settings.size()>0)
			return settings.get(0);
		return null;
	}
	
	public QueueSettings getTestSettings(String userId) {
		List<QueueSettings>settings=queryView(TESTBYUSER,userId);
		if(settings.size()>0)
			return settings.get(0);
		return null;
	}

}
