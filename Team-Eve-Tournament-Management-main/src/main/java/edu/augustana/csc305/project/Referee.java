package edu.augustana.csc305.project;
public class Referee{
	private final String name;
	//private String username;
	//private final String password;
	private final Court court; //court assignment

	public Referee(String name, Court court){
		this.name = name;
		this.court = court;
	}
}
