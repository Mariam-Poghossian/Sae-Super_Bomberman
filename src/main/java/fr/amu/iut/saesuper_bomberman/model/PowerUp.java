package fr.amu.iut.saesuper_bomberman.model;

/**
 * Représente un power-up qui peut être collecté par les joueurs.
 * Les power-ups apparaissent lorsque des murs destructibles sont détruits
 * et offrent différents avantages aux joueurs qui les collectent.
 */
public class PowerUp {
    private int x, y;
    private PowerUpType type;
    private boolean collected;

    /**
     * Crée un nouveau power-up à la position spécifiée.
     *
     * @param x La coordonnée X du power-up sur la grille
     * @param y La coordonnée Y du power-up sur la grille
     * @param type Le type de power-up
     */
    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.collected = false;
    }

    /**
     * Renvoie la coordonnée X du power-up.
     * @return La position X sur la grille
     */
    public int getX() { return x; }

    /**
     * Renvoie la coordonnée Y du power-up.
     * @return La position Y sur la grille
     */
    public int getY() { return y; }

    /**
     * Renvoie le type de ce power-up.
     * @return Le type de power-up
     */
    public PowerUpType getType() { return type; }

    /**
     * Indique si le power-up a été collecté.
     * @return true si le power-up a été collecté, false sinon
     */
    public boolean isCollected() { return collected; }

    /**
     * Marque le power-up comme collecté.
     */
    public void collect() { this.collected = true; }
}
