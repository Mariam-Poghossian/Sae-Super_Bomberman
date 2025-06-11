package fr.amu.iut.saesuper_bomberman.components;

import fr.amu.iut.saesuper_bomberman.controllerfxml.MenuController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    public void openControlSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/amu/iut/saesuper_bomberman/components/controlSettings.fxml"));
            Scene scene = new Scene(loader.load());

            // Fond noir semi-transparent uniquement appliqué au root
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.getRoot().setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);"); // ajustable à 0.3 ou 0.5 si tu veux plus clair ou plus foncé

            // Optionnel : adoucir le fond des composants internes
            scene.getRoot().lookupAll(".button, .text-field, .label").forEach(node ->
                    node.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);")
            );

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);

            Stage mainStage = (Stage) bottomMenu.getScene().getWindow();
            stage.initOwner(mainStage);

            Platform.runLater(() -> {
                double centerXPosition = mainStage.getX() + mainStage.getWidth()/2 - scene.getWidth()/2;
                double centerYPosition = mainStage.getY() + mainStage.getHeight()/2 - scene.getHeight()/2;
                stage.setX(centerXPosition);
                stage.setY(centerYPosition);
            });

            stage.show();
        } catch (Exception e) {
            System.err.println("❌ Erreur ouverture paramètres : " + e.getMessage());
        }
    }



    @FXML
    public void initialize() {
        bottomMenu.setVisible(false);

        Platform.runLater(() -> {
            Scene scene = bottomMenu.getScene();
            if (scene != null) {
                scene.setOnMouseEntered(e -> bottomMenu.setVisible(true));
                scene.setOnMouseExited(e -> bottomMenu.setVisible(false));
            } else {
                System.err.println("❌ Scene is null: le composant n'est pas bien attaché !");
            }
        });

        muteButton.setOnAction(e -> {
            if (volumeSlider.getValue() > 0) {
                previousVolume = volumeSlider.getValue();
                volumeSlider.setValue(0);
            } else {
                volumeSlider.setValue(previousVolume);
            }
        });

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            ImageView iv = (ImageView) muteButton.getGraphic();
            if (newVal.doubleValue() == 0) {
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/Mute.png").toExternalForm()));
            } else {
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/Volume.png").toExternalForm()));
            }

            Stage stage = (Stage) volumeSlider.getScene().getWindow();
            MenuController menuController = (MenuController) stage.getUserData();
            if (menuController != null) {
                menuController.setVolume(newVal.doubleValue());
            }
        });

        fullscreenButton.setOnAction(e -> {
            Stage stage = (Stage) fullscreenButton.getScene().getWindow();
            if (stage != null) {
                isFullscreen = !isFullscreen;
                stage.setFullScreen(isFullscreen);
            }
        });

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
                System.out.println("⏸️ Jeu en pause");
            } else {
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

            if (isPaused) {
                ImageView iv = (ImageView) pauseButton.getGraphic();
                iv.setImage(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/icons/pause.png").toExternalForm()));
                isPaused = false;
            }
        });
    }
}