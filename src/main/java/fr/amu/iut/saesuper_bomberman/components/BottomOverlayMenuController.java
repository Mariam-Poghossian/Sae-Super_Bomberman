package fr.amu.iut.saesuper_bomberman.components;

import fr.amu.iut.saesuper_bomberman.controllerfxml.MenuController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class BottomOverlayMenuController {

    @FXML
    private BorderPane bottomMenu;

    @FXML
    private Button muteButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button fullscreenButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button restartButton; // Ajoutez cette ligne en haut

    private double previousVolume = 50; // Volume par défaut
    private boolean isFullscreen = false;
    private boolean isPaused = false;

    @FXML
    public void initialize() {
        bottomMenu.setVisible(false); // caché au départ

        Platform.runLater(() -> {
            Scene scene = bottomMenu.getScene();
            if (scene != null) {
                scene.setOnMouseEntered(e -> bottomMenu.setVisible(true));
                scene.setOnMouseExited(e -> bottomMenu.setVisible(false));
            } else {
                System.err.println("❌ Scene is null: le composant n’est pas bien attaché !");
            }
        });

        // 🔊 Mute / unmute
        muteButton.setOnAction(e -> {
            if (volumeSlider.getValue() > 0) {
                previousVolume = volumeSlider.getValue();
                volumeSlider.setValue(0);
            } else {
                volumeSlider.setValue(previousVolume);
            }
        });

        // 🔁 Changement d’icône selon le volume
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            ImageView iv = (ImageView) muteButton.getGraphic();
            if (newVal.doubleValue() == 0) {
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/Mute.png").toExternalForm()));
            } else {
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/Volume.png").toExternalForm()));
            }
        });

        // 🖥️ Bouton plein écran
        fullscreenButton.setOnAction(e -> {
            Stage stage = (Stage) fullscreenButton.getScene().getWindow();
            if (stage != null) {
                isFullscreen = !isFullscreen;
                stage.setFullScreen(isFullscreen);
            }
        });

        // ⏸️ Bouton pause / reprise
        pauseButton.setOnAction(e -> {
            ImageView iv = (ImageView) pauseButton.getGraphic();
            Stage stage = (Stage) pauseButton.getScene().getWindow();
            MenuController menuController = (MenuController) stage.getUserData();

            if (menuController == null) {
                System.err.println("❌ MenuController non trouvé dans le stage !");
                return;
            }

            if (!isPaused) {
                // Quand on met en pause, on affiche le bouton play
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/play.png").toExternalForm()));
                isPaused = true;
                menuController.pauseAllAnimations();
                System.out.println("⏸️ Jeu en pause");
            } else {
                // Quand on reprend, on affiche le bouton pause
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/pause.png").toExternalForm()));
                isPaused = false;
                menuController.resumeAllAnimations();
                System.out.println("▶️ Jeu relancé");
            }
        });

        restartButton.setOnAction(e -> {
            Stage stage = (Stage) restartButton.getScene().getWindow();
            MenuController menuController = (MenuController) stage.getUserData();

            if (menuController == null) {
                System.err.println("❌ MenuController non trouvé dans le stage !");
                return;
            }

            menuController.restartAllAnimations();
            System.out.println("🔄 Animations redémarrées");

            // Réinitialisation du bouton pause si nécessaire
            if (isPaused) {
                ImageView iv = (ImageView) pauseButton.getGraphic();
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/pause.png").toExternalForm()));
                isPaused = false;
            }
        });
    }
}

