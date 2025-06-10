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
    private Button restartButton;

    private double previousVolume = 50;
    private boolean isFullscreen = false;
    private boolean isPaused = false;

    @FXML
    public void initialize() {
        initializeBottomMenu();
        initializeVolumeControls();
        initializeFullscreenButton();
        initializePauseButton();
        initializeRestartButton();
    }

    private void initializeBottomMenu() {
        bottomMenu.setVisible(false);
        Platform.runLater(() -> {
            Scene scene = bottomMenu.getScene();
            if (scene != null) {
                scene.setOnMouseEntered(e -> bottomMenu.setVisible(true));
                scene.setOnMouseExited(e -> bottomMenu.setVisible(false));
            }
        });
    }

    private void initializeVolumeControls() {
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            Stage stage = (Stage) volumeSlider.getScene().getWindow();
            MenuController menuController = (MenuController) stage.getUserData();
            if (menuController != null) {
                menuController.setVolume(newVal.doubleValue());
            }

            ImageView iv = (ImageView) muteButton.getGraphic();
            if (newVal.doubleValue() == 0) {
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/Mute.png").toExternalForm()));
            } else {
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/Volume.png").toExternalForm()));
            }
        });

        muteButton.setOnAction(e -> {
            Stage stage = (Stage) muteButton.getScene().getWindow();
            MenuController menuController = (MenuController) stage.getUserData();

            if (volumeSlider.getValue() > 0) {
                previousVolume = volumeSlider.getValue();
                volumeSlider.setValue(0);
                if (menuController != null) {
                    menuController.muteAudio(true);
                }
            } else {
                volumeSlider.setValue(previousVolume);
                if (menuController != null) {
                    menuController.muteAudio(false);
                }
            }
        });
    }

    private void initializeFullscreenButton() {
        fullscreenButton.setOnAction(e -> {
            Stage stage = (Stage) fullscreenButton.getScene().getWindow();
            if (stage != null) {
                isFullscreen = !isFullscreen;
                stage.setFullScreen(isFullscreen);
            }
        });
    }

    private void initializePauseButton() {
        pauseButton.setOnAction(e -> {
            ImageView iv = (ImageView) pauseButton.getGraphic();
            Stage stage = (Stage) pauseButton.getScene().getWindow();
            MenuController menuController = (MenuController) stage.getUserData();

            if (menuController == null) {
                System.err.println("❌ MenuController non trouvé dans le stage !");
                return;
            }

            if (!isPaused) {
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/play.png").toExternalForm()));
                isPaused = true;
                menuController.pauseAllAnimations();
            } else {
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/pause.png").toExternalForm()));
                isPaused = false;
                menuController.resumeAllAnimations();
            }
        });
    }

    private void initializeRestartButton() {
        restartButton.setOnAction(e -> {
            Stage stage = (Stage) restartButton.getScene().getWindow();
            MenuController menuController = (MenuController) stage.getUserData();

            if (menuController == null) {
                System.err.println("❌ MenuController non trouvé dans le stage !");
                return;
            }

            menuController.restartAllAnimations();

            if (isPaused) {
                ImageView iv = (ImageView) pauseButton.getGraphic();
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/pause.png").toExternalForm()));
                isPaused = false;
            }
        });
    }
}