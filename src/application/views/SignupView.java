package application.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import application.controllers.SignupController;

/**
 * View class for the signup screen
 */
public class SignupView {
    private BorderPane mainLayout;
    private SignupController controller;
    
    public SignupView() {
        controller = new SignupController();
        initialize();
    }
    
    private void initialize() {
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        
        // Create header
        Text headerText = new Text("Northeastern University");
        headerText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Text subHeaderText = new Text("Create Account");
        subHeaderText.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getChildren().addAll(headerText, subHeaderText);
        mainLayout.setTop(headerBox);
        BorderPane.setMargin(headerBox, new Insets(0, 0, 20, 0));
        
        // Create form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        Label firstNameLabel = new Label("First Name:");
        grid.add(firstNameLabel, 0, 0);
        
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter your first name");
        grid.add(firstNameField, 1, 0);
        
        Label lastNameLabel = new Label("Last Name:");
        grid.add(lastNameLabel, 0, 1);
        
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter your last name");
        grid.add(lastNameField, 1, 1);
        
        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        grid.add(passwordField, 1, 2);
        
        Label confirmPasswordLabel = new Label("Confirm Password:");
        grid.add(confirmPasswordLabel, 0, 3);
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        grid.add(confirmPasswordField, 1, 3);
        
        Button registerButton = new Button("Register");
        registerButton.setDefaultButton(true);
        
        Button backButton = new Button("Back to Login");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(backButton, registerButton);
        grid.add(buttonBox, 1, 4);
        
        mainLayout.setCenter(grid);
        
        // Set up event handlers
        registerButton.setOnAction(e -> {
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showErrorAlert("Registration Error", "Passwords do not match");
                return;
            }
            
            try {
                controller.register(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    passwordField.getText()
                );
            } catch (Exception ex) {
                showErrorAlert("Registration Error", ex.getMessage());
            }
        });
        
        backButton.setOnAction(e -> {
            controller.navigateToLogin();
        });
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public BorderPane getView() {
        return mainLayout;
    }
}