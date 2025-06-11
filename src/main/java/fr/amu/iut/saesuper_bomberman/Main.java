package fr.amu.iut.saesuper_bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

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

    public static void main(String[] args) {
        launch();
    }
}
