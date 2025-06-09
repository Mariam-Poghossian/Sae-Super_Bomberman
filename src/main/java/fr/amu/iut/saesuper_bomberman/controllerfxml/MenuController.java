package fr.amu.iut.saesuper_bomberman.controllerfxml;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

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

    private Node bottomOverlay; // menu flottant

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // === FOND ===
        Image bg = new Image(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/assets/images/background.png"
        ).toExternalForm());
        background.setImage(bg);
        background.setFitWidth(bg.getWidth());
        background.setFitHeight(bg.getHeight());

        // === OBJETS VOLANTS ===
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

        // === ANIMATIONS ===
        TranslateTransition mt = new TranslateTransition(Duration.seconds(35), montgolfiere);
        mt.setFromX(0); mt.setToX(1200);
        mt.setCycleCount(Animation.INDEFINITE);
        mt.setInterpolator(Interpolator.LINEAR);
        mt.play();

        TranslateTransition dh = new TranslateTransition(Duration.seconds(28), dirigeable);
        dh.setFromX(0); dh.setToX(1200);
        dh.setCycleCount(Animation.INDEFINITE);
        dh.setInterpolator(Interpolator.LINEAR);
        dh.play();

        TranslateTransition df = new TranslateTransition(Duration.seconds(25), petitDirigeable);
        df.setFromX(0); df.setToX(-1200);
        df.setCycleCount(Animation.INDEFINITE);
        df.setInterpolator(Interpolator.LINEAR);
        df.play();

        // === LOGO ===
        ImageView logo = new ImageView(new Image(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/assets/images/logo.png").toExternalForm()));
        logo.setFitWidth(750);
        logo.setPreserveRatio(true);
        logo.setLayoutX(60);
        logo.setLayoutY(50);
        logo.setVisible(false);
        root.getChildren().add(logo);

        PauseTransition pause = new PauseTransition(Duration.seconds(3.5));
        pause.setOnFinished(e -> {
            logo.setVisible(true);
            logo.setOpacity(0);
            logo.setScaleX(0.5);
            logo.setScaleY(0.5);
            FadeTransition fade = new FadeTransition(Duration.seconds(1.5), logo);
            fade.setFromValue(0); fade.setToValue(1);
            ScaleTransition scale = new ScaleTransition(Duration.seconds(1.5), logo);
            scale.setFromX(0.5); scale.setToX(1);
            scale.setFromY(0.5); scale.setToY(1);
            new ParallelTransition(fade, scale).play();
        });
        pause.play();

        // === ⬇️ MENU FLOTTANT — visible si souris dans la fenêtre ===
        try {
            bottomOverlay = FXMLLoader.load(getClass().getResource(
                    "/fr/amu/iut/saesuper_bomberman/components/BottomOverlayMenu.fxml"));
            bottomOverlay.setVisible(false);
            root.getChildren().add(bottomOverlay);

            // 🔽 Ajout pour l’ancrer en bas
            AnchorPane.setBottomAnchor(bottomOverlay, 0.0);
            AnchorPane.setLeftAnchor(bottomOverlay, 0.0);
            AnchorPane.setRightAnchor(bottomOverlay, 0.0);
        } catch (Exception e) {
            System.err.println("Erreur chargement menu flottant : " + e.getMessage());
        }


        root.setOnMouseEntered(e -> {
            if (bottomOverlay != null) bottomOverlay.setVisible(true);
        });

        root.setOnMouseExited(e -> {
            if (bottomOverlay != null) bottomOverlay.setVisible(false);
        });
    }
}
