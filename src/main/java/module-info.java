module com.fxmlrubic {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.fxmlrubic to javafx.fxml;
    exports com.fxmlrubic;
    exports com.fxmlrubic.controller;
    opens com.fxmlrubic.controller to javafx.fxml;
    exports com.fxmlrubic.game;
    opens com.fxmlrubic.game to javafx.fxml;
    exports com.fxmlrubic.utils;
    opens com.fxmlrubic.utils to javafx.fxml;
}