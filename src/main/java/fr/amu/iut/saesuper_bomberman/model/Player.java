package fr.amu.iut.saesuper_bomberman.model;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

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
    private int team; // Temps de la mort du joueur

    // Ajouter ces deux méthodes à la classe Player
    public void setTeam(int team) {
        this.team = team;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    // Ajouter aussi un getter pour l'équipe
    public int getTeam() {
        return team;
    }

    public javafx.geometry.Bounds getBoundsInParent() {
        return new javafx.geometry.BoundingBox(x, y, 32, 32);
    }

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

    public void setMaximumExplosion() {
        bombRange = MAXIMUM_EXPLOSION_RANGE;
    }

    public void bombExploded(Bomb bomb) {
        activeBombs.remove(bomb);
    }

    public boolean canTakeDamage() {
        long currentTime = System.currentTimeMillis();
        return alive && currentTime - lastDamageTime > DAMAGE_IMMUNITY_DURATION;
    }

    public void killByDeathMatch() {
        if (alive) {
            alive = false;
            deathTime = System.currentTimeMillis();
            System.out.println("Joueur " + id + " est écrasé par un mur!");
        }
    }

    public boolean takeDamage() {
        if (canTakeDamage()) {
            alive = false;
            deathTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isImmune() {
        return !canTakeDamage();
    }

    public void addKill() {
        killCount++;
    }

    public void increaseBombRange() {
        // Limiter la portée au maximum défini
        if (bombRange < MAXIMUM_EXPLOSION_RANGE) {
            bombRange++;
        }
    }

    public void increaseMaxBombs() {
        if (maxBombs < 5) {
            maxBombs++;
        }
    }

    public void increaseSpeed() {
        // Pourrait être utilisé pour réduire le délai entre les mouvements
    }

    // Getters modifiés
    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
    public int getLives() { return alive ? 1 : 0; } // Pour compatibilité avec l'affichage
    public int getBombRange() { return bombRange; }
    public int getMaxBombs() { return maxBombs; }
    public int getActiveBombsCount() { return activeBombs.size(); }
    public List<Bomb> getActiveBombs() { return new ArrayList<>(activeBombs); }
    public int getKillCount() { return killCount; }
    public long getDeathTime() {
        return deathTime;
    }
    public boolean canPlaceBomb() {
        return alive && activeBombs.size() < maxBombs;
    }
}