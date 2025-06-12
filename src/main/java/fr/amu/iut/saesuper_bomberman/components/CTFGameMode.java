
package fr.amu.iut.saesuper_bomberman.components;

import fr.amu.iut.saesuper_bomberman.model.GameState;
import fr.amu.iut.saesuper_bomberman.model.Player;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CTFGameMode extends GameMode {
    private Rectangle team1Flag;
    private Rectangle team2Flag;
    private int team1Score = 0;
    private int team2Score = 0;
    private final int SCORE_TO_WIN = 3;
    private boolean team1FlagCaptured = false;
    private boolean team2FlagCaptured = false;

    public CTFGameMode(GameState gameState) {
        super(gameState);
        setupTeams();
        initializeFlags();
    }

    private void setupTeams() {
        // Garder seulement les 2 premiers joueurs actifs
        for (int i = 0; i < Math.min(2, gameState.getPlayers().size()); i++) {
            Player player = gameState.getPlayers().get(i);
            player.setTeam(i + 1);
            player.setAlive(true);
        }
        // Désactiver les autres joueurs
        for (int i = 2; i < gameState.getPlayers().size(); i++) {
            gameState.getPlayers().get(i).setAlive(false);
        }
    }

    private void initializeFlags() {
        team1Flag = new Rectangle(32, 32, 16, 16);
        team2Flag = new Rectangle(480, 352, 16, 16);
        team1Flag.setFill(Color.BLUE);
        team2Flag.setFill(Color.RED);
    }

    @Override
    public void update() {
        checkFlagCapture();
        checkScoring();
    }

    @Override
    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            // Contrôles Joueur 1
            case Z -> movePlayer(0, -1, 1);
            case S -> movePlayer(0, 1, 1);
            case Q -> movePlayer(-1, 0, 1);
            case D -> movePlayer(1, 0, 1);
            case SPACE -> placeBomb(1);

            // Contrôles Joueur 2
            case UP -> movePlayer(0, -1, 2);
            case DOWN -> movePlayer(0, 1, 2);
            case LEFT -> movePlayer(-1, 0, 2);
            case RIGHT -> movePlayer(1, 0, 2);
            case ENTER -> placeBomb(2);
        }
    }

    @Override
    public void handleKeyRelease(KeyEvent event) {
        // Pas utilisé ici
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
            if (player.isAlive()) {
                player.placeBomb(gameState);
            }
        }
    }

    private void checkFlagCapture() {
        for (Player player : gameState.getPlayers()) {
            if (!player.isAlive()) continue;

            if (player.getTeam() == 2 && !team1FlagCaptured &&
                    player.getX() == 1 && player.getY() == 1) {
                team1FlagCaptured = true;
                System.out.println("Équipe 2 a capturé le drapeau bleu!");
            }
            if (player.getTeam() == 1 && !team2FlagCaptured &&
                    player.getX() == 15 && player.getY() == 11) {
                team2FlagCaptured = true;
                System.out.println("Équipe 1 a capturé le drapeau rouge!");
            }
        }
    }

    private void checkScoring() {
        for (Player player : gameState.getPlayers()) {
            if (!player.isAlive()) continue;

            if (player.getTeam() == 2 && team1FlagCaptured &&
                    player.getX() == 15 && player.getY() == 11) {
                team2Score++;
                resetFlags();
                System.out.println("Point pour l'équipe 2! Score: " + team2Score);
            }
            if (player.getTeam() == 1 && team2FlagCaptured &&
                    player.getX() == 1 && player.getY() == 1) {
                team1Score++;
                resetFlags();
                System.out.println("Point pour l'équipe 1! Score: " + team1Score);
            }
        }
    }

    private void resetFlags() {
        team1FlagCaptured = false;
        team2FlagCaptured = false;
    }

    @Override
    public boolean isGameOver() {
        return team1Score >= SCORE_TO_WIN || team2Score >= SCORE_TO_WIN;
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public int getTeam2Score() {
        return team2Score;
    }
}