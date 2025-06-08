module fr.amu.iut.saesuper_bomberman {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens fr.amu.iut.saesuper_bomberman to javafx.fxml;
    exports fr.amu.iut.saesuper_bomberman;

    exports fr.amu.iut.saesuper_bomberman.controllerfxml;
    opens fr.amu.iut.saesuper_bomberman.controllerfxml to javafx.fxml;

    // ✅ Ajout nécessaire pour éviter l'erreur d'accès
    exports fr.amu.iut.saesuper_bomberman.components;
    opens fr.amu.iut.saesuper_bomberman.components to javafx.fxml;
}
