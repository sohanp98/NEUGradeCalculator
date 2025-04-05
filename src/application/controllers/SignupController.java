package application.controllers;

import java.sql.SQLException;

import application.models.User;
import application.services.UserService;
import application.utils.Navigator;
import application.views.LoginView;

/**
 * Controller class for the signup screen
 */
public class SignupController {
    private UserService userService;
    
    public SignupController() {
        userService = new UserService();
    }
    
    /**
     * Handle user registration
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     * @param password User's password
     * @throws Exception If registration fails
     */
    public void register(String firstName, String lastName, String password) throws Exception {
        try {
            User user = userService.register(firstName, lastName, password);
            
            // Show success message and navigate to login
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully. Please login with your credentials.");
            alert.showAndWait();
            
            navigateToLogin();
        } catch (SQLException e) {
            throw new Exception("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Navigate to the login screen
     */
    public void navigateToLogin() {
        LoginView loginView = new LoginView();
        Navigator.navigateTo(loginView.getView(), "Grade Calculator - Login");
    }
}