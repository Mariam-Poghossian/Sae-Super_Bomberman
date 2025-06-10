package fr.amu.iut.saesuper_bomberman.model;

public class PowerUp {
    private int x, y;
    private PowerUpType type;
    private boolean collected;

    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.collected = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public PowerUpType getType() { return type; }
    public boolean isCollected() { return collected; }
    public void collect() { this.collected = true; }
}
