package edu.augustana.csc305.project;
import java.util.*;
public class Pool implements java.io.Serializable{
	private String ID; //do not user change this
	private static int idCounter = 0;
	private List<Team> teams; //must be at least 2
	private List<String> teamIDs;
	private Court court;
	private List<Match> matches;

	public Pool(){
		idCounter++;
		ID = "pl" + idCounter;
		teams = new ArrayList<>();
		teamIDs = new ArrayList<>();
		court = null;
		matches = new ArrayList<>();
	}

	//ID
	public String getID() { return ID; }
	public void setID(String id) { ID = id; }
    public static int getNextIdIndex() { return idCounter; } //to save counter
    public static void setNextIdIndex(int count) { idCounter = count; } //to restore counter
	public List<String> getTeamIDs() { return teamIDs; }
	public void setTeamIDs(List<String> ids) { teamIDs = ids; }
	//teams
	public List<Team> getTeams(){ return teams; }
	public void addTeam(Team team){
		if (team != null) {
			teams.add(team);
			if (team.getID() != null && !teamIDs.contains(team.getID())) teamIDs.add(team.getID());
		}
	}
	//court
	public Court getCourt(){ return court; }
	public void setCourt(Court court){ if (court != null) this.court = court; }
	//matches
	public List<Match> getMatches(){ return matches; }
	public void setMatches(List<Match> matches){
		if (matches != null && !matches.isEmpty()) {
			this.matches = matches;
		}
	}
	public void addMatch(Match match) { if (match != null) matches.add(match); }

	//for after file loading - populates teams from ids
	public void resolveTeamsByID(java.util.Map<String, Team> teamMap) {
		teams.clear();
		if (teamIDs == null) return;
		for (String id : teamIDs) {
			Team team = teamMap.get(id);
			if (team != null) teams.add(team);
		}
	}
}
