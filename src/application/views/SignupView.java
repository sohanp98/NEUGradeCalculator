package application.views;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.util.Duration;
import application.controllers.SignupController;

/**
 * View class for the signup screen with enhanced styling
 */
public class SignupView {
    private BorderPane mainLayout;
    private SignupController controller;
    
    // Define color constants for consistent styling
    private static final Color PRIMARY_COLOR = Color.rgb(0, 59, 111); // Northeastern Blue
    private static final Color SECONDARY_COLOR = Color.rgb(200, 16, 46); // Northeastern Red
    private static final Color ACCENT_COLOR = Color.rgb(0, 173, 86); // Green for grades
    private static final Color LIGHT_GRAY = Color.rgb(240, 240, 240);
    
    // Screen dimensions for responsive design
    private final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    private final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    
    public SignupView() {
        controller = new SignupController();
        initialize();
    }
    
    private void initialize() {
        mainLayout = new BorderPane();
        
        // Set up gradient background
        LinearGradient gradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(245, 245, 255)),
            new Stop(1, Color.rgb(225, 235, 245))
        );
        
        mainLayout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
        mainLayout.setPadding(new Insets(20));
        
        // Set preferred size to full screen
        mainLayout.setPrefSize(screenWidth, screenHeight);
        
        // Create decorative elements in the background
        createBackgroundElements();
        
        // Create header section
        createHeader();
        
        // Create signup form
        createSignupForm();
        
        // Create footer
        createFooter();
    }
    
    private void createBackgroundElements() {
        // Create decorative circles in background
        StackPane backgroundElements = new StackPane();
        backgroundElements.setMouseTransparent(true); // Allow clicks to pass through
        
        // Top left decorative circle
        Circle circle1 = new Circle(150);
        circle1.setFill(Color.rgb(0, 59, 111, 0.05));
        circle1.setTranslateX(-screenWidth / 4);
        circle1.setTranslateY(-screenHeight / 5);
        
        // Bottom right decorative circle
        Circle circle2 = new Circle(180);
        circle2.setFill(Color.rgb(200, 16, 46, 0.05));
        circle2.setTranslateX(screenWidth / 3);
        circle2.setTranslateY(screenHeight / 4);
        
        // Middle accent circle
        Circle circle3 = new Circle(100);
        circle3.setFill(Color.rgb(0, 173, 86, 0.07));
        circle3.setTranslateX(screenWidth / 5);
        circle3.setTranslateY(-screenHeight / 6);
        
        backgroundElements.getChildren().addAll(circle1, circle2, circle3);
        mainLayout.getChildren().add(backgroundElements);
    }
    
    private void createHeader() {
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(30, 20, 40, 20));
        
        // Create university logo or emblem
        StackPane logoContainer = new StackPane();
        
        // Background circle for logo
        Circle logoBackground = new Circle(50);
        logoBackground.setFill(Color.WHITE);
        
        // Add shadow to logo background
        DropShadow logoShadow = new DropShadow();
        logoShadow.setRadius(10);
        logoShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        logoShadow.setOffsetY(3);
        logoBackground.setEffect(logoShadow);
        
        // Create Northeastern "N" logo
        StackPane nuLogo = new StackPane();
        
        // Main "N" letter
        Text nText = new Text("N");
        nText.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        nText.setFill(PRIMARY_COLOR);
        
        
        nuLogo.getChildren().addAll(nText);
        
        logoContainer.getChildren().addAll(logoBackground, nuLogo);
        
        // Create university name text with reflection
        Text universityName = new Text("Northeastern University");
        universityName.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        universityName.setFill(PRIMARY_COLOR);
        universityName.setTextAlignment(TextAlignment.CENTER);
        
        // Add reflection effect
        Reflection reflection = new Reflection();
        reflection.setFraction(0.2);
        reflection.setTopOpacity(0.5);
        universityName.setEffect(reflection);
        
        // Create subtitle
        Text subtitle = new Text("Create Account");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        subtitle.setFill(SECONDARY_COLOR);
        subtitle.setTextAlignment(TextAlignment.CENTER);
        
        // Create decorative colored bar
        Rectangle colorBar = new Rectangle(350, 5);
        colorBar.setArcWidth(5);
        colorBar.setArcHeight(5);
        
        // Create gradient for color bar
        LinearGradient barGradient = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, PRIMARY_COLOR),
            new Stop(0.5, SECONDARY_COLOR),
            new Stop(1, ACCENT_COLOR)
        );
        colorBar.setFill(barGradient);
        
        // Add all elements to header
        headerBox.getChildren().addAll(logoContainer, universityName, subtitle, colorBar);
        
        // Add animation to the header
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), headerBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        
        mainLayout.setTop(headerBox);
    }
    
    private void createSignupForm() {
        // Create container for the form
        VBox formContainer = new VBox();
        formContainer.setAlignment(Pos.CENTER);
        
        // Create form panel with white background and rounded corners
        StackPane formPanel = new StackPane();
        formPanel.setPadding(new Insets(30));
        
        // Create background rectangle with rounded corners
        Rectangle backgroundRect = new Rectangle();
        backgroundRect.setWidth(500);
        backgroundRect.setHeight(420);
        backgroundRect.setArcWidth(20);
        backgroundRect.setArcHeight(20);
        
        // Create white to light gray gradient
        LinearGradient panelGradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.WHITE),
            new Stop(1, Color.rgb(245, 245, 250))
        );
        backgroundRect.setFill(panelGradient);
        
        // Add shadow to form panel
        DropShadow formShadow = new DropShadow();
        formShadow.setRadius(15);
        formShadow.setColor(Color.rgb(0, 0, 0, 0.25));
        formShadow.setOffsetY(5);
        formShadow.setOffsetX(0);
        backgroundRect.setEffect(formShadow);
        
        // Create form contents
        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(20);
        formGrid.setAlignment(Pos.CENTER);
        
        // Form Title
        Text formTitle = new Text("Register New Account");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        formTitle.setFill(PRIMARY_COLOR);
        formGrid.add(formTitle, 0, 0, 2, 1);
        GridPane.setMargin(formTitle, new Insets(0, 0, 15, 0));
        
        // First Name field
        Label firstNameLabel = new Label("First Name:");
        firstNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        firstNameLabel.setTextFill(PRIMARY_COLOR);
        formGrid.add(firstNameLabel, 0, 1);
        
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter your first name");
        firstNameField.setPrefHeight(40);
        firstNameField.setPrefWidth(250);
        styleTextField(firstNameField);
        formGrid.add(firstNameField, 1, 1);
        
        // Last Name field
        Label lastNameLabel = new Label("Last Name:");
        lastNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lastNameLabel.setTextFill(PRIMARY_COLOR);
        formGrid.add(lastNameLabel, 0, 2);
        
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter your last name");
        lastNameField.setPrefHeight(40);
        lastNameField.setPrefWidth(250);
        styleTextField(lastNameField);
        formGrid.add(lastNameField, 1, 2);
        
        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        passwordLabel.setTextFill(PRIMARY_COLOR);
        formGrid.add(passwordLabel, 0, 3);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        passwordField.setPrefHeight(40);
        passwordField.setPrefWidth(250);
        styleTextField(passwordField);
        formGrid.add(passwordField, 1, 3);
        
        // Confirm Password field
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        confirmPasswordLabel.setTextFill(PRIMARY_COLOR);
        formGrid.add(confirmPasswordLabel, 0, 4);
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        confirmPasswordField.setPrefHeight(40);
        confirmPasswordField.setPrefWidth(250);
        styleTextField(confirmPasswordField);
        formGrid.add(confirmPasswordField, 1, 4);
        
        // Create horizontal divider
        Rectangle divider = new Rectangle(400, 1);
        divider.setFill(new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.TRANSPARENT),
            new Stop(0.2, Color.rgb(200, 200, 220, 0.5)),
            new Stop(0.8, Color.rgb(200, 200, 220, 0.5)),
            new Stop(1, Color.TRANSPARENT)
        ));
        formGrid.add(divider, 0, 5, 2, 1);
        GridPane.setMargin(divider, new Insets(10, 0, 10, 0));
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button backButton = createStyledButton("Back to Login", LIGHT_GRAY);
        backButton.setPrefWidth(150);
        backButton.setPrefHeight(40);
        backButton.setTextFill(PRIMARY_COLOR);
        backButton.setStyle(backButton.getStyle() + "-fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        Button registerButton = createStyledButton("Register", ACCENT_COLOR);
        registerButton.setPrefWidth(150);
        registerButton.setPrefHeight(40);
        registerButton.setTextFill(Color.WHITE);
        registerButton.setDefaultButton(true);
        
        buttonBox.getChildren().addAll(backButton, registerButton);
        formGrid.add(buttonBox, 1, 6);
        GridPane.setMargin(buttonBox, new Insets(15, 0, 0, 0));
        
        // Put it all together
        formPanel.getChildren().addAll(backgroundRect, formGrid);
        formContainer.getChildren().add(formPanel);
        
        mainLayout.setCenter(formContainer);
        
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
    
    private void createFooter() {
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER_RIGHT);
        footerBox.setPadding(new Insets(15, 30, 15, 30));
        
        Label versionLabel = new Label("Grade Calculator v1.0");
        versionLabel.setFont(Font.font("Arial", 12));
        versionLabel.setTextFill(Color.rgb(100, 100, 100));
        
        footerBox.getChildren().add(versionLabel);
        mainLayout.setBottom(footerBox);
    }
    
    private void styleTextField(TextField textField) {
        textField.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 5; " +
            "-fx-border-color: #cccccc; " +
            "-fx-border-radius: 5; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 8px 12px; " +
            "-fx-font-size: 14px;"
        );
        
        // Add subtle inner shadow for depth
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(2);
        innerShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        innerShadow.setOffsetX(1);
        innerShadow.setOffsetY(1);
        textField.setEffect(innerShadow);
        
        // Change style on focus
        textField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                textField.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 5; " +
                    "-fx-border-color: " + toRgbString(ACCENT_COLOR) + "; " +
                    "-fx-border-radius: 5; " +
                    "-fx-border-width: 2px; " +
                    "-fx-padding: 7px 11px; " + 
                    "-fx-font-size: 14px;"
                );
            } else {
                textField.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 5; " +
                    "-fx-border-color: #cccccc; " +
                    "-fx-border-radius: 5; " +
                    "-fx-border-width: 1px; " +
                    "-fx-padding: 8px 12px; " +
                    "-fx-font-size: 14px;"
                );
            }
        });
    }
    
    private Button createStyledButton(String text, Color bgColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Convert color to CSS format
        String colorString = toRgbString(bgColor);
        
        // Style the button
        button.setStyle(
            "-fx-background-color: " + colorString + "; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        // Add hover effect
        Color hoverColor = bgColor.darker();
        String hoverColorString = toRgbString(hoverColor);
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: " + hoverColorString + "; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + colorString + "; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand;"
            )
        );
        
        return button;
    }
    
    private String toRgbString(Color color) {
        return String.format(
            "rgb(%d, %d, %d)",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255)
        );
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert dialog
        alert.getDialogPane().setStyle(
            "-fx-background-color: #f8f8f8; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1px; " +
            "-fx-font-size: 14px;"
        );
        
        alert.showAndWait();
    }
    
    public BorderPane getView() {
        return mainLayout;
    }
    
   
    
}