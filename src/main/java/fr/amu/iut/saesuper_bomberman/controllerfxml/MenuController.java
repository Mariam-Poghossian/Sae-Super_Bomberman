package fr.amu.iut.saesuper_bomberman.controllerfxml;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * Contrôleur du menu principal de Super Bomberman.
 * Gère l'initialisation de l'interface, les animations, la musique,
 * et la navigation entre les différentes scènes (connexion, invité, etc.).
 *
 * - Affiche le fond animé et le logo.
 * - Gère les boutons principaux (connexion, invité).
 * - Lance et contrôle la musique de fond.
 * - Permet de mettre en pause, reprendre ou réinitialiser les animations et la musique.
 * - Charge dynamiquement le menu flottant du bas.
 *
 * FXML associé : Menu.fxml
 */
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

    private List<TextField> playerFields = new ArrayList<>();



    /**
     * Initialise tous les éléments graphiques et animations du menu.
     */
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
    /**
     * Initialise et lance la musique de fond.
     */
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
    /**
     * Charge et adapte l'image de fond.
     */
    private void initializeBackground() {
        Image bg = new Image(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/assets/images/background.png"
        ).toExternalForm());
        background.setImage(bg);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
    }

    /**
     * Ajoute les objets volants (montgolfière, dirigeables) à la scène.
     */
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
    /**
     * Démarre les animations des objets volants.
     */
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

    /**
     * Ajoute le logo du jeu et prépare son animation.
     */
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

    /**
     * Charge le menu flottant du bas.
     */
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

    /**
     * Crée et configure les boutons principaux du menu.
     */

    private void initializeMainMenuButtons() {
        Button btnSeConnecter = new Button("JEU NORMAL");
        Button btnInvite = new Button("CAPTURE THE FLAG");
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



        btnSeConnecter.setOnAction(event1 -> {
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
                    HBox playerRow = new HBox(10);
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
                    playerFields.add(playerField);
                    playerField.setPromptText("Joueur " + i);
                    playerField.setPrefWidth(300);
                    playerField.setPrefHeight(70);
                    playerField.getStyleClass().add("player-textfield");

                    playerRow.getChildren().addAll(playerStack, playerField);
                    playersBox.getChildren().add(playerRow);
                }
                loginRoot.getChildren().add(playersBox);

                Button suivantButton = new Button("SUIVANT");
                suivantButton.setStyle("-fx-background-color: #22ca22;-fx-text-fill: white;-fx-font-family: 'Press Start 2P';-fx-font-size: 20px;-fx-min-width: 100px;-fx-min-height: 40px;");
                AnchorPane.setBottomAnchor(suivantButton, 100.0);
                AnchorPane.setRightAnchor(suivantButton, 50.0);
                loginRoot.getChildren().add(suivantButton);

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

                Scene loginScene = new Scene(loginRoot,
                        originalScene.getWidth(),
                        originalScene.getHeight());
                Stage stage = (Stage) btnSeConnecter.getScene().getWindow();
                stage.setScene(loginScene);

                suivantButton.setOnAction(evt -> {
                    try {
                        AnchorPane themeRoot = new AnchorPane();

                        // Fond
                        ImageView bg = new ImageView(new Image(getClass().getResource(
                                "/fr/amu/iut/saesuper_bomberman/assets/images/background.png").toExternalForm()));
                        bg.fitWidthProperty().bind(themeRoot.widthProperty());
                        bg.fitHeightProperty().bind(themeRoot.heightProperty());
                        themeRoot.getChildren().add(bg);

                        // Objets volants
                        ImageView m = new ImageView(new Image(getClass().getResource(
                                "/fr/amu/iut/saesuper_bomberman/assets/images/montgolfiere.png").toExternalForm()));
                        m.setFitWidth(200); m.setPreserveRatio(true); m.setLayoutX(-180); m.setLayoutY(50);

                        ImageView d = new ImageView(new Image(getClass().getResource(
                                "/fr/amu/iut/saesuper_bomberman/assets/images/dirigeable_h.png").toExternalForm()));
                        d.setFitWidth(150); d.setPreserveRatio(true); d.setLayoutX(-250); d.setLayoutY(370);

                        ImageView df = new ImageView(new Image(getClass().getResource(
                                "/fr/amu/iut/saesuper_bomberman/assets/images/dirigeable_fire.png").toExternalForm()));
                        df.setFitWidth(280); df.setPreserveRatio(true); df.setLayoutX(950); df.setLayoutY(260);

                        themeRoot.getChildren().addAll(m, d, df);

                        // Bouton retour
                        Button back = new Button();
                        ImageView arrow = new ImageView(new Image(getClass().getResource(
                                "/fr/amu/iut/saesuper_bomberman/assets/images/retour.png").toExternalForm()));
                        arrow.setFitWidth(40); arrow.setFitHeight(40);
                        back.setGraphic(arrow);
                        back.setStyle("-fx-background-color: transparent; -fx-padding: 10px;");
                        AnchorPane.setTopAnchor(back, 20.0);
                        AnchorPane.setLeftAnchor(back, 20.0);
                        back.setOnAction(ev -> stage.setScene(loginScene));
                        themeRoot.getChildren().add(back);

                        // Sélection des thèmes
                        HBox themes = new HBox(40);
                        themes.setAlignment(Pos.CENTER);
                        AnchorPane.setTopAnchor(themes, 150.0);
                        AnchorPane.setLeftAnchor(themes, 50.0);
                        AnchorPane.setRightAnchor(themes, 50.0);

                        String[] themePaths = {
                                "/fr/amu/iut/saesuper_bomberman/assets/images/theme_1.png",
                                "/fr/amu/iut/saesuper_bomberman/assets/images/theme_2.png",
                                "/fr/amu/iut/saesuper_bomberman/assets/images/theme_3.png"
                        };

                        int[] selectedTheme = {0};

                        for (int i = 0; i < 3; i++) {
                            VBox box = new VBox(10);
                            box.setAlignment(Pos.CENTER);
                            box.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: " + (i == 0 ? "#22ca22" : "transparent") + ";");

                            final int themeNumber = i + 1;

                            ImageView img = new ImageView(new Image(getClass().getResource(themePaths[i]).toExternalForm()));
                            img.setFitWidth(200); img.setFitHeight(200);

                            Label label = new Label("THÈME " + themeNumber);
                            label.setStyle("-fx-font-family: 'Press Start 2P'; -fx-font-size: 20px; -fx-text-fill: white;");

                            EventHandler<MouseEvent> selectHandler = event2 -> {
                                themes.getChildren().forEach(node ->
                                        ((VBox) node).setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: transparent;"));
                                box.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: #22ca22; -fx-background-color: rgba(34, 202, 34, 0.2);");
                                selectedTheme[0] = themeNumber - 1;
                            };

                            img.setOnMouseClicked(selectHandler);
                            label.setOnMouseClicked(selectHandler);

                            box.setOnMouseEntered(ev -> {
                                if (selectedTheme[0] != themeNumber - 1) {
                                    box.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: #ffffff; -fx-background-color: rgba(255,255,255,0.1);");
                                }
                            });

                            box.setOnMouseExited(ev -> {
                                if (selectedTheme[0] != themeNumber - 1) {
                                    box.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: transparent;");
                                } else {
                                    box.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: #22ca22; -fx-background-color: rgba(34, 202, 34, 0.2);");
                                }
                            });

                            VBox.setMargin(label, new Insets(0, 0, 10, 0));
                            box.getChildren().addAll(img, label);
                            themes.getChildren().add(box);
                        }

                        themeRoot.getChildren().add(themes);

                        // Bouton JOUER
                        Button jouer = new Button("JOUER");
                        jouer.setStyle("-fx-background-color: #22ca22; -fx-text-fill: white; -fx-font-family: 'Press Start 2P'; -fx-font-size: 20px; -fx-min-width: 100px; -fx-min-height: 40px;");
                        AnchorPane.setBottomAnchor(jouer, 100.0);
                        AnchorPane.setRightAnchor(jouer, 50.0);
                        themeRoot.getChildren().add(jouer);

                        jouer.setOnAction(ev -> {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/amu/iut/saesuper_bomberman/controllerfxml/game.fxml"));
                                Parent gameRoot = loader.load();
                                GameController controller = loader.getController();
                                List<String> playerNames = getPlayerNames();
                                controller.setPlayerNames(playerNames);
                                Scene gameScene = new Scene(gameRoot);
                                controller.setScene(gameScene);
                                Stage stagePlay = (Stage) jouer.getScene().getWindow();
                                stagePlay.setScene(gameScene);
                                controller.startGame();
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        });

                        // Transitions (au bon format)
                        TranslateTransition mtTheme = new TranslateTransition(Duration.seconds(35), m);
                        mtTheme.setFromX(0);
                        mtTheme.setToX(1200);
                        mtTheme.setCycleCount(Animation.INDEFINITE);
                        mtTheme.setInterpolator(Interpolator.LINEAR);
                        mtTheme.play();

                        TranslateTransition dhTheme = new TranslateTransition(Duration.seconds(28), d);
                        dhTheme.setFromX(0);
                        dhTheme.setToX(1200);
                        dhTheme.setCycleCount(Animation.INDEFINITE);
                        dhTheme.setInterpolator(Interpolator.LINEAR);
                        dhTheme.play();

                        TranslateTransition dfTheme = new TranslateTransition(Duration.seconds(25), df);
                        dfTheme.setFromX(0);
                        dfTheme.setToX(-1200);
                        dfTheme.setCycleCount(Animation.INDEFINITE);
                        dfTheme.setInterpolator(Interpolator.LINEAR);
                        dfTheme.play();

                        Scene themeScene = new Scene(themeRoot, loginScene.getWidth(), loginScene.getHeight());

                        stage.setScene(themeScene);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        btnInvite.setOnAction(e -> {
            try {
                originalScene = btnInvite.getScene();
                AnchorPane loginRoot = new AnchorPane();

                // Arrière-plan
                ImageView loginBackground = new ImageView(new Image(getClass().getResource(
                        "/fr/amu/iut/saesuper_bomberman/assets/images/background.png").toExternalForm()));
                loginBackground.fitWidthProperty().bind(loginRoot.widthProperty());
                loginBackground.fitHeightProperty().bind(loginRoot.heightProperty());

                // Objets volants
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

                // Bouton retour
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

                // Box des thèmes
                HBox themesBox = new HBox(40);
                themesBox.setAlignment(Pos.CENTER);
                AnchorPane.setLeftAnchor(themesBox, 0.0);
                AnchorPane.setRightAnchor(themesBox, 0.0);
                themesBox.setLayoutY(150);

                // Configuration des thèmes
                String[] themePaths = {
                        "/fr/amu/iut/saesuper_bomberman/assets/images/theme_1.png",
                        "/fr/amu/iut/saesuper_bomberman/assets/images/theme_2.png",
                        "/fr/amu/iut/saesuper_bomberman/assets/images/theme_3.png"
                };

                int[] selectedTheme = {0}; // Par défaut, thème 1 sélectionné

                for (int i = 0; i < 3; i++) {
                    VBox themeBox = new VBox(10);
                    themeBox.setAlignment(Pos.CENTER);
                    themeBox.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: " + (i == 0 ? "#22ca22" : "transparent") + ";");

                    final int themeNumber = i + 1;

                    ImageView themeImage = new ImageView(new Image(getClass().getResource(themePaths[i]).toExternalForm()));
                    themeImage.setFitWidth(200);
                    themeImage.setFitHeight(200);

                    Button themeBtn = new Button();
                    themeBtn.setGraphic(themeImage);
                    themeBtn.setStyle("-fx-background-color: transparent;");

                    Label themeLabel = new Label("THÈME " + themeNumber);
                    themeLabel.setStyle("-fx-font-family: 'Press Start 2P'; -fx-font-size: 20px; -fx-text-fill: white; -fx-alignment: center;");

                    EventHandler<MouseEvent> clickHandler = event -> {
                        // Réinitialiser tous les styles
                        themesBox.getChildren().forEach(node ->
                                ((VBox)node).setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: transparent;")
                        );
                        // Appliquer le style sélectionné
                        themeBox.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: #22ca22; -fx-background-color: rgba(34, 202, 34, 0.2);");
                        selectedTheme[0] = themeNumber - 1;
                    };

                    themeBtn.setOnMouseClicked(clickHandler);
                    themeLabel.setOnMouseClicked(clickHandler);

                    // Effet de survol
                    themeBox.setOnMouseEntered(evt -> {
                        if (selectedTheme[0] != themeNumber - 1) {
                            themeBox.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: #ffffff; -fx-background-color: rgba(255, 255, 255, 0.1);");
                        }
                    });

                    themeBox.setOnMouseExited(evt -> {
                        if (selectedTheme[0] != themeNumber - 1) {
                            themeBox.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: transparent;");
                        } else {
                            themeBox.setStyle("-fx-padding: 10; -fx-border-width: 3; -fx-border-color: #22ca22; -fx-background-color: rgba(34, 202, 34, 0.2);");
                        }
                    });

                    VBox.setMargin(themeLabel, new Insets(0, 0, 10, 0));
                    themeBox.getChildren().addAll(themeBtn, themeLabel);
                    themesBox.getChildren().add(themeBox);
                }

                loginRoot.getChildren().add(themesBox);

                // Bouton START
                Button startButton = new Button("JOUER");
                startButton.setStyle("-fx-background-color: #22ca22; -fx-text-fill: white; -fx-font-family: 'Press Start 2P'; -fx-font-size: 20px; -fx-min-width: 100px; -fx-min-height: 40px;");
                AnchorPane.setBottomAnchor(startButton, 100.0);
                AnchorPane.setRightAnchor(startButton, 50.0);

                startButton.setOnAction(evt -> {
                    try {
                        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/fr/amu/iut/saesuper_bomberman/controllerfxml/game.fxml"));
                        Parent gameRoot = gameLoader.load();
                        GameController gameController = gameLoader.getController();

                        Scene gameScene = new Scene(gameRoot, 860, 650);
                        gameController.setScene(gameScene);

                        Stage stage = (Stage) startButton.getScene().getWindow();
                        stage.setScene(gameScene);
                        stage.setResizable(false);

                        gameController.startGame();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                loginRoot.getChildren().add(startButton);

                // Animations des objets volants
                TranslateTransition mtLogin = new TranslateTransition(Duration.seconds(35), loginMontgolfiere);
                mtLogin.setFromX(0);
                mtLogin.setToX(1200);
                mtLogin.setCycleCount(Animation.INDEFINITE);
                mtLogin.setInterpolator(Interpolator.LINEAR);
                mtLogin.play();

                TranslateTransition dhLogin = new TranslateTransition(Duration.seconds(28), loginDirigeable);
                dhLogin.setFromX(0);
                dhLogin.setToX(1200);
                dhLogin.setCycleCount(Animation.INDEFINITE);
                dhLogin.setInterpolator(Interpolator.LINEAR);
                dhLogin.play();

                TranslateTransition dfLogin = new TranslateTransition(Duration.seconds(25), loginPetitDirigeable);
                dfLogin.setFromX(0);
                dfLogin.setToX(-1200);
                dfLogin.setCycleCount(Animation.INDEFINITE);
                dfLogin.setInterpolator(Interpolator.LINEAR);
                dfLogin.play();

                Scene loginScene = new Scene(loginRoot, originalScene.getWidth(), originalScene.getHeight());
                Stage stage = (Stage) btnInvite.getScene().getWindow();
                stage.setScene(loginScene);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });








        VBox menuBox = new VBox(6, btnSeConnecter, btnInvite);
        menuBox.setOpacity(0);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setLayoutX(170);
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

    public List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < playerFields.size(); i++) {
            String name = playerFields.get(i).getText().trim();
            names.add(name.isEmpty() ? "Joueur " + (i + 1) : name);
        }
        return names;
    }



    /**
     * Modifie le volume de la musique.
     * @param volume Valeur entre 0 et 100.
     */
    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume / 100.0);
        }
    }
    /**
     * Coupe ou réactive le son.
     * @param mute true pour couper, false pour réactiver.
     */
    public void muteAudio(boolean mute) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(mute ? 0 : 0.5);
        }
    }
    /**
     * Met en pause toutes les animations et la musique.
     */
    public void pauseAllAnimations() {
        mt.pause(); dh.pause(); df.pause();
        if (!logoAnimationDone) {
            logoPause.pause();
            if (logoAnimation != null) logoAnimation.pause();
        }
        if (mediaPlayer != null) mediaPlayer.pause();
    }
    /**
     * Reprend toutes les animations et la musique.
     */
    public void resumeAllAnimations() {
        mt.play(); dh.play(); df.play();
        if (!logoAnimationDone) {
            logoPause.play();
            if (logoAnimation != null) logoAnimation.play();
        }
        if (mediaPlayer != null) mediaPlayer.play();
    }

    /**
     * Réinitialise et relance toutes les animations et la musique.
     */
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