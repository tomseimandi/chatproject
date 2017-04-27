package org.seimandi.chatproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	public static Connection conn;
	
	public static boolean requestDB(String username, String password) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch(Exception e) {
			System.out.println("Erreur de chargement du driver.");
			System.exit(0);
		} 
		
		try {
			String url = "jdbc:sqlite:sample.db";
			conn = DriverManager.getConnection(url);
		} catch(Exception e) {
			System.out.println("Erreur de connexion à la base de données.");
		}
		
		try {
			PreparedStatement statement = conn.prepareStatement("SELECT password FROM login WHERE id = '"+username+"'");
			ResultSet resultat = statement.executeQuery();
			if (resultat.next()) {
				String motDePasse = resultat.getString(1);
				if(motDePasse.equals(password)) {
					conn.close();
					return true;
				} else { 
					conn.close();
					return false;
				}
			} else {
				conn.close();
				return false;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}	
	
	public static void register(String username, String password) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch(Exception e) {
			System.out.println("Erreur de chargement du driver.");
			System.exit(0);
		} 
		
		try {
			String url = "jdbc:sqlite:sample.db";
			conn = DriverManager.getConnection(url);
		} catch(Exception e) {
			System.out.println("Erreur de connexion à la base de données.");
		}
		try {
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(20);
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS login (id STRING NOT NULL PRIMARY KEY, password STRING NOT NULL)");
			statement.executeUpdate("INSERT INTO login VALUES('"+username+"','"+password+"')");
			conn.close();
		} catch(SQLException e) {
		      System.err.println(e.getMessage());
		}
	}
}
