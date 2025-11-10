package edu.augustana.csc305.project;
import java.time.LocalDateTime;
import java.util.*;

public class Tournament {
    private String name;
    private LocalDateTime start;
    private final List<Court> courts;
    private final List<Team> teams;
    private final List<Match> matches;
	private String category;
	private List<Pool> pools;
	//private final Organizer creator = null; //initialize from logged in user

    public Tournament(String name, LocalDateTime start) {
        this.name = name;
        this.start = start;
        this.courts = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.pools = new ArrayList<>();
        this.category = "None selected";
    }
    public Tournament() {
        this("", null);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public List<Court> getCourts() { return courts; }
    public void addCourts(List<Court> courts) { this.courts.addAll(courts); }
    public void addCourt() { courts.add(new Court(courts.size() + 1)); }

    public List<Team> getTeams() { return teams; }
    public void addTeam(Team team) { teams.add(team); }

    public List<Match> getMatches() { return matches; }
    public void addMatch(Match match) { matches.add(match); }

    public List<Pool> getPools() { return pools;}
    public void setPools(List<Pool> poolsList) { this.pools = poolsList; }
    public void addPools(List<Pool> poolsList) { this.pools.addAll(poolsList); }
    public void addPool(Pool pool) { this.pools.add(pool); }

    public String getCategory() { return category; }
    public void setCategory(String catagory) { this.category = catagory; }

	public void generateBracket() {}
	public void displayBracket() {}

    @Override
    public String toString() { return name + " (" + (start != null ? start : "no start time") + ")"; }
}

