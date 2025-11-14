package edu.augustana.csc305.project;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.format.DateTimeFormatter;

public class Match implements java.io.Serializable {
    private String ID; //do not let user change this
    private static int idCounter = 0;
    @JsonIgnore
    private Team home;
    private String homeID;
    @JsonIgnore
    private Team away;
    private String awayID;
    @JsonIgnore
    private Court assignedCourt; // null = unassigned
    private LocalDateTime scheduledStart; // null = unscheduled
    @JsonIgnore
    private Team winner; // null = no winner yet
    private String category; // to determine how winner is decided
    private int set; // to determine current set for volleyball matches
    private boolean inProgress; //true if match is running, false if not
    private boolean isRobin; //true if match is part of a Round-Robin, false if not

    public Match(Team home, Team away) {
            idCounter++;
            this.ID = "m" + idCounter;
            this.home = home;
            this.homeID = home == null ? null : home.getID();
            this.away = away;
            this.awayID = away == null ? null : away.getID();
            this.assignedCourt = null;
            this.scheduledStart = null;
            this.winner = null;
            this.category = "None selected";
            this.set = 1;
            this.inProgress = false;
            this.isRobin = false;
    }
    public Match(){ //for save file, not reccomended for creation
        this(null, null);
    }
   
    //ID
    public String getID() { return ID; }
    public void setID(String id) { ID = id; }
    public static int getNextIdIndex() { return idCounter; } //to save counter
    public static void setNextIdIndex(int count) { idCounter = count; } //to restore counter
    public String getHomeID() { return homeID; }
    public void setHomeID(String id) { this.homeID = id; }
    public String getAwayID() { return awayID; }
    public void setAwayID(String id) { this.awayID = id; }
    //teams- home and away
    public Team getHome() { return home; }
    public void setHome(Team home) { if (home != null) {this.home = home; homeID = home.getID();} }
    public Team getAway() { return away; }
    public void setAway(Team away) { if (away != null) {this.away = away; awayID = away.getID();} }
    //assignedCourtNumber
    public int getAssignedCourt() { return assignedCourt == null ? -1 : assignedCourt.getNumber(); }
    public void setAssignedCourt(Court court) {if (court != null) this.assignedCourt = court; } //not for round-robin use - robin matches use court assigned to pool
    //start
    public LocalDateTime getScheduledStart() { return scheduledStart; }
    public void setScheduledStart(LocalDateTime dateTime) { if (dateTime != null) this.scheduledStart = dateTime; }
    //category
    public void setCategory(String category) { if (category.equals( "VolleyBall") || category.equals("Soccer")) this.category = category; }
    public String getCategory() { return category; }
    //set - for volleyball matches
    public int getSet() { return set; }
    public void setSet(int set) { if (set > 0) this.set = set; }
    public void nextSet() { this.set += 1; home.setReset(); away.setReset(); }
    //in progress
    public boolean isInProgress() { return inProgress; }
    public void startMatch() { inProgress = true; }
    //is robin
    public boolean isRobin() { return isRobin; }
    public void setAsRobin() { isRobin = true; }
    
    public void checkSetWinner(){ //to run after each point scored in volleyball match
        if (home.getPointsScored() >= 25 && home.getPointsScored() - away.getPointsScored() >= 2){
                home.addSetWin();
                nextSet();
            } else if (away.getPointsScored() >= 25 && away.getPointsScored() - home.getPointsScored() >= 2){
                away.addSetWin();
                nextSet();
            } else { // no set winner yet
                winner = null;
            }
     }

    public void checkWinner(){ // determines winner based on category rules - run after all sets are finished for volletball, after time ends for soccer
        if (category.equals("Volleyball")){
            if (home.getSetWins() >= 3){
                winner = home;
            } else if (away.getSetWins() >= 3){
                winner = away;
            } else {
                winner = null; //match not over yet
            }
        } else if (category.equals("Soccer")){
            if (home.getPointsScored() > away.getPointsScored()){
                winner = home;
                home.addWin();
            } else if (away.getPointsScored() > home.getPointsScored()){
                winner = away;
                away.addWin();
            } else {
                winner = null; //tie - need to add overtime or penalty kicks later
            }
        }
     }
    public Team getWinner() { return winner; }

    public void endMatch(){ //finalizes match - sets winner based on current scores
        checkWinner();
        home.matchReset();
        away.matchReset();
        inProgress = false;
    }
    

    @Override
    public String toString() {
        String homeStr = (home != null) ? home.toString() : (homeID != null ? homeID : "-");
        String awayStr = (away != null) ? away.toString() : (awayID != null ? awayID : "-");
        String courtInfo = (assignedCourt == null || assignedCourt.getNumber() <= 0) ? "Unassigned Court" : "Court " + assignedCourt.getNumber();
        String timeInfo = (scheduledStart != null) ? scheduledStart.toString() : "Unscheduled";
        String winnerInfo = (winner != null) ? "Winner " + winner.toString() : "Match in progress or not started";
        return homeStr + " vs " + awayStr + " | " + courtInfo + " | " + timeInfo + " | " + winnerInfo;
    }
}
