package fr.amu.iut.saesuper_bomberman.components;

import fr.amu.iut.saesuper_bomberman.model.GameState;
import javafx.scene.input.KeyEvent;

public abstract class GameMode {
    protected GameState gameState;

    public GameMode(GameState gameState) {
        this.gameState = gameState;
    }

    public abstract void update();
    public abstract void handleKeyPress(KeyEvent event);
    public abstract void handleKeyRelease(KeyEvent event);
    public abstract boolean isGameOver(); // Ajout de cette méthode abstraite
}