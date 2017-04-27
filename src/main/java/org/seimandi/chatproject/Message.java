package org.seimandi.chatproject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final DateFormat DATEFORMAT = new SimpleDateFormat("dd-MM-yyyy 'à' HH:mm:ss");
	protected Date sent;
	
	protected User from;
	public User getFrom() {
		return from;
	}
	
	protected String content;
	public String getContent() {
		return content;
	}
	
	public Message(User from, String content) {
		this.from = from;
		this.content = content;
		this.sent = new Date();
	}
	
	@Override
	public String toString() {
		return "Envoyé par " + from + " le " + DATEFORMAT.format(sent) + ": " + content;
	}
}
