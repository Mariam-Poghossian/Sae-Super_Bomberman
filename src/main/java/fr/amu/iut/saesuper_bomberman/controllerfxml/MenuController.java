package fr.amu.iut.saesuper_bomberman.controllerfxml;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ImageView background;

    private ImageView montgolfiere;
    private ImageView dirigeable;
    private ImageView petitDirigeable;
    private Node bottomOverlay;
    private MediaPlayer mediaPlayer;

    private TranslateTransition mt;
    private TranslateTransition dh;
    private TranslateTransition df;
    private PauseTransition logoPause;
    private ParallelTransition logoAnimation;
    private ImageView logo;

    private boolean logoAnimationDone = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAudio();
        initializeBackground();
        initializeFlyingObjects();
        initializeAnimations();
        initializeLogo();
        initializeBottomMenu();
    }

    private void initializeAudio() {
        try {
            String musicFile = "/fr/amu/iut/saesuper_bomberman/assets/audio/music.mp3";
            URL musicUrl = getClass().getResource(musicFile);
            if (musicUrl == null) {
                System.err.println("❌ Fichier audio non trouvé : " + musicFile);
                return;
            }
            System.out.println("✅ Fichier audio trouvé : " + musicUrl);

            Media media = new Media(musicUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.5);
            System.out.println("🔊 Volume initial : " + mediaPlayer.getVolume());

            mediaPlayer.play();
            System.out.println("▶️ Lecture audio démarrée");
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement audio : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeBackground() {
        Image bg = new Image(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/assets/images/background.png"
        ).toExternalForm());
        background.setImage(bg);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
    }

    private void initializeFlyingObjects() {
        montgolfiere = new ImageView(new Image(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/assets/images/montgolfiere.png").toExternalForm()));
        montgolfiere.setFitWidth(200);
        montgolfiere.setPreserveRatio(true);
        montgolfiere.setLayoutX(-180);
        montgolfiere.setLayoutY(50);

        dirigeable = new ImageView(new Image(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/assets/images/dirigeable_h.png").toExternalForm()));
        dirigeable.setFitWidth(150);
        dirigeable.setPreserveRatio(true);
        dirigeable.setLayoutX(-250);
        dirigeable.setLayoutY(370);

        petitDirigeable = new ImageView(new Image(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/assets/images/dirigeable_fire.png").toExternalForm()));
        petitDirigeable.setFitWidth(280);
        petitDirigeable.setPreserveRatio(true);
        petitDirigeable.setLayoutX(950);
        petitDirigeable.setLayoutY(260);

        root.getChildren().addAll(montgolfiere, dirigeable, petitDirigeable);
    }

    private void initializeAnimations() {
        mt = new TranslateTransition(Duration.seconds(35), montgolfiere);
        mt.setFromX(0);
        mt.setToX(1200);
        mt.setCycleCount(Animation.INDEFINITE);
        mt.setInterpolator(Interpolator.LINEAR);
        mt.play();

        dh = new TranslateTransition(Duration.seconds(28), dirigeable);
        dh.setFromX(0);
        dh.setToX(1200);
        dh.setCycleCount(Animation.INDEFINITE);
        dh.setInterpolator(Interpolator.LINEAR);
        dh.play();

        df = new TranslateTransition(Duration.seconds(25), petitDirigeable);
        df.setFromX(0);
        df.setToX(-1200);
        df.setCycleCount(Animation.INDEFINITE);
        df.setInterpolator(Interpolator.LINEAR);
        df.play();
    }

    private void initializeLogo() {
        logo = new ImageView(new Image(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/assets/images/logo.png").toExternalForm()));
        logo.setFitWidth(750);
        logo.setPreserveRatio(true);
        logo.setLayoutX(60);
        logo.setLayoutY(50);
        logo.setVisible(false);
        root.getChildren().add(logo);

        logoPause = new PauseTransition(Duration.seconds(3.5));
        logoPause.setOnFinished(e -> {
            if (!logoAnimationDone) {
                logo.setVisible(true);
                logo.setOpacity(0);
                logo.setScaleX(0.5);
                logo.setScaleY(0.5);

                FadeTransition fade = new FadeTransition(Duration.seconds(1.5), logo);
                fade.setFromValue(0);
                fade.setToValue(1);

                ScaleTransition scale = new ScaleTransition(Duration.seconds(1.5), logo);
                scale.setFromX(0.5);
                scale.setToX(1);
                scale.setFromY(0.5);
                scale.setToY(1);

                logoAnimation = new ParallelTransition(fade, scale);
                logoAnimation.setOnFinished(event -> logoAnimationDone = true);
                logoAnimation.play();
            }
        });
        logoPause.play();
    }

    private void initializeBottomMenu() {
        try {
            bottomOverlay = FXMLLoader.load(getClass().getResource(
                    "/fr/amu/iut/saesuper_bomberman/components/BottomOverlayMenu.fxml"));
            bottomOverlay.setVisible(false);
            root.getChildren().add(bottomOverlay);

            Platform.runLater(() -> {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.setUserData(this);

                if (root.getScene() != null) {
                    root.getScene().getStylesheets().add(getClass().getResource(
                            "/fr/amu/iut/saesuper_bomberman/assets/styles/bottomMenu.css").toExternalForm());
                }
            });

            AnchorPane.setBottomAnchor(bottomOverlay, 0.0);
            AnchorPane.setLeftAnchor(bottomOverlay, 0.0);
            AnchorPane.setRightAnchor(bottomOverlay, 0.0);
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement menu flottant : " + e.getMessage());
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            double normalizedVolume = volume / 100.0;
            mediaPlayer.setVolume(normalizedVolume);
            System.out.println("🔊 Volume changé : " + normalizedVolume);
        } else {
            System.err.println("❌ MediaPlayer est null");
        }
    }

    public void muteAudio(boolean mute) {
        if (mediaPlayer != null) {
            if (mute) {
                mediaPlayer.setVolume(0);
            } else {
                mediaPlayer.setVolume(0.5);
            }
        }
    }

    public void pauseAllAnimations() {
        mt.pause();
        dh.pause();
        df.pause();
        if (!logoAnimationDone) {
            logoPause.pause();
            if (logoAnimation != null) logoAnimation.pause();
        }
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void resumeAllAnimations() {
        mt.play();
        dh.play();
        df.play();
        if (!logoAnimationDone) {
            logoPause.play();
            if (logoAnimation != null) logoAnimation.play();
        }
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void restartAllAnimations() {
        // Réinitialisation des animations
        montgolfiere.setLayoutX(-180);
        dirigeable.setLayoutX(-250);
        petitDirigeable.setLayoutX(950);

        mt.stop();
        dh.stop();
        df.stop();

        logo.setVisible(false);
        logoAnimationDone = false;
        if (logoAnimation != null) {
            logoAnimation.stop();
        }
        if (logoPause != null) {
            logoPause.stop();
        }

        // Redémarrage de l'audio depuis le début
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.play();
        }

        // Redémarrage des animations
        mt.playFromStart();
        dh.playFromStart();
        df.playFromStart();
        logoPause.playFromStart();
    }
}