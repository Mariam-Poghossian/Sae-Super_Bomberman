package fr.amu.iut.saesuper_bomberman.model;

public class Explosion {
    private int x, y;
    private int duration;
    private int timeLeft;
    private long creationTime;
    private Player bombOwner; // Nouveau : référence au propriétaire de la bombe

    public Explosion(int x, int y, int duration) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.timeLeft = duration;
        this.creationTime = System.nanoTime();
        this.bombOwner = null; // Initialisé à null par défaut
    }

    public void update() {
        timeLeft--;
    }

    public boolean isFinished() {
        return timeLeft <= 0;
    }

    // Nouveau setter pour le propriétaire de la bombe
    public void setBombOwner(Player owner) {
        this.bombOwner = owner;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getTimeLeft() { return timeLeft; }
    public int getDuration() { return duration; }
    public long getCreationTime() { return creationTime; }
    public Player getBombOwner() { return bombOwner; } // Nouveau getter
}