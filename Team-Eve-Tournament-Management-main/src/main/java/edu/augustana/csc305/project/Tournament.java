package edu.augustana.csc305.project;
import java.time.LocalDateTime;
import java.util.*;

public class Tournament implements java.io.Serializable {
    private String ID;
    private String name;
    private LocalDateTime start;
    private List<Court> courts;
    private List<Team> teams;
    private List<Match> matches;
    private List<Match> runningMatches; // matches currently being played, remove when finished
	private String category;// to determine what type of bracket
	private List<Pool> pools;
    private static int idCounter = 0;
    //private final Organizer creator = null; //initialize from logged in user

    public Tournament(String name, LocalDateTime start) {
        this.name = name;
        this.start = start;
        this.courts = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.runningMatches = new ArrayList<>();
        this.pools = new ArrayList<>();
        this.category = "None selected";
        idCounter++;
        this.ID = "T" + idCounter;
        
    }
    public Tournament(){ //for save file, not reccomended for creation
        this("", null);
    }

    //ID
    public String getID() { return ID; }
    public static int getNextIdIndex() { return idCounter; } //to save counter
    public static void setNextIdIndex(int count) { idCounter = count; } //to restore counter
    //name
    public String getName() { return name; }
    public void setName(String name) { if (!name.equals("") && !name.isBlank()) this.name = name; }
    //start date and time
    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { if (start != null) this.start = start; }
    //courts
    public List<Court> getCourts() { return courts; }
    public void addCourts(List<Court> courts) { if (!courts.isEmpty() && courts != null) this.courts.addAll(courts); }
    public void addCourt() { courts.add(new Court(courts.size() + 1)); }
    public void addCourt(Court court) { if (court != null) courts.add(court);}
    //teams
    public List<Team> getTeams() { return teams; }
    public void addTeam(Team team) { if (team != null) teams.add(team); }
    public void addTeams(List<Team> teams) { if (!teams.isEmpty() && teams != null) this.teams.addAll(teams);  }
    //matches
    public List<Match> getMatches() { return matches; }
    public void addMatch(Match match) { if (match != null) matches.add(match); }
    public void addMatches(List<Match> matches) { if (!matches.isEmpty() && matches != null) this.matches.addAll(matches); }
    //running matches
    public List<Match> getRunningMatches() { return runningMatches;}
    public void addMatchToRunning(Match match) { if (match != null) runningMatches.add(match); }
    public void addMatchesToRunning(List<Match> matches) { if (!matches.isEmpty() && matches != null) runningMatches.addAll(matches); }
    public void finishMatch(Match match) { 
        if (match != null && matches.contains(match)) {
            match.endMatch();
            matches.remove(match);
            runningMatches.remove(match);
        }
    }
    public void startMatch(Match match) {
        if (match != null && matches.contains(match)){
            match.start();
            addMatchToRunning(match);
        }
    }
    //pools
    public List<Pool> getPools() { return pools; }
    public void setPools(List<Pool> poolsList) { if (!poolsList.isEmpty() && poolsList != null) this.pools = poolsList; }
    public void addPools(List<Pool> poolsList) { if (!poolsList.isEmpty() && poolsList != null) this.pools.addAll(poolsList); }
    public void addPool(Pool pool) { if (pool != null) this.pools.add(pool); }
    //category
    public String getCategory() { return category; }
    public void setCategory(String category) { if (category.equals("Volleyball") || category.equals("Soccer")) this.category = category; }
    //bracket methods
	public void generateBracket() {
        if (category.equals("Volleyball")) {
            return;
        } else if (category.equals("Soccer")) {
            return;
        }
    }
	public void displayBracket() {}

    public void resolveReferences() {
        // build map of team id -> Team
        Map<String, Team> teamMap = new java.util.HashMap<>();
        if (teams != null) {
            for (Team team : teams) {
                if (team != null && team.getID() != null) teamMap.put(team.getID(), team);
            }
        }

        // resolve matches
        if (matches != null) {
            for (Match match : matches) {
                if (match.getHomeID() != null) {
                    Team h = teamMap.get(match.getHomeID());
                    if (h != null) match.setHome(h);
                }
                if (match.getAwayID() != null) {
                    Team a = teamMap.get(match.getAwayID());
                    if (a != null) match.setAway(a);
                }
            }
        }

        // resolve pools
        if (pools != null) {
            for (Pool pool : pools) {
                pool.resolveTeamsByID(teamMap);
            }
        }
    }

    @Override
    public String toString() { return name + " (" + (start != null ? start : "no start time") + ")"; }
}

