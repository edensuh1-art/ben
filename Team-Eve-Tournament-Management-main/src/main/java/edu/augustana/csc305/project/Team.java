package edu.augustana.csc305.project;
import java.util.*;

public class Team {
    private String name;
    private String mascot;
    private List<Player> players;
    private int spiritRating;
    private int winScore;

    public Team(String name) {
        this.name = name;
        this.mascot = "";
        this.players = new ArrayList<>();
        this.spiritRating = 0;
        this.winScore = 0;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Player> getPlayers() { return players; }
    public void addPlayer(Player player) { players.add(player); }
    public void addPlayerList(List<Player> players) {
        this.players.addAll(players);
    }
    //public Int getScore

    @Override
    public String toString() { return name; }
}
