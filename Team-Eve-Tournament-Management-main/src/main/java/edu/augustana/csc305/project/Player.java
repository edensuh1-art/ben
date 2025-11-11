package edu.augustana.csc305.project;

public class Player implements java.io.Serializable {
	private String ID; //Do not allow user to change this
	private String name;
	private int age; //limit inputs to numbers
	private String height; //limit inputs to numbers
	private String weight; //limit inputs to numbers
	private String position;
	private String skills;
	private static int idCounter = 0;
	private Team team;

	//leave implementations alone, the organizer will be able to edit them after creating
	public Player(String name){
		idCounter++;
		this.ID = "plr" + idCounter;
		this.name = name;
		age = 0;
		height = "0ft 0in"; //use formatter? like with start in tournament?
		weight = "0lbs";
		position = "None entered";
		skills = "None entered";
	}
	public Player(){ //for save file, do not reccomend for creation
		this("");
	}
	
	//ID
	public String getID() { return ID; }
	public void setID(String id) { ID = id; }
    public static int getNextIdIndex() { return idCounter; } //to save counter
    public static void setNextIdIndex(int count) { idCounter = count; } //to restore counter
	//name
	public String getName(){ return name; }
	public void setName(String name){
		if (name != null && !name.isBlank()) {
			this.name = name;
		}
	}
	//age
	public int getAge(){ return age; }
	public void setAge(int age){ if (age > 0) this.age = age; }
	//height
	public String getHeight(){ return height; }
	public void setHeight(String height){ this.height = height; }
	//weight
	public String getWeight(){ return weight;}
	public void setWeight(String weight){ this.weight = weight; }
	//position
	public String getPosition(){ return position;}
	public void setPosition(String position){ this.position = position; }
	//skills
	public String getSkills(){ return skills; }
	public void setSkills(String skills){ this.skills = skills; }
	//team
	public Team getTeam() { return team; }
	public void setTeam(Team team) { this.team = team; }

	@Override
	public String toString() {
		return name == null || name.isBlank() ? "Unnamed Player" : name;
	}
}
