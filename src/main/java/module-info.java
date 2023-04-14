module com.example.fortbyte_conglomerate {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.sql;
    requires org.controlsfx.controls;
    //requires javatuples;

    opens com.example.fortbyte_conglomerate to javafx.fxml;
    exports com.example.fortbyte_conglomerate;
    exports com.example.fortbyte_conglomerate.controller;
    opens com.example.fortbyte_conglomerate.controller to javafx.fxml;
}