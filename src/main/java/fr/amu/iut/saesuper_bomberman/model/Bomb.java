package fr.amu.iut.saesuper_bomberman.model;

/**
 * Représente une bombe placée par un joueur sur la grille.
 * Les bombes explosent après un délai et créent des explosions.
 */
public class Bomb {
    private int x, y;
    private int range;
    private Player owner;
    private long creationTime;
    private static final long EXPLOSION_DELAY = 2000; // 2 secondes
    private boolean hasExploded = false;

    /**
     * Crée une nouvelle bombe à la position spécifiée.
     *
     * @param x La coordonnée X de la bombe
     * @param y La coordonnée Y de la bombe
     * @param range La portée de l'explosion de la bombe
     * @param owner Le joueur qui a posé la bombe
     */
    public Bomb(int x, int y, int range, Player owner) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.owner = owner;
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * Met à jour l'état de la bombe.
     */
    public void update() {
        // La bombe explose automatiquement après le délai
    }

    /**
     * Vérifie si la bombe doit exploser.
     * @return true si la bombe doit exploser, false sinon
     */
    public boolean shouldExplode() {
        return !hasExploded && System.currentTimeMillis() - creationTime >= EXPLOSION_DELAY;
    }

    /**
     * Fait exploser la bombe et informe son propriétaire.
     */
    public void explode() {
        if (!hasExploded) {
            hasExploded = true;
            owner.bombExploded(this);
        }
    }

    /**
     * Renvoie la coordonnée X de la bombe.
     * @return La position X sur la grille
     */
    public int getX() { return x; }

    /**
     * Renvoie la coordonnée Y de la bombe.
     * @return La position Y sur la grille
     */
    public int getY() { return y; }

    /**
     * Renvoie la portée de l'explosion de la bombe.
     * @return La portée en nombre de cases
     */
    public int getRange() { return range; }

    /**
     * Renvoie le propriétaire de la bombe.
     * @return Le joueur qui a posé la bombe
     */
    public Player getOwner() { return owner; }

    /**
     * Renvoie le temps restant avant l'explosion de la bombe.
     * @return Le temps restant en millisecondes
     */
    public long getTimeLeft() {
        return Math.max(0, EXPLOSION_DELAY - (System.currentTimeMillis() - creationTime));
    }
}