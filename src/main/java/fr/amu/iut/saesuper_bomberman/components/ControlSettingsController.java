package fr.amu.iut.saesuper_bomberman.components;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControlSettingsController {

    @FXML
    private TextField leftKey, rightKey, upKey, downKey, bombKey;

    private int currentPlayer = 1;

    @FXML
    public void selectPlayer1() {
        currentPlayer = 1;
        updateKeysDisplay();
    }

    @FXML
    public void selectPlayer2() {
        currentPlayer = 2;
        updateKeysDisplay();
    }

    @FXML
    public void selectPlayer3() {
        currentPlayer = 3;
        updateKeysDisplay();
    }

    @FXML
    public void selectPlayer4() {
        currentPlayer = 4;
        updateKeysDisplay();
    }

    private void updateKeysDisplay() {
        switch(currentPlayer) {
            case 1:
                leftKey.setText("GAUCHE");
                rightKey.setText("DROITE");
                upKey.setText("HAUT");
                downKey.setText("BAS");
                bombKey.setText("ESPACE");
                break;
            case 2:
                leftKey.setText("Q");
                rightKey.setText("D");
                upKey.setText("Z");
                downKey.setText("S");
                bombKey.setText("E");
                break;
            case 3:
                leftKey.setText("F");
                rightKey.setText("H");
                upKey.setText("T");
                downKey.setText("G");
                bombKey.setText("Y");
                break;
            case 4:
                leftKey.setText("J");
                rightKey.setText("L");
                upKey.setText("I");
                downKey.setText("K");
                bombKey.setText("O");
                break;
        }
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) leftKey.getScene().getWindow();
        stage.close();
    }
}