package edu.augustana.csc305.project;

public class Referee implements java.io.Serializable{
	private String name;
	//private String username;
	//private final String password;
	private Court court; //court assignment

	public Referee(String name, Court court){
		this.name = name;
		this.court = court;
	}
}
