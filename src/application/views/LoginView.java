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

import application.controllers.LoginController;

/**
 * View class for the login screen
 */
public class LoginView {
    private BorderPane mainLayout;
    private LoginController controller;
    
    public LoginView() {
        controller = new LoginController();
        initialize();
    }
    
    private void initialize() {
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        
        // Create header
        Text headerText = new Text("Northeastern University");
        headerText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        Text subHeaderText = new Text("Grade Calculator");
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
        
        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 1);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        grid.add(passwordField, 1, 1);
        
        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        
        Button signupButton = new Button("Sign Up");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(loginButton, signupButton);
        grid.add(buttonBox, 1, 2);
        
        mainLayout.setCenter(grid);
        
        // Set up event handlers
        loginButton.setOnAction(e -> {
            try {
                controller.login(firstNameField.getText(), passwordField.getText());
            } catch (Exception ex) {
                showErrorAlert("Login Error", ex.getMessage());
            }
        });
        
        signupButton.setOnAction(e -> {
            controller.navigateToSignup();
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