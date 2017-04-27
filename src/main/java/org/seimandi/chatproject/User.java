package org.seimandi.chatproject;

import java.io.Serializable;

public class User implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
    
    public User(String name) {  
        this.name = name;
    }
    
    public String getName() {
    	return name;
    }
    
    public boolean compareTo(User u) {
        if(u != null) {
            return name.equals(u.name);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

