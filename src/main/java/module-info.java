module fr.amu.iut.saesuper_bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires javafx.media;

    exports fr.amu.iut.saesuper_bomberman;
    exports fr.amu.iut.saesuper_bomberman.components;
    exports fr.amu.iut.saesuper_bomberman.controllerfxml;

    opens fr.amu.iut.saesuper_bomberman to javafx.fxml;
    opens fr.amu.iut.saesuper_bomberman.components to javafx.fxml;
    opens fr.amu.iut.saesuper_bomberman.controllerfxml to javafx.fxml;
}