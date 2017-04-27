package org.seimandi.chatproject;

import java.awt.TextArea;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class PrivateMessageFrame extends Frame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User from, to;
	private TextArea messageTextArea;
    private TextField outputTextField;

    private WindowAdapter windowCloseListener;
    private ActionListener outputListener;
    
    private HomeFrame hFrame;

    private boolean active = true;
    public boolean isActive() {
    	return active;
    }
    
    public PrivateMessageFrame(HomeFrame hFrame, User to) {
    	super("Conversation privée entre " + hFrame.getUser().getName() + " et " + to.getName());
    	this.hFrame = hFrame;
    	this.from = hFrame.getUser();
    	this.to = to;
    	
    	setFrame();
    }
    
    public void setFrame() {
    	windowCloseListener = new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    			active = false;
    			dispose();
    		}
    	};
    	
    	outputListener = new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			TextField temp = (TextField) e.getSource();
    			send(temp.getText());
    			temp.setText("");
    		}
    	};
    	
    	messageTextArea = new TextArea();
    	messageTextArea.setEditable(false);
    	outputTextField = new TextField();
    	outputTextField.addActionListener(outputListener);
    	addWindowListener(windowCloseListener);
    	setLayout(new BorderLayout());
    	setBounds(480, 480, 720, 480);
    	add("Center", messageTextArea);
    	add("South", outputTextField);
    	setVisible(true);
    }
    
    public void append(PrivateMessage message) {
    	messageTextArea.append("\n" + message);
    	messageTextArea.setCaretPosition(messageTextArea.getText().length());
    }
    
    public void send(String text) {
    	PrivateMessage message = new PrivateMessage(from, to, text);
    	hFrame.sendMessage(message);
    	append(message);
    }
}
