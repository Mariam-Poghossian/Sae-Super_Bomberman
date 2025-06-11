module fr.amu.iut.saesuper_bomberman {
    // Exports existants
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens fr.amu.iut.saesuper_bomberman to javafx.fxml;
    opens fr.amu.iut.saesuper_bomberman.controllerfxml to javafx.fxml;
    opens fr.amu.iut.saesuper_bomberman.components to javafx.fxml;

    // Ajouter ces lignes pour le package model
    opens fr.amu.iut.saesuper_bomberman.model to javafx.fxml;
    exports fr.amu.iut.saesuper_bomberman.model;

    exports fr.amu.iut.saesuper_bomberman;
    exports fr.amu.iut.saesuper_bomberman.controllerfxml;
    exports fr.amu.iut.saesuper_bomberman.components;
}