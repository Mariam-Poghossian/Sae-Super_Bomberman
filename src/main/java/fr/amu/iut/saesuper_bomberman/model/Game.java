package fr.amu.iut.saesuper_bomberman.model;

import fr.amu.iut.saesuper_bomberman.controllerfxml.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Game extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/amu/iut/saesuper_bomberman/controllerfxml/game.fxml"));
        Parent root = loader.load();

        GameController controller = loader.getController();

        Scene scene = new Scene(root);

        // Passer la scène au contrôleur pour gérer les événements clavier
        controller.setScene(scene);

        primaryStage.setTitle("Bomberman");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Démarrer le jeu
        controller.startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}