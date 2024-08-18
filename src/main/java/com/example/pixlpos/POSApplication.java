package com.example.pixlpos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.InputStream;

import java.io.IOException;

public class POSApplication extends Application {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(POSApplication.class.getResource("/fxml/login-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle("Login Screen");
        InputStream imageStream = getClass().getResourceAsStream("/icon.JPG");
        if (imageStream == null) {
            throw new IOException("Image resource not found: /icon.JPG");
        }
        Image image = new Image(imageStream);
        stage.getIcons().add(image);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}