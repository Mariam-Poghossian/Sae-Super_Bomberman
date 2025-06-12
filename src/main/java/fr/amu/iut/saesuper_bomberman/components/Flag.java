package fr.amu.iut.saesuper_bomberman.components;

public class Flag {
    private int team;
    private int x;
    private int y;
    private boolean isCaptured;

    public Flag(int team, int x, int y) {
        this.team = team;
        this.x = x;
        this.y = y;
        this.isCaptured = false;
    }

    public int getTeam() {
        return team;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured(boolean captured) {
        isCaptured = captured;
    }
}