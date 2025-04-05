package application;

import javafx.application.Application;
import javafx.stage.Stage;

import application.utils.DatabaseHelper;
import application.utils.Navigator;
import application.views.LoginView;

/**
 * Main application class
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Set up database
            DatabaseHelper.getInstance();
            
            // Set up navigator
            Navigator.setPrimaryStage(primaryStage);
            
            // Load login view
            LoginView loginView = new LoginView();
            primaryStage.setTitle("Grade Calculator - Login");
            primaryStage.setScene(new javafx.scene.Scene(loginView.getView(), 600, 400));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        // Close database connection
        DatabaseHelper.getInstance().closeConnection();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}