package edu.augustana.csc305.project;

import java.io.File;

abstract class User{ //abstract for varied user types - only organizer for now
	private String name;
	private final String username;
	private final String password;
	//private String permissionLevel;

	//default when not logged in
	public User(){
		this("Visitor", "");
	}

	//for when logged in, Organizer (no others for now)
	public User(String username, String password){
		this.name = "";
		this.username = username;
		this.password = password;
	}

	public void setName(String name){ this.name = name; }
	public String getName(){ return name; }

	public String getUsername(){ return username; }

	public String getPassword(){ return password; }

	public File getUserFile(){
		File baseDirect = new File("save");
		if (!baseDirect.exists()) baseDirect.mkdirs();
		String safe = username.replaceAll("[^A-Za-z0-9_.-]", "_");
		return new File(baseDirect, safe + ".json");
	}
	
}
