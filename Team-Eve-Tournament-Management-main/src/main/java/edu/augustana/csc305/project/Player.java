package edu.augustana.csc305.project;
//don't do anything with player info during this sprint to make things simpler
public class Player{
	private String name;
	private int age; //limit inputs to numbers
	private String height; //limit inputs to numbers
	private String weight; //limit inputs to numbers
	private String position;
	private String skills;
	private Team team;

	//leave implementations alone, the organizer will be able to edit them after creating
	public Player(String name){
		this.name = name;
		age = 0;
		height = "0ft 0in"; //made of two inputs, feet and inches
		weight = "0lbs";
		position = "None entered";
		skills = "None entered";
	}

	public String getName(){
		return name;
	}

	public int getAge(){
		return age;
	}

	public String getHeight(){
		return height;
	}

	public String getWeight(){
		return weight;
	}

	public String getposition(){
		return position;
	}

	public String getSkills(){
		return skills;
	}

	public Team getTeam() {
		return team;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAge(int age) {
		this.age = Math.max(0, age);
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public String getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return name == null || name.isBlank() ? "Unnamed Player" : name;
	}
}