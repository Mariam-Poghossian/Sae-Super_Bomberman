package fr.amu.iut.saesuper_bomberman.controllerfxml;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import fr.amu.iut.saesuper_bomberman.model.*;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class GameController implements Initializable {

    @FXML
    private Canvas gameCanvas;
    @FXML
    private ImageView player1Image;
    @FXML
    private ImageView player2Image;
    @FXML
    private ImageView player3Image;
    @FXML
    private ImageView player4Image;
    @FXML
    private Label player1Name;
    @FXML
    private Label player2Name;
    @FXML
    private Label player3Name;
    @FXML
    private Label player4Name;
    @FXML
    private Label player1Kills;
    @FXML
    private Label player2Kills;
    @FXML
    private Label player3Kills;
    @FXML
    private Label player4Kills;
    @FXML
    private Label timerLabel;
    @FXML
    private Label gameStatus;

    private static final int TILE_SIZE = 38;
    private static final long MOVEMENT_DELAY = 150_000_000;

    private boolean themeChangePressed = false;
    private GraphicsContext gc;
    private GameState gameState;
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private AnimationTimer gameLoop;
    private Scene scene;
    private int currentThemeIndex = 0; // 0 = default, 1 = special1, 2 = special2

    private long[] lastMovementTime = new long[4];
    private boolean gameEndedByTime = false;

    // Images des joueurs pour l'interface
    private Image p1Image, p2Image, p3Image, p4Image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = gameCanvas.getGraphicsContext2D();
        gameState = new GameState();

        // Ajuster la taille du canvas pour la nouvelle fenêtre
        gameCanvas.setWidth(17 * TILE_SIZE);  // 17 * 38 = 646 pixels
        gameCanvas.setHeight(13 * TILE_SIZE); // 13 * 38 = 494 pixels

        for (int i = 0; i < 4; i++) {
            lastMovementTime[i] = 0;
        }

        // Charger les images des joueurs pour l'interface
        loadPlayerInterfaceImages();

        gameCanvas.setFocusTraversable(true);
    }

    private void loadPlayerInterfaceImages() {
        try {
            String basePath = "/fr/amu/iut/saesuper_bomberman/assets/bomberman/";

            // Charger les images P1, P2, P3, P4
            p1Image = new Image(getClass().getResourceAsStream(basePath + "p1.png"));
            p2Image = new Image(getClass().getResourceAsStream(basePath + "p2.png"));
            p3Image = new Image(getClass().getResourceAsStream(basePath + "p3.png"));
            p4Image = new Image(getClass().getResourceAsStream(basePath + "p4.png"));

            // Assigner les images aux ImageView
            if (p1Image != null && !p1Image.isError()) {
                player1Image.setImage(p1Image);
            }
            if (p2Image != null && !p2Image.isError()) {
                player2Image.setImage(p2Image);
            }
            if (p3Image != null && !p3Image.isError()) {
                player3Image.setImage(p3Image);
            }
            if (p4Image != null && !p4Image.isError()) {
                player4Image.setImage(p4Image);
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images des joueurs pour l'interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void reloadPlayerInterfaceImages(String themePath) {
        try {
            // Charger les images P1, P2, P3, P4 avec le nouveau thème
            p1Image = new Image(getClass().getResourceAsStream(themePath + "p1.png"));
            p2Image = new Image(getClass().getResourceAsStream(themePath + "p2.png"));
            p3Image = new Image(getClass().getResourceAsStream(themePath + "p3.png"));
            p4Image = new Image(getClass().getResourceAsStream(themePath + "p4.png"));

            // Assigner les images aux ImageView
            if (p1Image != null && !p1Image.isError()) {
                player1Image.setImage(p1Image);
            }
            if (p2Image != null && !p2Image.isError()) {
                player2Image.setImage(p2Image);
            }
            if (p3Image != null && !p3Image.isError()) {
                player3Image.setImage(p3Image);
            }
            if (p4Image != null && !p4Image.isError()) {
                player4Image.setImage(p4Image);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du rechargement des images des joueurs pour l'interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        setupKeyHandlers();
    }

    private void setupKeyHandlers() {
        scene.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            gameCanvas.requestFocus();
        });

        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
    }

    public void startGame() {
        gameCanvas.requestFocus();
        gameEndedByTime = false;
        startGameLoop();
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_000_000) {
                    update(now);
                    render();
                    updateUI();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void update(long currentTime) {
        if (gameState.isTimeUp() && !gameEndedByTime) {
            // Au lieu de terminer la partie, activer le Death Match
            gameEndedByTime = true;
            gameState.startDeathMatch();
            gameStatus.setText("DEATH MATCH ACTIVÉ! SURVIVEZ!");
            gameStatus.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
            timerLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 18px; -fx-font-family: 'Courier New';");
        }

        if (pressedKeys.contains(KeyCode.R)) {
            if (!themeChangePressed) {
                // R ramène toujours au thème par défaut
                gameState.changeTheme(GameState.DEFAULT_THEME);
                reloadPlayerInterfaceImages(GameState.DEFAULT_THEME);
                currentThemeIndex = 0;
                themeChangePressed = true;
                gameStatus.setText("THÈME STANDARD ACTIVÉ");
            }
        } else if (pressedKeys.contains(KeyCode.T)) {
            if (!themeChangePressed) {
                // T passe au thème suivant dans le cycle
                currentThemeIndex = (currentThemeIndex + 1) % 3;
                switch (currentThemeIndex) {
                    case 0:
                        gameState.changeTheme(GameState.DEFAULT_THEME);
                        reloadPlayerInterfaceImages(GameState.DEFAULT_THEME);
                        gameStatus.setText("THÈME STANDARD ACTIVÉ");
                        break;
                    case 1:
                        gameState.changeTheme(GameState.SPECIAL_THEME);
                        reloadPlayerInterfaceImages(GameState.SPECIAL_THEME);
                        gameStatus.setText("THÈME SPÉCIAL 1 ACTIVÉ");
                        break;
                    case 2:
                        gameState.changeTheme(GameState.SPECIAL_THEME2);
                        reloadPlayerInterfaceImages(GameState.SPECIAL_THEME2);
                        gameStatus.setText("THÈME SPÉCIAL 2 ACTIVÉ");
                        break;
                }
                themeChangePressed = true;
            }
        } else {
            // Réinitialiser themeChangePressed lorsque ni R ni T ne sont appuyés
            themeChangePressed = false;
        }

        // Mettre à jour le Death Match si actif
        if (gameState.isDeathMatchActive()) {
            gameState.updateDeathMatch();
        }

        // La partie continue même après la fin du temps normal
        for (int i = 0; i < gameState.getPlayers().size(); i++) {
            Player player = gameState.getPlayers().get(i);
            if (player.isAlive()) {
                updatePlayerMovement(player, currentTime, i);
            }
        }

        gameState.updateBombs();
        gameState.checkExplosionCollisions();
        gameState.checkPowerUpCollisions();
        checkGameState();
    }

    private void updatePlayerMovement(Player player, long currentTime, int playerIndex) {
        boolean canMove = currentTime - lastMovementTime[playerIndex] >= MOVEMENT_DELAY;

        int dx = 0, dy = 0;
        boolean wantsToBomb = false;

        switch (player.getId()) {
            case 1:
                // Joueur 1 : Flèches + Espace (inchangé)
                if (pressedKeys.contains(KeyCode.UP) && !pressedKeys.contains(KeyCode.DOWN)
                        && !pressedKeys.contains(KeyCode.LEFT) && !pressedKeys.contains(KeyCode.RIGHT)) {
                    dy = -1;
                } else if (pressedKeys.contains(KeyCode.DOWN) && !pressedKeys.contains(KeyCode.UP)
                        && !pressedKeys.contains(KeyCode.LEFT) && !pressedKeys.contains(KeyCode.RIGHT)) {
                    dy = 1;
                } else if (pressedKeys.contains(KeyCode.LEFT) && !pressedKeys.contains(KeyCode.RIGHT)
                        && !pressedKeys.contains(KeyCode.UP) && !pressedKeys.contains(KeyCode.DOWN)) {
                    dx = -1;
                } else if (pressedKeys.contains(KeyCode.RIGHT) && !pressedKeys.contains(KeyCode.LEFT)
                        && !pressedKeys.contains(KeyCode.UP) && !pressedKeys.contains(KeyCode.DOWN)) {
                    dx = 1;
                }
                if (pressedKeys.contains(KeyCode.SPACE)) wantsToBomb = true;
                break;

            case 2:
                // Joueur 2 : ZQSD + E (modifié)
                if (pressedKeys.contains(KeyCode.Z) && !pressedKeys.contains(KeyCode.S)
                        && !pressedKeys.contains(KeyCode.Q) && !pressedKeys.contains(KeyCode.D)) {
                    dy = -1; // Z = Haut
                } else if (pressedKeys.contains(KeyCode.S) && !pressedKeys.contains(KeyCode.Z)
                        && !pressedKeys.contains(KeyCode.Q) && !pressedKeys.contains(KeyCode.D)) {
                    dy = 1; // S = Bas
                } else if (pressedKeys.contains(KeyCode.Q) && !pressedKeys.contains(KeyCode.D)
                        && !pressedKeys.contains(KeyCode.Z) && !pressedKeys.contains(KeyCode.S)) {
                    dx = -1; // Q = Gauche
                } else if (pressedKeys.contains(KeyCode.D) && !pressedKeys.contains(KeyCode.Q)
                        && !pressedKeys.contains(KeyCode.Z) && !pressedKeys.contains(KeyCode.S)) {
                    dx = 1; // D = Droite
                }
                if (pressedKeys.contains(KeyCode.E)) wantsToBomb = true; // E = Bombe
                break;

            case 3:
                // Joueur 3 : IJKL + O (modifié)
                if (pressedKeys.contains(KeyCode.I) && !pressedKeys.contains(KeyCode.K)
                        && !pressedKeys.contains(KeyCode.J) && !pressedKeys.contains(KeyCode.L)) {
                    dy = -1; // I = Haut
                } else if (pressedKeys.contains(KeyCode.K) && !pressedKeys.contains(KeyCode.I)
                        && !pressedKeys.contains(KeyCode.J) && !pressedKeys.contains(KeyCode.L)) {
                    dy = 1; // K = Bas
                } else if (pressedKeys.contains(KeyCode.J) && !pressedKeys.contains(KeyCode.L)
                        && !pressedKeys.contains(KeyCode.I) && !pressedKeys.contains(KeyCode.K)) {
                    dx = -1; // J = Gauche
                } else if (pressedKeys.contains(KeyCode.L) && !pressedKeys.contains(KeyCode.J)
                        && !pressedKeys.contains(KeyCode.I) && !pressedKeys.contains(KeyCode.K)) {
                    dx = 1; // L = Droite
                }
                if (pressedKeys.contains(KeyCode.O)) wantsToBomb = true; // O = Bombe
                break;

            case 4:
                // Joueur 4 : Pavé numérique 8456 + 9 (modifié)
                if (pressedKeys.contains(KeyCode.NUMPAD8) && !pressedKeys.contains(KeyCode.NUMPAD5)
                        && !pressedKeys.contains(KeyCode.NUMPAD4) && !pressedKeys.contains(KeyCode.NUMPAD6)) {
                    dy = -1; // 8 = Haut
                } else if (pressedKeys.contains(KeyCode.NUMPAD5) && !pressedKeys.contains(KeyCode.NUMPAD8)
                        && !pressedKeys.contains(KeyCode.NUMPAD4) && !pressedKeys.contains(KeyCode.NUMPAD6)) {
                    dy = 1; // 5 = Bas
                } else if (pressedKeys.contains(KeyCode.NUMPAD4) && !pressedKeys.contains(KeyCode.NUMPAD6)
                        && !pressedKeys.contains(KeyCode.NUMPAD8) && !pressedKeys.contains(KeyCode.NUMPAD5)) {
                    dx = -1; // 4 = Gauche
                } else if (pressedKeys.contains(KeyCode.NUMPAD6) && !pressedKeys.contains(KeyCode.NUMPAD4)
                        && !pressedKeys.contains(KeyCode.NUMPAD8) && !pressedKeys.contains(KeyCode.NUMPAD5)) {
                    dx = 1; // 6 = Droite
                }
                if (pressedKeys.contains(KeyCode.NUMPAD9)) wantsToBomb = true; // 9 = Bombe
                break;
        }

        if ((dx != 0 || dy != 0) && canMove) {
            boolean moved = player.move(dx, dy, gameState);
            if (moved) {
                lastMovementTime[playerIndex] = currentTime;
            }
        }

        if (wantsToBomb) {
            player.placeBomb(gameState);
        }
    }

    private void render() {
        gameState.render(gc, TILE_SIZE);
    }

    private void updateUI() {
        // Mettre à jour le timer
        timerLabel.setText(gameState.getFormattedTimeRemaining());

        // Mettre à jour les kills des joueurs avec format style rétro
        for (int i = 0; i < gameState.getPlayers().size(); i++) {
            Player player = gameState.getPlayers().get(i);
            Label killLabel = getPlayerKillLabel(i);
            ImageView imageView = getPlayerImageView(i);

            if (killLabel != null) {
                // Format avec zéro devant si nécessaire (style rétro)
                String killText = String.format("%02d", player.getKillCount());
                killLabel.setText(killText);

                if (player.isAlive()) {
                    // Joueur vivant - couleurs normales (blanc sur fond orange)
                    killLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 28px; -fx-font-family: 'Courier New'; -fx-min-width: 40; -fx-min-height: 40; -fx-alignment: center;");
                    if (imageView != null) {
                        imageView.setOpacity(1.0);
                    }
                } else {
                    // Joueur mort - couleurs grisées
                    killLabel.setStyle("-fx-text-fill: #888888; -fx-font-weight: bold; -fx-font-size: 28px; -fx-font-family: 'Courier New'; -fx-min-width: 40; -fx-min-height: 40; -fx-alignment: center;");
                    if (imageView != null) {
                        imageView.setOpacity(0.3);
                    }
                }
            }
        }
    }


    private Label getPlayerKillLabel(int playerIndex) {
        switch (playerIndex) {
            case 0:
                return player1Kills;
            case 1:
                return player2Kills;
            case 2:
                return player3Kills;
            case 3:
                return player4Kills;
            default:
                return null;
        }
    }

    private ImageView getPlayerImageView(int playerIndex) {
        switch (playerIndex) {
            case 0:
                return player1Image;
            case 1:
                return player2Image;
            case 2:
                return player3Image;
            case 3:
                return player4Image;
            default:
                return null;
        }
    }

    private void checkGameState() {
        if (gameEndedByTime) {
            return;
        }

        long alivePlayers = gameState.getPlayers().stream().mapToLong(p -> p.isAlive() ? 1 : 0).sum();

        if (alivePlayers <= 1) {
            Player winner = gameState.getPlayers().stream()
                    .filter(Player::isAlive)
                    .findFirst()
                    .orElse(null);

            if (winner != null) {
                // Un seul survivant
                gameStatus.setText("PLAYER " + winner.getId() + " WINS! SCORE: " + String.format("%02d", winner.getKillCount()));
                gameStatus.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
            } else {
                // Tous les joueurs sont morts, vérifier s'il y a eu une mort simultanée
                List<Player> lastDeadPlayers = findLastDeadPlayers();

                if (lastDeadPlayers.size() > 1) {
                    // Plusieurs joueurs sont morts en même temps - égalité
                    String tiedPlayers = lastDeadPlayers.stream()
                            .map(p -> "P" + p.getId())
                            .collect(Collectors.joining(" VS "));

                    int tieScore = lastDeadPlayers.get(0).getKillCount();
                    gameStatus.setText("ÉGALITÉ! " + tiedPlayers + " - SCORE: " + String.format("%02d", tieScore));
                    gameStatus.setStyle("-fx-text-fill: yellow; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-background-color: #444444; -fx-padding: 3;");
                } else if (lastDeadPlayers.size() == 1) {
                    // Le dernier mort gagne
                    Player lastDead = lastDeadPlayers.get(0);
                    gameStatus.setText("PLAYER " + lastDead.getId() + " WINS! SCORE: " + String.format("%02d", lastDead.getKillCount()));
                    gameStatus.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
                } else {
                    // Si aucun joueur mort récemment (cas improbable), utiliser la méthode existante
                    Player lastWinner = gameState.getPlayers().stream()
                            .max(Comparator.comparingInt(Player::getKillCount))
                            .orElse(null);

                    if (lastWinner != null && lastWinner.getKillCount() > 0) {
                        gameStatus.setText("PLAYER " + lastWinner.getId() + " WINS! SCORE: " + String.format("%02d", lastWinner.getKillCount()));
                        gameStatus.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
                    } else {
                        gameStatus.setText("DRAW GAME - NO SURVIVORS");
                        gameStatus.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
                    }
                }
            }

            if (gameLoop != null) {
                new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(
                                javafx.util.Duration.seconds(3),
                                e -> gameLoop.stop()
                        )
                ).play();
            }
        } else {
            gameStatus.setText("BATTLE IN PROGRESS - " + alivePlayers + " PLAYERS ALIVE");
            gameStatus.setStyle("-fx-text-fill: white; -fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        }
    }

    private List<Player> findLastDeadPlayers() {
        // Ignorer les joueurs qui sont toujours en vie
        List<Player> deadPlayers = gameState.getPlayers().stream()
                .filter(p -> !p.isAlive() && p.getDeathTime() > 0)
                .collect(Collectors.toList());

        if (deadPlayers.isEmpty()) {
            return Collections.emptyList();
        }

        // Trouver le temps de mort le plus récent
        long lastDeathTime = deadPlayers.stream()
                .mapToLong(Player::getDeathTime)
                .max()
                .orElse(0);

        // Trouver tous les joueurs morts à ce moment (ou dans une fenêtre de 100ms)
        return deadPlayers.stream()
                .filter(p -> Math.abs(p.getDeathTime() - lastDeathTime) < 100) // Fenêtre de 100ms pour considérer comme simultané
                .collect(Collectors.toList());
    }

    private Label getPlayerNameLabel(int playerIndex) {
        switch (playerIndex) {
            case 0:
                return player1Name;
            case 1:
                return player2Name;
            case 2:
                return player3Name;
            case 3:
                return player4Name;
            default:
                return null;
        }
    }


    @FXML
    private void restartGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameState = new GameState();
        gameEndedByTime = false;
        for (int i = 0; i < 4; i++) {
            lastMovementTime[i] = 0;
        }

        // Recharger les images avec le thème actuel
        String currentTheme = GameState.DEFAULT_THEME;
        switch (currentThemeIndex) {
            case 0: currentTheme = GameState.DEFAULT_THEME; break;
            case 1: currentTheme = GameState.SPECIAL_THEME; break;
            case 2: currentTheme = GameState.SPECIAL_THEME2; break;
        }
        reloadPlayerInterfaceImages(currentTheme);

        // Remettre les images en opacité normale et réinitialiser les scores
        player1Image.setOpacity(1.0);
        player2Image.setOpacity(1.0);
        player3Image.setOpacity(1.0);
        player4Image.setOpacity(1.0);

        // Réinitialiser les scores avec le bon style
        String initialStyle = "-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 28px; -fx-font-family: 'Courier New'; -fx-min-width: 40; -fx-min-height: 40; -fx-alignment: center;";

        player1Kills.setText("00");
        player1Kills.setStyle(initialStyle);
        player2Kills.setText("00");
        player2Kills.setStyle(initialStyle);
        player3Kills.setText("00");
        player3Kills.setStyle(initialStyle);
        player4Kills.setText("00");
        player4Kills.setStyle(initialStyle);

        gameStatus.setText("READY PLAYER ONE");

        startGame();
    }
}
