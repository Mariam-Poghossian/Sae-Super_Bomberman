package fr.amu.iut.saesuper_bomberman;

import fr.amu.iut.saesuper_bomberman.components.CTFGameMode;
import fr.amu.iut.saesuper_bomberman.model.GameState;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainCTF extends Application {
    private static final int TILE_SIZE = 32;
    private GameState gameState;
    private Canvas gameCanvas;
    private boolean running;
    @Override
    public void start(Stage primaryStage) {
        // Initialiser le jeu
        gameState = new GameState();
        gameState.setGameMode(new CTFGameMode(gameState));
        running = true;

        // Créer le canvas
        gameCanvas = new Canvas(17 * TILE_SIZE, 13 * TILE_SIZE);
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        // Créer la scène
        StackPane root = new StackPane(gameCanvas);
        Scene scene = new Scene(root);

        // Ajouter les gestionnaires d'événements clavier
        scene.setOnKeyPressed(this::handleKeyPress);
        scene.setOnKeyReleased(this::handleKeyRelease);

        // Configuration de la fenêtre
        primaryStage.setTitle("Super Bomberman - CTF Mode");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> running = false);
        primaryStage.show();

        // Boucle de jeu
        startGameLoop();
    }

    private void handleKeyPress(KeyEvent event) {
        if (gameState != null && gameState.getGameMode() != null) {
            gameState.getGameMode().handleKeyPress(event);
        }
    }

    private void handleKeyRelease(KeyEvent event) {
        if (gameState != null && gameState.getGameMode() != null) {
            gameState.getGameMode().handleKeyRelease(event);
        }
    }

    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!running) {
                    stop();
                    return;
                }

                // Mise à jour du jeu
                gameState.update();

                // Rendu
                gameState.render(gameCanvas.getGraphicsContext2D(), TILE_SIZE);
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}