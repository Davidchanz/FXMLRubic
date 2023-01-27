module com.fxmlrubic {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
                        requires org.kordamp.bootstrapfx.core;
                requires com.almasb.fxgl.all;
    
    opens com.fxmlrubic to javafx.fxml;
    exports com.fxmlrubic;
}