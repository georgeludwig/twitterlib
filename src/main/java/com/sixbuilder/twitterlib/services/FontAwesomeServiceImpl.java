package com.sixbuilder.twitterlib.services;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * Default implementation of {@link FontAwesomeService}.
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class FontAwesomeServiceImpl implements FontAwesomeService {

	private boolean alreadyAdded = false;
	
	@Inject
	private Request request;

	public void setup(MarkupWriter writer) {
		if (!alreadyAdded && !request.isXHR()) {
			final Element head = writer.getDocument().getRootElement().find("head");
			head.element("link", "rel", "stylesheet", "href", "//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css");
			alreadyAdded = true;
		}
	}

}
