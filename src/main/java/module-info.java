module fr.amu.iut.saesuper_bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    // Exporter le package principal (où se trouve BombermanApplication)
    exports fr.amu.iut.saesuper_bomberman;
    opens fr.amu.iut.saesuper_bomberman to javafx.fxml;

    // Exporter les autres packages si nécessaire
    exports fr.amu.iut.saesuper_bomberman.model;
    opens fr.amu.iut.saesuper_bomberman.model to javafx.fxml;

    exports fr.amu.iut.saesuper_bomberman.controllerfxml;
    opens fr.amu.iut.saesuper_bomberman.controllerfxml to javafx.fxml;
}