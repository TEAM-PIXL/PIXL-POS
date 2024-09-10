module PIXL.POS {

    /*===============================================================================================================================================================================
    -------------------------------------------------------------------------->   !!! IMPORTANT !!!   <---------------------------------------------------------------------------

            This is the module-info.java file for the PIXL POS project. This file is used to declare the dependencies of the project. Make sure to add all the required
                       dependencies here. Especially the JavaFX dependencies. Particularly open and exports the packages that contain any javafx controllers.

    -------------------------------------------------------------------------->   !!! IMPORTANT !!!   <---------------------------------------------------------------------------
    ===============================================================================================================================================================================*/

    requires java.sql;
    requires java.desktop;
    requires java.base;
    requires java.logging;
    requires java.xml;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jbcrypt;
    requires org.junit.jupiter.api;
    requires org.junit.platform.commons;
    requires org.junit.platform.engine;

    opens teampixl.com.pixlpos.application to javafx.fxml;
    opens teampixl.com.pixlpos.controllers.loginconsole to javafx.fxml;
    opens teampixl.com.pixlpos.controllers.adminconsole to javafx.fxml;
    opens teampixl.com.pixlpos.controllers.cookconsole to javafx.fxml;
    opens teampixl.com.pixlpos.controllers.waiterconsole to javafx.fxml;

    exports teampixl.com.pixlpos.application;
    exports teampixl.com.pixlpos.controllers.loginconsole;
    exports teampixl.com.pixlpos.controllers.adminconsole;
    exports teampixl.com.pixlpos.controllers.cookconsole;
    exports teampixl.com.pixlpos.controllers.waiterconsole;
}