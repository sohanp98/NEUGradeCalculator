package application.controllers;

import java.sql.SQLException;

import application.models.User;
import application.services.UserService;
import application.utils.Navigator;
import application.views.HomeView;
import application.views.SignupView;

/**
 * Controller class for the login screen
 */
public class LoginController {
    private UserService userService;
    
    public LoginController() {
        userService = new UserService();
    }
    
    /**
     * Handle user login
     * 
     * @param firstName User's first name
     * @param password User's password
     * @throws Exception If login fails
     */
    public void login(String firstName, String password) throws Exception {
        try {
            User user = userService.login(firstName, password);
            navigateToHome(user);
        } catch (SQLException e) {
            throw new Exception("Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Navigate to the home screen
     * 
     * @param user The logged in user
     */
    private void navigateToHome(User user) {
        HomeView homeView = new HomeView();
        Navigator.navigateTo(homeView.getView(), "Grade Calculator - Welcome, " + user.getFirstName());
    }
    
    /**
     * Navigate to the signup screen
     */
    public void navigateToSignup() {
        SignupView signupView = new SignupView();
        Navigator.navigateTo(signupView.getView(), "Grade Calculator - Create Account");
    }
}