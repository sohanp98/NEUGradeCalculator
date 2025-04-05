package application.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class for navigating between scenes
 */
public class Navigator {
    private static Stage primaryStage;
    
    /**
     * Set the primary stage for the application
     * 
     * @param stage The primary stage
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
    
    /**
     * Navigate to a new scene
     * 
     * @param root The root node of the new scene
     * @param title The title for the window
     */
    public static void navigateTo(Parent root, String title) {
        primaryStage.setTitle(title);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Get the primary stage
     * 
     * @return The primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}