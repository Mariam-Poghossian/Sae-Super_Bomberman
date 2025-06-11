package fr.amu.iut.saesuper_bomberman.model;

/**
 * Énumération des types de tuiles utilisées dans le jeu.
 * Définit les différents types de cases qui peuvent exister sur la grille de jeu.
 */
public enum TileType {
    /**
     * Représente une case d'herbe sur laquelle les joueurs peuvent se déplacer
     */
    GRASS,
    /**
     * Représente un mur destructible qui peut être détruit par une explosion
     */
    WALL_DESTRUCTIBLE,
    /**
     * Représente un mur indestructible qui ne peut pas être détruit
     */
    WALL_INDESTRUCTIBLE
}