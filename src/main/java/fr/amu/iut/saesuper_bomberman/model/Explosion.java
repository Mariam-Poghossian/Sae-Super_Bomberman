package fr.amu.iut.saesuper_bomberman.model;

/**
 * Représente une explosion sur la grille de jeu.
 * Les explosions sont créées lorsque les bombes explosent et peuvent
 * détruire des murs, d'autres bombes et éliminer des joueurs.
 */
public class Explosion {
    private int x, y;
    private int duration;
    private int timeLeft;
    private long creationTime;
    private Player bombOwner; // Nouveau : référence au propriétaire de la bombe

    /**
     * Crée une nouvelle explosion à la position spécifiée.
     *
     * @param x La coordonnée X de l'explosion
     * @param y La coordonnée Y de l'explosion
     * @param duration La durée de l'explosion en frames
     */
    public Explosion(int x, int y, int duration) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.timeLeft = duration;
        this.creationTime = System.nanoTime();
        this.bombOwner = null; // Initialisé à null par défaut
    }

    /**
     * Met à jour l'état de l'explosion (diminue son temps restant).
     */
    public void update() {
        timeLeft--;
    }

    /**
     * Vérifie si l'explosion est terminée.
     * @return true si l'explosion est terminée, false sinon
     */
    public boolean isFinished() {
        return timeLeft <= 0;
    }

    /**
     * Définit le propriétaire de la bombe qui a créé cette explosion.
     * Utilisé pour attribuer les éliminations au bon joueur.
     *
     * @param owner Le joueur qui a posé la bombe
     */
    public void setBombOwner(Player owner) {
        this.bombOwner = owner;
    }

    /**
     * Renvoie la coordonnée X de l'explosion.
     * @return La position X sur la grille
     */
    public int getX() { return x; }

    /**
     * Renvoie la coordonnée Y de l'explosion.
     * @return La position Y sur la grille
     */
    public int getY() { return y; }

    /**
     * Renvoie le temps restant de l'explosion.
     * @return Le temps restant en frames
     */
    public int getTimeLeft() { return timeLeft; }

    /**
     * Renvoie la durée totale de l'explosion.
     * @return La durée totale en frames
     */
    public int getDuration() { return duration; }

    /**
     * Renvoie le propriétaire de la bombe qui a créé cette explosion.
     * @return Le joueur propriétaire ou null si non défini
     */
    public Player getBombOwner() { return bombOwner; } // Nouveau getter
}