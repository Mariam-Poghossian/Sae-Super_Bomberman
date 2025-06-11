package fr.amu.iut.saesuper_bomberman.model;


public class Bomb {
    private int x, y;
    private int range;
    private Player owner;
    private long creationTime;
    private static final long EXPLOSION_DELAY = 2000; // 2 secondes
    private boolean hasExploded = false;

    public Bomb(int x, int y, int range, Player owner) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.owner = owner;
        this.creationTime = System.currentTimeMillis();
    }

    public void update() {
        // La bombe explose automatiquement après le délai
    }

    public boolean shouldExplode() {
        return !hasExploded && System.currentTimeMillis() - creationTime >= EXPLOSION_DELAY;
    }

    public void explode() {
        if (!hasExploded) {
            hasExploded = true;
            owner.bombExploded(this);
        }
    }

    public boolean hasExploded() {
        return hasExploded;
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getRange() { return range; }
    public Player getOwner() { return owner; }
    public long getTimeLeft() {
        return Math.max(0, EXPLOSION_DELAY - (System.currentTimeMillis() - creationTime));
    }
}