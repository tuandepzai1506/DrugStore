package com.quanlykho;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.quanlykho.util.DatabaseConnection;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            // Initialize database
            DatabaseConnection.initializeDatabase();
            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
        
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/quanlykho/fxml/MainView.fxml")
        );

        Scene scene = new Scene(loader.load(), 900, 600);
        stage.setTitle("Drug Store");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
