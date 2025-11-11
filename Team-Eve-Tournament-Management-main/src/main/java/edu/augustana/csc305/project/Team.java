package edu.augustana.csc305.project;
import java.util.*;

public class Team implements java.io.Serializable {
    private String ID; //do not allow user to change this
    private String name;
    private String mascot;
    private List<Player> players;
    private int spiritRating;
    private int winScore; //number of wins for the team
    private int pointsScored; //total points scored by the team for their current match/set
    private int setWins; //number of sets won in current match (for volleyball)
    private static int idCounter = 0;
    private boolean isAvailable = true; //true if not in a running match
    private String photoUrl;

    public Team(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Team must have a name");
        idCounter++;
        this.ID = "tm" + idCounter;
        this.name = name;
        this.mascot = "";
        this.players = new ArrayList<>();
        this.photoUrl = "";
        this.spiritRating = 0;
        this.winScore = 0;
        this.pointsScored = 0;
    }
    public Team(){ //for save file, not reccomended for creation
        idCounter++;
        this.ID = "tm" + idCounter;
        this.name = "";
        this.mascot = "";
        this.players = new ArrayList<>();
        this.photoUrl = "";
        this.spiritRating = 0;
        this.winScore = 0;
        this.pointsScored = 0;
    }

    //ID
    public String getID() { return ID; }
    public void setID(String id) { ID = id; }
    public static int getNextIdIndex() { return idCounter; } //to save counter
    public static void setNextIdIndex(int count) { idCounter = count; } //to restore counter
    //Name
    public String getName() { return name; }
    public void setName(String name) { if (!name.equals("") && !name.isBlank()) this.name = name; }
    //Players
    public List<Player> getPlayers() { return players; }
    	//elios changes
	public void addPlayer(Player player) { 
        if (player != null) {
            Team currentTeam = player.getTeam();
            if (currentTeam != null && currentTeam != this) {
                currentTeam.removePlayer(player);
            }

            if (!players.contains(player)) {
                players.add(player);
            }
            player.setTeam(this);
        }
    }

    public void addPlayers(List<Player> players) { if (!players.isEmpty() && players != null) players.forEach(this::addPlayer);}
    public void removePlayer(Player player) {
        if (player == null) return;
        if (players.remove(player) && player.getTeam() == this) {
            player.setTeam(null);
        }
    }
    //Wins
    public int getWins() { return winScore; }
    public void addWin() { this.winScore += 1; }
    //current points
    public int getPointsScored() { return pointsScored; }
    public void addPoint(int point) { this.pointsScored += point; }
    //availble for match
    public boolean isAvailable(){ return isAvailable; }
    public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }
    //photourl
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    //for volleyball matches
    public int getSetWins() { return setWins; }
    public void addSetWin() { this.setWins += 1; }
    public void setReset() { //resets team stats for new set in volleyball
        this.pointsScored = 0;
    }

    //to be called by a match
    public void matchReset() { //resets team stats for new match
        this.pointsScored = 0;
        this.setWins = 0; // only used for volleyball
        isAvailable = true;
    }
   
    @Override
    public String toString() { return name; }
}
