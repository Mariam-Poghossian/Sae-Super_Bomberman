package fr.amu.iut.saesuper_bomberman.components;

import javafx.scene.layout.Pane;
import fr.amu.iut.saesuper_bomberman.model.GameState;

public class GamePanel extends Pane {
    private GameState gameState;

    public GamePanel(GameState gameState) {
        this.gameState = gameState;
        setPrefSize(544, 416); // 17*32 x 13*32
    }

    public GameState getGameState() {
        return gameState;
    }
}