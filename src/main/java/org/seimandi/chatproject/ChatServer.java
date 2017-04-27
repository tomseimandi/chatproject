package org.seimandi.chatproject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;

public class ChatServer extends Thread {
	private HomeFrame hFrame;
	
	private boolean running = true;
	public boolean getRunning() {
		return running;
	}
	public void setRunning (boolean running) {
		this.running = running;
	}
	
	public ChatServer(HomeFrame hFrame) {
		super("ServerThread" + hFrame.getUser().getName());
		this.hFrame = hFrame;
	}
	
	public void run() {
		while(running) {
			try {
				processMessage(receiveMessage());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Message receiveMessage() throws IOException, ClassNotFoundException {
		byte[] buf = new byte[1024];
		DatagramPacket datagram = new DatagramPacket(buf, buf.length);
		SocketClass.socket().receive(datagram);
		ByteArrayInputStream bInputStream = new ByteArrayInputStream(buf);
		ObjectInputStream oInputStream = new ObjectInputStream(bInputStream);
		Message message = (Message) oInputStream.readObject();
		return message;
	}
	
	private void processMessage(Message message) {
		if (message.getContent().equals("[[DISCONNECTING]]")) {
			removeUser(message.getFrom().getName());
			exit();
		} else if (message.getContent().equals("[[CONNECTED]]")) {
			updateUserState(message.getFrom());
		} 
		else if (message instanceof PrivateMessage) {
			PrivateMessage privateMessage = (PrivateMessage) message;
			if (privateMessage.getTo().compareTo(hFrame.getUser())) {
				hFrame.receiveMessage(privateMessage);
			}
		} else {
			hFrame.addMessage(message);
		}
		hFrame.updateUserList();
	}
	
	private void updateUserState(User u) {
		if (hFrame.getOnlineUsers().containsKey(u.getName())) {
		} else {
			hFrame.getOnlineUsers().put(u.getName(), u);
		}
	}
	
	private void removeUser(String name) {
		hFrame.getOnlineUsers().remove(name);
		hFrame.updateUserList();
	}
	
	private void exit() {
		hFrame.updateUserList();
		running = false;
		SocketClass.socket().close();
	}
}

