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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import application.controllers.LoginController;

/**
 * View class for the login screen with enhanced styling
 */
public class LoginView {
    private BorderPane mainLayout;
    private LoginController controller;
    
    // Define color constants for consistent styling
    private static final Color PRIMARY_COLOR = Color.rgb(0, 59, 111); // Northeastern Blue
    private static final Color SECONDARY_COLOR = Color.rgb(200, 16, 46); // Northeastern Red
    private static final Color ACCENT_COLOR = Color.rgb(0, 173, 86); // Green for grades
    private static final Color LIGHT_GRAY = Color.rgb(240, 240, 240);
    
    // Screen dimensions for responsive design
    private final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    private final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    
    public LoginView() {
        controller = new LoginController();
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
        
        // Create login form with images
        createLoginForm();
        
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
        
        // Create grade icon "A+" text
        Text gradeText = new Text("A+");
        gradeText.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        gradeText.setFill(PRIMARY_COLOR);
        
        logoContainer.getChildren().addAll(logoBackground, gradeText);
        
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
        Text subtitle = new Text("Grade Calculator");
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
    
    private void createLoginForm() {
        // Create a horizontal layout to divide the screen
        HBox contentArea = new HBox(40);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(20, 40, 40, 40));
        
        // Left side - Form panel
        VBox formContainer = new VBox();
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMinWidth(450);
        
        // Create form panel with white background and rounded corners
        StackPane formPanel = new StackPane();
        formPanel.setPadding(new Insets(30));
        
        // Create background rectangle with rounded corners
        Rectangle backgroundRect = new Rectangle();
        backgroundRect.setWidth(450);
        backgroundRect.setHeight(350);
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
        formGrid.setHgap(10);
        formGrid.setAlignment(Pos.CENTER);
        
        // Form Title
        Text formTitle = new Text("Login to your Account");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        formTitle.setFill(PRIMARY_COLOR);
        formGrid.add(formTitle, 0, 0);
        GridPane.setMargin(formTitle, new Insets(0, 0, 15, 0));
        
        // First Name field
        Label firstNameLabel = new Label("First Name:");
        firstNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        firstNameLabel.setTextFill(PRIMARY_COLOR);
        formGrid.add(firstNameLabel, 0, 1);
        
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter your first name");
        firstNameField.setPrefHeight(40);
        firstNameField.setPrefWidth(320);
        styleTextField(firstNameField);
        formGrid.add(firstNameField, 0, 2);
        
        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        passwordLabel.setTextFill(PRIMARY_COLOR);
        formGrid.add(passwordLabel, 0, 3);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(40);
        passwordField.setPrefWidth(320);
        styleTextField(passwordField);
        formGrid.add(passwordField, 0, 4);
        
        // Create horizontal divider
        Rectangle divider = new Rectangle(320, 1);
        divider.setFill(new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.TRANSPARENT),
            new Stop(0.2, Color.rgb(200, 200, 220, 0.5)),
            new Stop(0.8, Color.rgb(200, 200, 220, 0.5)),
            new Stop(1, Color.TRANSPARENT)
        ));
        formGrid.add(divider, 0, 5);
        GridPane.setMargin(divider, new Insets(10, 0, 10, 0));
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button loginButton = createStyledButton("Login", PRIMARY_COLOR);
        loginButton.setPrefWidth(130);
        loginButton.setPrefHeight(40);
        loginButton.setTextFill(Color.WHITE);
        loginButton.setDefaultButton(true);
        
        Button signupButton = createStyledButton("Sign Up", LIGHT_GRAY);
        signupButton.setPrefWidth(130);
        signupButton.setPrefHeight(40);
        signupButton.setTextFill(PRIMARY_COLOR);
        signupButton.setStyle(signupButton.getStyle() + "-fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        buttonBox.getChildren().addAll(loginButton, signupButton);
        formGrid.add(buttonBox, 0, 6);
        GridPane.setMargin(buttonBox, new Insets(15, 0, 0, 0));
        
        // Put it all together
        formPanel.getChildren().addAll(backgroundRect, formGrid);
        formContainer.getChildren().add(formPanel);
        
        // Right side - Images related to grade calculation
        VBox imageContainer = new VBox(25);
        imageContainer.setAlignment(Pos.CENTER);
        
        // Create first image panel - Calculator
        StackPane calculatorImagePane = createImagePanel(
            "/application/resources/calculator.png", 
            "Grade Calculator", 
            "Calculate your GPA and track your academic progress"
        );
        
        // Create second image panel - Grades
        StackPane gradesImagePane = createImagePanel(
            "/application/resources/grades.png", 
            "Track Your Performance", 
            "Visualize your grades across all courses"
        );
        
        // Create third image panel - Reports
        StackPane reportImagePane = createImagePanel(
            "/application/resources/report.png", 
            "Generate Reports", 
            "Create detailed reports of your academic standings"
        );
        
        // Add all image panels to the container
        imageContainer.getChildren().addAll(
            calculatorImagePane, 
            gradesImagePane, 
            reportImagePane
        );
        
        // Add both form and images to the content area
        contentArea.getChildren().addAll(formContainer, imageContainer);
        
        mainLayout.setCenter(contentArea);
        
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
    
    private StackPane createImagePanel(String imagePath, String title, String description) {
        StackPane imagePanel = new StackPane();
        
        // Create background rectangle with rounded corners
        Rectangle imageBg = new Rectangle(400, 120);
        imageBg.setArcWidth(15);
        imageBg.setArcHeight(15);
        imageBg.setFill(Color.WHITE);
        
        // Add shadow
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setOffsetY(3);
        imageBg.setEffect(shadow);
        
        // Layout for image and text
        HBox contentBox = new HBox(20);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.setPadding(new Insets(15, 25, 15, 25));
        contentBox.setMaxWidth(380); // Limit width to ensure text wrapping
        
        // Image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(image);
            imageView.setFitWidth(70);
            imageView.setFitHeight(70);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            // Create a fallback icon if image can't be loaded
            Circle fallbackCircle = new Circle(35);
            fallbackCircle.setFill(new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, ACCENT_COLOR),
                new Stop(1, ACCENT_COLOR.darker())
            ));
            
            Text iconText = new Text(title.substring(0, 1));
            iconText.setFont(Font.font("Arial", FontWeight.BOLD, 26));
            iconText.setFill(Color.WHITE);
            
            StackPane fallbackIcon = new StackPane(fallbackCircle, iconText);
            contentBox.getChildren().add(fallbackIcon);
        }
        
        if (imageView.getImage() != null) {
            contentBox.getChildren().add(imageView);
        }
        
        // Text content
        VBox textBox = new VBox(5);
        textBox.setMaxWidth(280); // Force text wrapping by limiting width
        
        Text titleText = new Text(title);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleText.setFill(PRIMARY_COLOR);
        
        // Use Label instead of Text for automatic wrapping
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        descLabel.setTextFill(Color.rgb(80, 80, 80));
        descLabel.setWrapText(true); // Enable text wrapping
        descLabel.setMaxWidth(280);  // Set maximum width for wrapping
        
        textBox.getChildren().addAll(titleText, descLabel);
        contentBox.getChildren().add(textBox);
        
        imagePanel.getChildren().addAll(imageBg, contentBox);
        return imagePanel;
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
    
    /**
     * Sets the application to full screen mode.
     * This method should be called after the stage is shown.
     * @param stage The primary stage of the application
     */
    public void setFullScreen(javafx.stage.Stage stage) {
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("Press ESC to exit full screen mode");
    }
}