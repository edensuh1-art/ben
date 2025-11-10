package edu.augustana.csc305.project;
//don't do anything with player info during this sprint to make things simpler
public class Player{
	private String name;
	private int age; //limit inputs to numbers
	private String height; //limit inputs to numbers
	private String weight; //limit inputs to numbers
	private String position;
	private String skills;

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
}
