package com.sixbuilder.twitterlib.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.sixbuilder.twitterlib.services.FontAwesomeService;


/**
 * Component that allows easy inclusion of <a href="http://fortawesome.github.io/Font-Awesome/icons/">Font Awesome</a>
 * icons.
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 */
public class FontAwesomeIcon {
	
	/**
	 * Name of the icon.
	 */
	@Parameter(required = true, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
	private String name;
	
	@Inject
	private FontAwesomeService fontAwesomeService;
	
	boolean beginRender(MarkupWriter writer) {
		fontAwesomeService.setup(writer);
		writer.element("i", "class", "fa " + name);
		writer.end();
		return false;
	}
	
}
