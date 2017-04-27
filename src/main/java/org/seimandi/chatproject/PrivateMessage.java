package org.seimandi.chatproject;

public class PrivateMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected User to;
	public User getFrom() {
		return from;
	}
	public User getTo() {
		return to;
	}
	
	public PrivateMessage(User from, User to, String content) {
		super(from, content);
		this.to = to;
	}
	
	@Override
    public String toString() {
		return "Envoyé par " + from + " le " + DATEFORMAT.format(sent) + ": " + content;
	}
}
