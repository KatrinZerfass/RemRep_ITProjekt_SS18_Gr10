package com.google.gwt.sample.itProjekt.shared.report;

import java.io.Serializable;


// 
/**
 * Die Klasse SimpleParagraph, welche reinen Text enth�lt.
 * @author Anna-MariaGmeiner
 */
public class SimpleParagraph extends Paragraph implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID=1L;
	
	/** Der String text. */
	private String text; 
	
	/**
	 * Konstruktor f�r den SimpleParagraph. 
	 *
	 * @param text the text
	 */
	public SimpleParagraph(String text) {
		this.text=text;
	}
	public SimpleParagraph() {
		
	}
	
	/**
	 * Setter f�r den String text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text=text;
	}
	
	/**
	 * Getter f�r den String text.
	 *
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}


@Override
	public String toString() {
		return this.text;
	}
}
