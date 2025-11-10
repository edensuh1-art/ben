package edu.augustana.csc305.project;

public class Court {
    private final int number;

    public Court(int number) {
        this.number = number;
    }
    public int getNumber() { return number; }

    @Override
    public String toString() {
        return "Court " + number;
    }
}
