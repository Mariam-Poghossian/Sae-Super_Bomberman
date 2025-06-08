package fr.amu.iut.saesuper_bomberman.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;

public class BottomOverlayMenuController {

    @FXML
    private HBox bottomMenu;

    @FXML
    public void initialize() {
        bottomMenu.setVisible(false); // caché au début

        // Exécuter quand tout est bien attaché
        Platform.runLater(() -> {
            Scene scene = bottomMenu.getScene();
            if (scene != null) {
                scene.setOnMouseEntered(e -> bottomMenu.setVisible(true));
                scene.setOnMouseExited(e -> bottomMenu.setVisible(false));
            } else {
                System.err.println("❌ Scene is null: le composant n’est pas bien attaché !");
            }
        });
    }
}
