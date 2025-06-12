package fr.amu.iut.saesuper_bomberman.model;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un joueur dans le jeu Bomberman.
 * Gère les caractéristiques et comportements du joueur :
 * - Position et déplacement sur la grille
 * - Placement et gestion des bombes
 * - État de vie et immunité temporaire
 * - Statistiques (éliminations, power-ups collectés)
 * - Interaction avec les autres éléments du jeu
 */
public class Player {
    private int id;
    private int x, y;
    private Color color;
    private boolean alive; // Remplace le système de vies par un simple booléen
    private int bombRange;
    private int maxBombs;
    private List<Bomb> activeBombs;
    private long lastBombTime;
    private long lastDamageTime;
    private static final long DAMAGE_IMMUNITY_DURATION = 1000;
    private static final int MAXIMUM_EXPLOSION_RANGE = 10;
    private int killCount;
    private long deathTime = 0; // Temps de la mort du joueur

    /**
     * Crée un nouveau joueur aux coordonnées spécifiées.
     *
     * @param id L'identifiant du joueur (1-4)
     * @param x La coordonnée X initiale du joueur
     * @param y La coordonnée Y initiale du joueur
     * @param color La couleur associée au joueur
     */
    public Player(int id, int x, int y, Color color) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = color;
        this.alive = true; // Le joueur commence vivant
        this.bombRange = 2;
        this.maxBombs = 1;
        this.activeBombs = new ArrayList<>();
        this.lastBombTime = 0;
        this.lastDamageTime = 0;
        this.killCount = 0;
    }

    /**
     * Déplace le joueur dans la direction spécifiée si possible.
     *
     * @param dx Le déplacement horizontal (-1, 0, 1)
     * @param dy Le déplacement vertical (-1, 0, 1)
     * @param gameState L'état actuel du jeu
     * @return true si le joueur a pu se déplacer, false sinon
     */
    public boolean move(int dx, int dy, GameState gameState) {
        if (!alive) return false; // Un joueur mort ne peut pas bouger

        int newX = x + dx;
        int newY = y + dy;

        if (gameState.canMoveTo(newX, newY)) {
            x = newX;
            y = newY;
            return true;
        }
        return false;
    }

    /**
     * Place une bombe à la position actuelle du joueur si possible.
     * Vérifie les conditions : joueur vivant, nombre maximum de bombes non atteint,
     * aucune bombe déjà présente à cette position.
     *
     * @param gameState L'état actuel du jeu
     */
    public void placeBomb(GameState gameState) {
        if (!alive) return; // Un joueur mort ne peut pas placer de bombes

        long currentTime = System.currentTimeMillis();
        if (activeBombs.size() < maxBombs && currentTime - lastBombTime > 300) {
            if (!gameState.hasBombAt(x, y)) {
                Bomb bomb = new Bomb(x, y, bombRange, this);
                gameState.addBomb(bomb);
                activeBombs.add(bomb);
                lastBombTime = currentTime;
            }
        }
    }

    /**
     * Définit la portée des bombes du joueur au maximum possible.
     * Effet du power-up "Maximum Explosion".
     */
    public void setMaximumExplosion() {
        bombRange = MAXIMUM_EXPLOSION_RANGE;
    }

    /**
     * Notifie le joueur qu'une de ses bombes a explosé.
     * Libère un emplacement dans le compteur de bombes actives.
     *
     * @param bomb La bombe qui a explosé
     */
    public void bombExploded(Bomb bomb) {
        activeBombs.remove(bomb);
    }

    /**
     * Vérifie si le joueur peut subir des dégâts.
     * Un joueur est immunisé pendant une courte période après avoir subi des dégâts.
     *
     * @return true si le joueur n'est pas immunisé, false sinon
     */
    public boolean canTakeDamage() {
        long currentTime = System.currentTimeMillis();
        return alive && currentTime - lastDamageTime > DAMAGE_IMMUNITY_DURATION;
    }

    /**
     * Élimine le joueur suite à la contraction de l'arène en Death Match.
     * Marque le joueur comme mort et enregistre le moment de sa mort.
     */
    public void killByDeathMatch() {
        if (alive) {
            alive = false;
            deathTime = System.currentTimeMillis();
            System.out.println("Joueur " + id + " est écrasé par un mur!");
        }
    }

    /**
     * Inflige des dégâts au joueur, l'éliminant s'il n'est pas immunisé.
     *
     * @return true si le joueur a subi des dégâts, false s'il était immunisé
     */
    public boolean takeDamage() {
        if (canTakeDamage()) {
            alive = false;
            deathTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     * Vérifie si le joueur est encore en vie.
     *
     * @return true si le joueur est vivant, false sinon
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Vérifie si le joueur est actuellement immunisé aux dégâts.
     *
     * @return true si le joueur est immunisé, false sinon
     */
    public boolean isImmune() {
        return !canTakeDamage();
    }

    /**
     * Incrémente le compteur d'éliminations du joueur.
     * Appelé lorsque le joueur élimine un autre joueur.
     */
    public void addKill() {
        killCount++;
    }

    /**
     * Augmente la portée des bombes du joueur.
     * Effet du power-up "Explosion Expander".
     */
    public void increaseBombRange() {
        // Limiter la portée au maximum défini
        if (bombRange < MAXIMUM_EXPLOSION_RANGE) {
            bombRange++;
        }
    }

    /**
     * Augmente le nombre maximum de bombes que le joueur peut poser simultanément.
     * Effet du power-up "Extra Bomb".
     */
    public void increaseMaxBombs() {
        if (maxBombs < 5) {
            maxBombs++;
        }
    }

    /**
     * Renvoie l'identifiant du joueur.
     *
     * @return L'identifiant du joueur (1-4)
     */
    public int getId() { return id; }

    /**
     * Renvoie la coordonnée X du joueur sur la grille.
     *
     * @return La position X sur la grille
     */
    public int getX() { return x; }

    /**
     * Renvoie la coordonnée Y du joueur sur la grille.
     *
     * @return La position Y sur la grille
     */
    public int getY() { return y; }

    /**
     * Renvoie la couleur associée au joueur.
     *
     * @return La couleur du joueur
     */
    public Color getColor() { return color; }

    /**
     * Renvoie la portée des bombes du joueur.
     *
     * @return La portée des explosions en nombre de cases
     */
    public int getBombRange() { return bombRange; }

    /**
     * Renvoie le nombre maximum de bombes que le joueur peut poser simultanément.
     *
     * @return Le nombre maximum de bombes
     */
    public int getMaxBombs() { return maxBombs; }

    /**
     * Renvoie le nombre d'éliminations réalisées par le joueur.
     *
     * @return Le compteur d'éliminations
     */
    public int getKillCount() { return killCount; }

    /**
     * Renvoie l'horodatage de la mort du joueur.
     * Utilisé pour déterminer l'ordre des éliminations.
     *
     * @return Le temps de la mort en millisecondes, ou 0 si le joueur est vivant
     */
    public long getDeathTime() {
        return deathTime;
    }
}