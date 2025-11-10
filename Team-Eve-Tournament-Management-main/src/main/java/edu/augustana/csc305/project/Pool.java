package edu.augustana.csc305.project;
import java.util.*;
public class Pool{
	private List<Team> teams; //must be at least 2
	private Court court;
	private List<Match> matches;

	public Pool(){
		teams = new ArrayList<>();
		court = null;
	}

	public void addTeam(Team team){
		teams.add(team);
	}

	public List<Team> getTeams(){
		return teams;
	}

	public void setCourt(Court court){
		this.court = court;
	}

	public Court getCourt(){
		return court;
	}

	public void setMatches(List<Match> matches){
		this.matches = matches;
	}

	public List<Match> getMatches(){
		return matches;
	}
}
