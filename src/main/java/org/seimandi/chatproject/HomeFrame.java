package org.seimandi.chatproject;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class HomeFrame extends Frame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TextArea messageTextArea;
    private TextField textField;
    private JList<User> onlineUsersJList;
    private ScrollPane onlineUsersPane;

    private WindowAdapter windowCloseListener;
    private ActionListener messageListener;
    private MouseAdapter mouseListener;

    private Map<String, User> onlineUsers;
    public Map<String, User> getOnlineUsers() {
    	return onlineUsers;
    }
    
    private Map<String, PrivateMessageFrame> privateMessages;
    private User user;
    public User getUser() {
    	return user;
    }
    
    private ChatServer chatServer;
    private ConnectedThread connectedThread;
    
    private static String connexionName = null;
    private static int closeInd = 0;
    
    public HomeFrame(String username) {
        super("Salle commune de Chat [" + username + "]");

        user = new User(username);
        onlineUsers = new TreeMap<String, User>();
        privateMessages = new TreeMap<String, PrivateMessageFrame>();

        initView();
        
        connectedThread = new ConnectedThread(user);
        connectedThread.start();

        chatServer = new ChatServer(this);
        chatServer.start();
    }
    
    public void initView() {
    	windowCloseListener = new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    			disconnect();
    			dispose();
    		}
    	};
    	
    	messageListener = new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			TextField temp = (TextField) e.getSource();
    			sendMessage(new Message(user, temp.getText()));
    			temp.setText("");
    		}
    	};
    	
    	mouseListener = new MouseAdapter() {
    		public void mouseClicked(MouseEvent e){
    			if (e.getClickCount() >= 1) {
    				@SuppressWarnings("unchecked")
					JList<User> temp = (JList<User>) e.getSource();
    				User chatTo = temp.getSelectedValue();
    				if (chatTo == null) {
    					System.out.println("test chat to noone");
    				} else if (user.compareTo(chatTo)) {
    					System.out.println("test self chat");
    				} else {
    					openPrivateChat(new PrivateMessage(user, chatTo, "test"));
    				}
    			}
    		}
    	};
    	
    	onlineUsersJList = new JList<User>();
    	onlineUsersJList.addMouseListener(mouseListener);
    	onlineUsersJList.setSize(new Dimension(60, 250));
    	updateUserList();
    	onlineUsersPane = new ScrollPane();
    	onlineUsersPane.add(onlineUsersJList);
    	messageTextArea = new TextArea();
    	messageTextArea.setEditable(false);
    	messageTextArea.setSize(300, 280);
    	textField = new TextField();
    	textField.addActionListener(messageListener);
    	addWindowListener(windowCloseListener);
    	
    	setSize(640, 480);
    	setLayout(new BorderLayout());
    	add("Center", messageTextArea);
    	add("South", textField);
    	add("East", onlineUsersPane);
    	
    	setVisible(true);
    }

    public void sendMessage(Message message) {
    	try {
    		ByteArrayOutputStream bOutputStream = new ByteArrayOutputStream();
    		ObjectOutputStream oOutputStream = new ObjectOutputStream(bOutputStream);
    		oOutputStream.writeObject(message);
    		oOutputStream.flush();
    		DatagramPacket datagram = new DatagramPacket(bOutputStream.toByteArray(), bOutputStream.size(), InetAddress.getByName("224.0.0.1"), 3333);
    		SocketClass.socket().send(datagram);
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void receiveMessage(PrivateMessage privateMessage) {
    	if (!privateMessages.containsKey(privateMessage.getFrom().getName()) || !privateMessages.get(privateMessage.getFrom().getName()).isActive()) {
    		privateMessages.put(privateMessage.getFrom().getName(), new PrivateMessageFrame(this, privateMessage.getFrom()));
    	}
    	privateMessages.get(privateMessage.getFrom().getName()).append(privateMessage);
    }
    
    public void addMessage(Message message) {
    	messageTextArea.append("\n" + message);
        messageTextArea.setCaretPosition(messageTextArea.getText().length());
    }
    
    private void disconnect() {
    	sendMessage(new Message(user, "[[DISCONNECTING]]"));
    	chatServer.setRunning(false);
    	connectedThread.setRunning(false);
    }
    
    public void openPrivateChat(PrivateMessage privateMessage) {
    	if (!privateMessages.containsKey(privateMessage.getTo().getName()) || !privateMessages.get(privateMessage.getTo().getName()).isActive()) {
    		privateMessages.put(privateMessage.getTo().getName(), new PrivateMessageFrame(this, privateMessage.getTo()));
    	}
    	privateMessages.get(privateMessage.getTo().getName()).append(privateMessage);
    }
   
    public void updateUserList() {
    	DefaultListModel<User> userListModel = new DefaultListModel<User>();
    	synchronized(onlineUsers) {
    		for (User u : onlineUsers.values()) {
    			if (!u.equals(this.user)) {
    				userListModel.addElement(u);
    			}
    		}
    	}
    	onlineUsersJList.setModel(userListModel);
    }
    
    public static void popMainMenu() {
    	Object[] options = {"S'inscrire",
                "Se connecter",
                "Quitter"};
    	int n = JOptionPane.showOptionDialog(null, null, "Chat - Menu Principal", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
    	if (n == JOptionPane.YES_OPTION) {
    		popRegisterMenu();
    	} else if (n == JOptionPane.NO_OPTION) {
    		popConnexionMenu();
    	} else if (n == JOptionPane.CANCEL_OPTION) {
    		closeInd = 1;
    		return;
    	}
    }
    
    public static void popRegisterMenu() {
    	JTextField username = new JTextField();
    	JTextField password = new JPasswordField();
    	Object[] message = {
    	    "Entrez un nom d'utilisateur:", username,
    	    "Entrez un mot de passe:", password
    	};

    	int option = JOptionPane.showConfirmDialog(null, message, "Enregistrement", JOptionPane.OK_CANCEL_OPTION);
    	if (option == JOptionPane.OK_OPTION) {
    	    Database.register(username.getText(), password.getText());
    	} else {
    	    System.out.println("Login canceled");
    	}
    	popMainMenu();
    }
    
    public static void popConnexionMenu() {
    	JTextField username = new JTextField();
    	JTextField password = new JPasswordField();
    	Object[] message = {
    	    "Username:", username,
    	    "Password:", password
    	};

    	int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
    	if (option == JOptionPane.OK_OPTION) {
    	    if (Database.requestDB(username.getText(), password.getText())) {
    	        System.out.println("Login successful");
    	        connexionName = username.getText();
    	    } else {
    	        System.out.println("Login failed");
    	    }
    	} else {
    	    System.out.println("Login canceled");
    	}
    	return;
    }
    
    public static void main(String args[]) {
    	System.setProperty("java.net.preferIPv4Stack", "true");
    	
    	while (connexionName == null && closeInd == 0) {
    		popMainMenu();
    	}
    	
    	if (closeInd == 1) {
    		return;
    	}

    	new HomeFrame(connexionName);
    }   
}
