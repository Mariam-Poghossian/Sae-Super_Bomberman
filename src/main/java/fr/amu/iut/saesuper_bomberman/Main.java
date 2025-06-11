package fr.amu.iut.saesuper_bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée principal de l'application Super Bomberman.
 * Initialise et affiche la fenêtre principale avec le menu.
 */
public class Main extends Application {

    /**
     * Méthode appelée au lancement de l'application JavaFX.
     * Charge le menu principal depuis le FXML et applique le style CSS.
     *
     * @param stage la fenêtre principale de l'application
     * @throws Exception en cas d'erreur de chargement du FXML ou des ressources
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Taille manuelle correspondant à background.png redimensionné
        double width = 860;
        double height = 650;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/controllerfxml/menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add(getClass().getResource(
                "/fr/amu/iut/saesuper_bomberman/assets/styles/menu.css"
        ).toExternalForm());
        stage.setTitle("Super Bomberman");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Méthode main : lance l'application JavaFX.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch();
    }
}