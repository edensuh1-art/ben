package edu.augustana.csc305.project;
abstract class User{
	private String name;
	private String username;
	private final String password;
	//private String permissionLevel;

	//default when not logged in
	public User(){
		this("Visitor", "");
	}

	//for when logged in, Organizer and Referee
	public User(String username, String password){
		this.name = "";
		this.username = username;
		this.password = password;
	}

	public void setName(String name){
		this.name = name;
	}
	
}
