package org.seimandi.chatproject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class ConnectedThread extends Thread{
	private User user;
	
	public User getUser() {
		return user;
	}
	
	private boolean running = true;
	
	public boolean getRunning() {
		return running;
	}
	
	public void setRunning(boolean r) {
		this.running = r;
	}
	
	public ConnectedThread(User u) {
		super("ConnectedThread" + u);
		this.user = u;
	}
	
	public void run() {
		while(running) {
			try {
				Message message = new Message(user, "[[CONNECTED]]");
				ByteArrayOutputStream bOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream oOutputStream = new ObjectOutputStream(bOutputStream);
				oOutputStream.writeObject(message);
				oOutputStream.flush();
				
				DatagramPacket messToSend = new DatagramPacket(bOutputStream.toByteArray(),bOutputStream.size(), InetAddress.getByName("224.0.0.1"), 3333);
				SocketClass.socket().send(messToSend);
				sleep(1000);	
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
