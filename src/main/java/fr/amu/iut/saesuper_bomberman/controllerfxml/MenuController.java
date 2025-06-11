package fr.amu.iut.saesuper_bomberman.controllerfxml;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    private Scene originalScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAudio();
        initializeBackground();
        initializeFlyingObjects();
        initializeAnimations();
        initializeLogo();
        initializeBottomMenu();
        initializeMainMenuButtons();
    }

    private void initializeAudio() {
        try {
            String musicFile = "/fr/amu/iut/saesuper_bomberman/assets/audio/music.mp3";
            URL musicUrl = getClass().getResource(musicFile);
            if (musicUrl == null) {
                System.err.println("\u274C Fichier audio non trouv\u00e9 : " + musicFile);
                return;
            }
            Media media = new Media(musicUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.5);
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("\u274C Erreur chargement audio : " + e.getMessage());
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
            System.err.println("\u274C Erreur chargement menu flottant : " + e.getMessage());
        }
    }


    private void initializeMainMenuButtons() {
        Button btnSeConnecter = new Button("SE CONNECTER");
        Button btnInvite = new Button("JOUER EN TANT QU'INVITE");
        btnSeConnecter.getStyleClass().add("menu-button");
        btnInvite.getStyleClass().add("menu-button");

        ImageView triangleSeConnecter = new ImageView(new Image(getClass().getResource("/fr/amu/iut/saesuper_bomberman/assets/images/triangle.png").toExternalForm()));
        triangleSeConnecter.setFitWidth(24);
        triangleSeConnecter.setFitHeight(24);
        triangleSeConnecter.setVisible(false);
        btnSeConnecter.setGraphic(triangleSeConnecter);
        btnSeConnecter.setGraphicTextGap(12);
        btnSeConnecter.setOnMouseEntered(e -> triangleSeConnecter.setVisible(true));
        btnSeConnecter.setOnMouseExited(e -> triangleSeConnecter.setVisible(false));

        // Triangle pour JOUER EN TANT QU'INVITE
        ImageView triangleInvite = new ImageView(new Image(getClass().getResource("/fr/amu/iut/saesuper_bomberman/assets/images/triangle.png").toExternalForm()));
        triangleInvite.setFitWidth(24);
        triangleInvite.setFitHeight(24);
        triangleInvite.setVisible(false);
        btnInvite.setGraphic(triangleInvite);
        btnInvite.setGraphicTextGap(12);
        btnInvite.setOnMouseEntered(e -> triangleInvite.setVisible(true));
        btnInvite.setOnMouseExited(e -> triangleInvite.setVisible(false));



        btnSeConnecter.setOnAction(e -> {
            try {
                originalScene = btnSeConnecter.getScene();
                AnchorPane loginRoot = new AnchorPane();

                ImageView loginBackground = new ImageView(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/images/background.png").toExternalForm()));
                loginBackground.fitWidthProperty().bind(loginRoot.widthProperty());
                loginBackground.fitHeightProperty().bind(loginRoot.heightProperty());

                ImageView loginMontgolfiere = new ImageView(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/images/montgolfiere.png").toExternalForm()));
                loginMontgolfiere.setFitWidth(200);
                loginMontgolfiere.setPreserveRatio(true);
                loginMontgolfiere.setLayoutX(-180);
                loginMontgolfiere.setLayoutY(50);

                ImageView loginDirigeable = new ImageView(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/images/dirigeable_h.png").toExternalForm()));
                loginDirigeable.setFitWidth(150);
                loginDirigeable.setPreserveRatio(true);
                loginDirigeable.setLayoutX(-250);
                loginDirigeable.setLayoutY(370);

                ImageView loginPetitDirigeable = new ImageView(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/images/dirigeable_fire.png").toExternalForm()));
                loginPetitDirigeable.setFitWidth(280);
                loginPetitDirigeable.setPreserveRatio(true);
                loginPetitDirigeable.setLayoutX(950);
                loginPetitDirigeable.setLayoutY(260);

                loginRoot.getChildren().addAll(loginBackground, loginMontgolfiere, loginDirigeable, loginPetitDirigeable);

                Button backButton = new Button();
                ImageView backArrow = new ImageView(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/images/retour.png").toExternalForm()));
                backArrow.setFitWidth(40);
                backArrow.setFitHeight(40);
                backButton.setGraphic(backArrow);
                backButton.setStyle("-fx-background-color: transparent; -fx-padding: 10px;");
                AnchorPane.setTopAnchor(backButton, 20.0);
                AnchorPane.setLeftAnchor(backButton, 20.0);
                backButton.setOnAction(evt -> {
                    Stage stage = (Stage) backButton.getScene().getWindow();
                    stage.setScene(originalScene);
                });
                loginRoot.getChildren().add(backButton);

                VBox playersBox = new VBox(20);
                playersBox.setLayoutX(250);
                playersBox.setLayoutY(150);

                for (int i = 1; i <= 4; i++) {
                    javafx.scene.layout.HBox playerRow = new javafx.scene.layout.HBox(10);
                    StackPane playerStack = new StackPane();
                    Rectangle playerSquare = new Rectangle();
                    playerSquare.setWidth(70);
                    playerSquare.setHeight(70);
                    playerSquare.setFill(Color.LIGHTGRAY);

                    ImageView playerIcon = new ImageView(new Image(getClass().getResource(
                            "/fr/amu/iut/saesuper_bomberman/assets/images/joueur" + i + ".png").toExternalForm()));
                    playerIcon.setFitWidth(70);
                    playerIcon.setFitHeight(70);
                    playerIcon.setPreserveRatio(true);

                    playerStack.getChildren().addAll(playerSquare, playerIcon);

                    TextField playerField = new TextField();
                    playerField.setPromptText("Joueur " + i);
                    playerField.setPrefWidth(300);
                    playerField.setPrefHeight(70);
                    playerField.getStyleClass().add("player-textfield");

                    playerRow.getChildren().addAll(playerStack, playerField);
                    playersBox.getChildren().add(playerRow);
                }
                loginRoot.getChildren().add(playersBox);

                Button startButton = new Button("START");
                startButton.setStyle("-fx-background-color: #22ca22;-fx-text-fill: white;-fx-font-family: 'Press Start 2P';-fx-font-size: 20px;-fx-min-width: 100px;-fx-min-height: 40px;");
                AnchorPane.setBottomAnchor(startButton, 100.0);
                AnchorPane.setRightAnchor(startButton, 50.0);
                loginRoot.getChildren().add(startButton);

                TranslateTransition mtLogin = new TranslateTransition(Duration.seconds(35), loginMontgolfiere);
                mtLogin.setFromX(0); mtLogin.setToX(1200);
                mtLogin.setCycleCount(Animation.INDEFINITE);
                mtLogin.setInterpolator(Interpolator.LINEAR);
                mtLogin.play();

                TranslateTransition dhLogin = new TranslateTransition(Duration.seconds(28), loginDirigeable);
                dhLogin.setFromX(0); dhLogin.setToX(1200);
                dhLogin.setCycleCount(Animation.INDEFINITE);
                dhLogin.setInterpolator(Interpolator.LINEAR);
                dhLogin.play();

                TranslateTransition dfLogin = new TranslateTransition(Duration.seconds(25), loginPetitDirigeable);
                dfLogin.setFromX(0); dfLogin.setToX(-1200);
                dfLogin.setCycleCount(Animation.INDEFINITE);
                dfLogin.setInterpolator(Interpolator.LINEAR);
                dfLogin.play();

                Scene loginScene = new Scene(loginRoot, 900, 700);
                Stage stage = (Stage) btnSeConnecter.getScene().getWindow();
                stage.setScene(loginScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        VBox menuBox = new VBox(6, btnSeConnecter, btnInvite);
        menuBox.setOpacity(0);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setLayoutX(97);
        menuBox.setLayoutY(470);
        root.getChildren().add(menuBox);

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

                FadeTransition menuFade = new FadeTransition(Duration.seconds(1.5), menuBox);
                menuFade.setFromValue(0);
                menuFade.setToValue(1);

                ScaleTransition menuScale = new ScaleTransition(Duration.seconds(1.5), menuBox);
                menuScale.setFromX(0.5);
                menuScale.setToX(1);
                menuScale.setFromY(0.5);
                menuScale.setToY(1);

                logoAnimation = new ParallelTransition(fade, scale, menuFade, menuScale);
                logoAnimation.setOnFinished(event -> logoAnimationDone = true);
                logoAnimation.play();
            }
        });
        logoPause.play();
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume / 100.0);
        }
    }

    public void muteAudio(boolean mute) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(mute ? 0 : 0.5);
        }
    }

    public void pauseAllAnimations() {
        mt.pause(); dh.pause(); df.pause();
        if (!logoAnimationDone) {
            logoPause.pause();
            if (logoAnimation != null) logoAnimation.pause();
        }
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public void resumeAllAnimations() {
        mt.play(); dh.play(); df.play();
        if (!logoAnimationDone) {
            logoPause.play();
            if (logoAnimation != null) logoAnimation.play();
        }
        if (mediaPlayer != null) mediaPlayer.play();
    }

public void restartAllAnimations() {
    // Réinitialisation des positions des objets volants
    montgolfiere.setLayoutX(-180);
    dirigeable.setLayoutX(-250);
    petitDirigeable.setLayoutX(950);

    // Arrêt des animations en cours
    mt.stop();
    dh.stop();
    df.stop();

    // Réinitialisation du logo
    logo.setVisible(true);
    logo.setOpacity(0);
    logo.setScaleX(0.5);
    logo.setScaleY(0.5);
    logoAnimationDone = false;

    // Récupérer la VBox des boutons
    VBox menuBox = root.getChildren().stream()
            .filter(node -> node instanceof VBox && node.getLayoutY() == 470)
            .map(node -> (VBox) node)
            .findFirst()
            .orElse(null);

    if (menuBox != null) {
        // Réinitialisation des boutons
        menuBox.setVisible(true);
        menuBox.setOpacity(0);
        menuBox.setScaleX(0.5);
        menuBox.setScaleY(0.5);

        // Animation des boutons
        FadeTransition menuFade = new FadeTransition(Duration.seconds(1.5), menuBox);
        menuFade.setFromValue(0);
        menuFade.setToValue(1);

        ScaleTransition menuScale = new ScaleTransition(Duration.seconds(1.5), menuBox);
        menuScale.setFromX(0.5);
        menuScale.setToX(1);
        menuScale.setFromY(0.5);
        menuScale.setToY(1);

        ParallelTransition menuAnimation = new ParallelTransition(menuFade, menuScale);
        menuAnimation.play();
    }

    // Animation du logo
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

    if (mediaPlayer != null) {
        mediaPlayer.stop();
        mediaPlayer.play();
    }

    // Redémarrage des animations de base
    mt.playFromStart();
    dh.playFromStart();
    df.playFromStart();
}



}