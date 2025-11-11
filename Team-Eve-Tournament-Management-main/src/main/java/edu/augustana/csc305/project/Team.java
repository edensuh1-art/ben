package edu.augustana.csc305.project;
import java.util.*;

public class Team {
    private String name;
    private final String mascot;
    private final List<Player> players;
    private String photoUrl;
    private final int spiritRating;
    private final int winScore;

    public Team(String name) {
        this.name = name;
        this.mascot = "";
        this.players = new ArrayList<>();
        this.photoUrl = "";
        this.spiritRating = 0;
        this.winScore = 0;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Player> getPlayers() { return players; }
    public void addPlayer(Player player) {
        if (player == null) return;

        Team currentTeam = player.getTeam();
        if (currentTeam != null && currentTeam != this) {
            currentTeam.removePlayer(player);
        }

        if (!players.contains(player)) {
            players.add(player);
        }
        player.setTeam(this);
    }

    public void addPlayerList(List<Player> players) {
        if (players == null) return;
        players.forEach(this::addPlayer);
    }

    public void removePlayer(Player player) {
        if (player == null) return;
        if (players.remove(player) && player.getTeam() == this) {
            player.setTeam(null);
        }
    }

    public String getPhotoUrl() { return photoUrl; }

    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    //public Int getScore

    @Override
    public String toString() { return name; }
}