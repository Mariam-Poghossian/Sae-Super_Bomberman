package fr.amu.iut.saesuper_bomberman.model;

/**
 * Énumération des types de power-ups disponibles dans le jeu.
 * Définit les différents bonus que les joueurs peuvent collecter.
 */
public enum PowerUpType {
    /** Augmente le nombre maximum de bombes que le joueur peut poser simultanément */
    EXTRA_BOMB,
    /** Augmente la portée des explosions des bombes du joueur */
    EXPLOSION_EXPANDER,
    /** Donne la portée d'explosion maximale aux bombes du joueur */
    MAXIMUM_EXPLOSION
}
