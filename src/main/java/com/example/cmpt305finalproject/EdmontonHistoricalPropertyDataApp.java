package com.example.cmpt305finalproject;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;

public class EdmontonHistoricalPropertyDataApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EdmontonHistoricalPropertyDataApp.class.getResource("edmonton-historical-property-data-view.fxml"));
        // 1600 px fits the filters, buttons, chart and every table column side by side.
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(fxmlLoader.load(), Math.min(1600, screen.getWidth()), Math.min(900, screen.getHeight()));
        stage.setTitle("Edmonton Historical Property Data");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}