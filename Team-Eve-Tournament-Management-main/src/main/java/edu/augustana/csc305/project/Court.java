package edu.augustana.csc305.project;

public class Court implements java.io.Serializable{
    private static int idCounter = 0;
    private String ID; //do not let user change this
    private final int number;
    private Referee referee; //court assignment

    public Court(int number) {
        idCounter++;
        this.ID = "c" + idCounter;
        this.number = number;
        this.referee = null;
    }
    public Court(){ //for save file, do not use for creation - number may be off, number is set to idcounter for ease of file loading
        this(idCounter);
    }

    //ID
    public String getID() { return ID; }
    public void setID(String ID) { this.ID = ID; }
    public static int getNextIdIndex() { return idCounter; } //to save counter
    public static void setNextIdIndex(int count) { idCounter = count; } //to restore counter
    //number
    public int getNumber() { return number; }
    //refferee
    public Referee getReferee() { return referee; }
    public void setReferee(Referee referee) { if (referee != null) this.referee = referee; }

    @Override
    public String toString() {
        return "Court " + number;
    }
}
