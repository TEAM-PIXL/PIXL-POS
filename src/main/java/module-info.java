module com.example.pixlpos {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pixlpos to javafx.fxml;
    exports com.example.pixlpos;
}