package edu.augustana.csc305.project;
import java.time.LocalDateTime;

public class Match {
    private Team home;
    private Team away;
    private int assignedCourtNumber; // null = unassigned
    private LocalDateTime scheduledStart; // null = unscheduled
    private Team winner; // null = no winner yet

    public Match(Team home, Team away) {
        this.home = home;
        this.away = away;
        this.assignedCourtNumber = -1 ;
        this.scheduledStart = null;
        this.winner = null;
    }
    /*public Match() { // can't use this, matches must have teams assigned to be matches
        this(null, null);
    }*/

    public Team getHome() { return home; }
    public void setHome(Team home) { this.home = home; }
    public Team getAway() { return away; }
    public void setAway(Team away) { this.away = away; }

    public Integer getAssignedCourtNumber() { return assignedCourtNumber; }
    public void setAssignedCourtNumber(Integer n) { this.assignedCourtNumber = n; }

    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(LocalDateTime t) { this.scheduledStart = t; }

    public Team getWinner(){ return null; } // change depending on how winner is being determind

    @Override
    public String toString() {
        String courtInfo = (assignedCourtNumber <= 0) ? "Court " + assignedCourtNumber : "Unassigned Court";
        String timeInfo = (scheduledStart != null) ? scheduledStart.toString() : "Unscheduled";
        String winnerInfo = (winner != null) ? "Winner " + winner.toString() : "Match in progress or not started";
        return home + " vs " + away + " | " + courtInfo + " | " + timeInfo + " | " + winnerInfo;
    }
}
