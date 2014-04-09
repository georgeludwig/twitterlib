package com.sixbuilder.twitterlib.services;

import org.apache.tapestry5.MarkupWriter;

import com.sixbuilder.twitterlib.components.FontAwesomeIcon;

/**
 * Service that allows easy inclusion of <a href="http://fortawesome.github.io/Font-Awesome/icons/">Font Awesome</a>
 * 
 * @author Thiago H. de Paula Figueiredo (http://machina.com.br/thiago)
 * @see FontAwesomeIcon
 */
public interface FontAwesomeService {

	/**
	 * Sets up the page for Font Awesome use.
	 * @param writer a {@link MarkupWriter}.
	 */
	void setup(MarkupWriter writer);
	
}
