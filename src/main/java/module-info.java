module PIXL.POS {
    requires java.sql;
    requires java.desktop;
    requires java.base;
    requires java.logging;
    requires java.xml;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jbcrypt;

    opens teampixl.com.pixlpos.controllers to javafx.fxml;
    exports teampixl.com.pixlpos;
}