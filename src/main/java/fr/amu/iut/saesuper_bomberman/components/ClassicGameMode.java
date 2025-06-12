package fr.amu.iut.saesuper_bomberman.components;

import fr.amu.iut.saesuper_bomberman.model.GameState;
import fr.amu.iut.saesuper_bomberman.model.Player;
import fr.amu.iut.saesuper_bomberman.model.Bomb;
import fr.amu.iut.saesuper_bomberman.model.TileType;
import javafx.scene.input.KeyEvent;

public class ClassicGameMode extends GameMode {
    private static final int MAP_WIDTH = 17;
    private static final int MAP_HEIGHT = 13;

    public ClassicGameMode(GameState gameState) {
        super(gameState);
    }

    @Override
    public void update() {
        // Les bombes et explosions sont gérées dans GameState.update()
        // Ici, on peut ajouter des règles spécifiques si besoin
    }

    @Override
    public void handleKeyPress(KeyEvent event) {
        // Contrôles pour 2 joueurs (exemple)
        switch (event.getCode()) {
            // Joueur 1
            case Z -> movePlayer(0, -1, 1);
            case S -> movePlayer(0, 1, 1);
            case Q -> movePlayer(-1, 0, 1);
            case D -> movePlayer(1, 0, 1);
            case SPACE -> placeBomb(1);

            // Joueur 2
            case UP -> movePlayer(0, -1, 2);
            case DOWN -> movePlayer(0, 1, 2);
            case LEFT -> movePlayer(-1, 0, 2);
            case RIGHT -> movePlayer(1, 0, 2);
            case ENTER -> placeBomb(2);

            // Ajoute ici les touches pour les joueurs 3 et 4 si besoin
        }
    }

    @Override
    public void handleKeyRelease(KeyEvent event) {
        // Pas utilisé dans ce mode
    }

    private void movePlayer(int dx, int dy, int playerNumber) {
        if (playerNumber <= gameState.getPlayers().size()) {
            Player player = gameState.getPlayers().get(playerNumber - 1);
            if (player.isAlive()) {
                player.move(dx, dy, gameState);
            }
        }
    }

    private void placeBomb(int playerNumber) {
        if (playerNumber <= gameState.getPlayers().size()) {
            Player player = gameState.getPlayers().get(playerNumber - 1);
            player.placeBomb(gameState);
        }
    }

    @Override
    public boolean isGameOver() {
        int alivePlayers = 0;
        for (Player player : gameState.getPlayers()) {
            if (player.isAlive()) {
                alivePlayers++;
            }
        }
        return alivePlayers <= 1;
    }
}