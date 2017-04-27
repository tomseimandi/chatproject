package org.seimandi.chatproject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SocketClass {
	private static MulticastSocket multicastSocket;

    private static void createMultiCastSocket() {
        try {
            multicastSocket = new MulticastSocket(3333);
            multicastSocket.joinGroup(InetAddress.getByName("224.0.0.1"));
	    } catch (IOException e) {
	    	e.printStackTrace();
        }
    }
    
    public static MulticastSocket socket() {
        if(multicastSocket == null || !multicastSocket.isConnected()) {
            createMultiCastSocket();
        }
        return multicastSocket;
    }
}
