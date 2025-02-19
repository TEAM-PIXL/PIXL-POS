module PIXL.POS {

    /*===============================================================================================================================================================================
    -------------------------------------------------------------------------->   !!! IMPORTANT !!!   <---------------------------------------------------------------------------

            This is the module-info.java file for the PIXL POS project. This file is used to declare the dependencies of the project. Make sure to add all the required
                       dependencies here. Especially the JavaFX dependencies. Particularly open and exports the packages that contain any javafx controllers.

    -------------------------------------------------------------------------->   !!! IMPORTANT !!!   <---------------------------------------------------------------------------
    ===============================================================================================================================================================================*/

    requires java.sql;
    requires java.desktop;
    requires java.logging;
    requires java.xml;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires jbcrypt;
    //requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires org.junit.platform.commons;
    requires org.junit.jupiter;
    requires java.compiler;
    requires geoip2;
    requires com.zaxxer.hikari;
    requires org.json;

    opens teampixl.com.pixlpos.database;
    opens teampixl.com.pixlpos.authentication;
    opens teampixl.com.pixlpos.application;
    opens teampixl.com.pixlpos.controllers.loginconsole;
    opens teampixl.com.pixlpos.controllers.adminconsole;
    opens teampixl.com.pixlpos.controllers.cookconsole;
    opens teampixl.com.pixlpos.controllers.waiterconsole;
    opens teampixl.com.pixlpos.database.api;
    opens teampixl.com.pixlpos.database.api.util;
    opens teampixl.com.pixlpos.models;
    opens teampixl.com.pixlpos.models.logs;
    opens teampixl.com.pixlpos.models.tools;
    opens teampixl.com.pixlpos.models.logs.network;
    opens teampixl.com.pixlpos.models.logs.definitions;

    exports teampixl.com.pixlpos;
    exports teampixl.com.pixlpos.application;
    exports teampixl.com.pixlpos.controllers.loginconsole;
    exports teampixl.com.pixlpos.controllers.adminconsole;
    exports teampixl.com.pixlpos.controllers.cookconsole;
    exports teampixl.com.pixlpos.controllers.waiterconsole;
    exports teampixl.com.pixlpos.database;
    exports teampixl.com.pixlpos.authentication;
    exports teampixl.com.pixlpos.database.api;
    exports teampixl.com.pixlpos.database.api.util;
    exports teampixl.com.pixlpos.models;
    exports teampixl.com.pixlpos.models.tools;
    exports teampixl.com.pixlpos.models.logs;
    exports teampixl.com.pixlpos.models.logs.network;
    exports teampixl.com.pixlpos.models.logs.definitions;
}