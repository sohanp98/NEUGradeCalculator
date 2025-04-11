package application.views;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.application.Platform;
import javafx.stage.Screen;

import application.controllers.HomeController;
import application.models.Semester;
import application.models.User;
import application.services.UserService;
import javafx.util.Duration;

/**
 * Enhanced view class for the home screen with modern styling
 */
public class HomeView {
    private BorderPane mainLayout;
    private HomeController controller;
    private User currentUser;
    private FlowPane semestersPane;
    private Label overallGpaLabel;
    private TextField goalGpaField;
    private Label requiredGpaLabel;
    
    // Define color constants for consistent styling
    private static final Color PRIMARY_COLOR = Color.rgb(0, 59, 111); // Northeastern Blue
    private static final Color SECONDARY_COLOR = Color.rgb(200, 16, 46); // Northeastern Red
    private static final Color ACCENT_COLOR = Color.rgb(0, 173, 86); // Green for grades
    private static final Color LIGHT_GRAY = Color.rgb(240, 240, 240);
    private static final Color BACKGROUND_COLOR = Color.rgb(245, 245, 255);
    private static final Color CARD_COLOR = Color.rgb(252, 252, 255);
    
    // Screen dimensions for responsive design
    private final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    private final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    
    /**
     * Constructor with forced UI update
     */
    public HomeView() {
        System.out.println("Creating new HomeView");
        controller = new HomeController();
        currentUser = UserService.getCurrentUser();
        initialize();
        
        // Force UI update after a short delay to ensure all components are rendered
        Platform.runLater(() -> {
            try {
                // Wait for 100ms to ensure JavaFX has completed initial rendering
                Thread.sleep(100);
                forceUpdateUI();
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting to update UI: " + e.getMessage());
            }
        });
    }

    /**
     * Initialize the view with enhanced styling
     */
    private void initialize() {
        try {
            System.out.println("Initializing HomeView");
            mainLayout = new BorderPane();
            
            // Set up gradient background
            LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, BACKGROUND_COLOR),
                new Stop(1, Color.rgb(235, 240, 250))
            );
            
            mainLayout.setStyle("-fx-background-color: #f5f5ff;");
            mainLayout.setPadding(new Insets(20));
            
            // Enhanced size settings to ensure full-screen display
            mainLayout.setPrefSize(screenWidth, screenHeight);
            mainLayout.setMinSize(screenWidth, screenHeight);
            mainLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            
            // Force the BorderPane to use all available space
            VBox.setVgrow(mainLayout, Priority.ALWAYS);
            HBox.setHgrow(mainLayout, Priority.ALWAYS);
            
            // Create background decorative elements
            createBackgroundElements();
            
            // Create enhanced header
            createEnhancedHeader();
            
            // Create styled content area
            createStyledContent();
            
            // Create footer
            createFooter();
            
            // Force an aggressive GPA update immediately
            forceDisplayGPAs();
            
            // Register for data change events
            application.utils.EventBus.getInstance().register(
                application.utils.DataChangedEvent.class,
                event -> {
                    System.out.println("DataChangedEvent received in HomeView - refreshing view");
                    Platform.runLater(this::forceDisplayGPAs);
                }
            );
            
            System.out.println("HomeView initialization complete");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing HomeView: " + e.getMessage());
        }
    }
    
    /**
     * Creates decorative background elements
     */
    private void createBackgroundElements() {
        // Create decorative circles in background
        StackPane backgroundElements = new StackPane();
        backgroundElements.setMouseTransparent(true); // Allow clicks to pass through
        
        // Top left decorative circle
        Circle circle1 = new Circle(180);
        circle1.setFill(Color.rgb(0, 59, 111, 0.04));
        circle1.setTranslateX(-400);
        circle1.setTranslateY(-200);
        
        // Bottom right decorative circle
        Circle circle2 = new Circle(200);
        circle2.setFill(Color.rgb(200, 16, 46, 0.04));
        circle2.setTranslateX(400);
        circle2.setTranslateY(300);
        
        // Middle accent circle
        Circle circle3 = new Circle(120);
        circle3.setFill(Color.rgb(0, 173, 86, 0.05));
        circle3.setTranslateX(200);
        circle3.setTranslateY(-150);
        
        backgroundElements.getChildren().addAll(circle1, circle2, circle3);
        mainLayout.getChildren().add(backgroundElements);
    }
    
    /**
     * Creates an enhanced header with logo and styling
     */
    private void createEnhancedHeader() {
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10, 20, 25, 20));
        topBox.setSpacing(20);
        topBox.setPrefWidth(screenWidth - 40); // Set preferred width to match screen
        
        // Left side - Logo and title
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        // Create logo circle
        StackPane logoContainer = new StackPane();
        
        Circle logoCircle = new Circle(30);
        logoCircle.setFill(new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, PRIMARY_COLOR),
            new Stop(1, PRIMARY_COLOR.darker())
        ));
        
        // Add drop shadow to logo
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetY(3);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        logoCircle.setEffect(dropShadow);
        
        // "A+" text for logo
        Text gradeText = new Text("A+");
        gradeText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gradeText.setFill(Color.WHITE);
        
        logoContainer.getChildren().addAll(logoCircle, gradeText);
        
        // Create title text
        VBox titleBox = new VBox(5);
        
        Text universityText = new Text("Northeastern University");
        universityText.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        universityText.setFill(PRIMARY_COLOR);
        
        Text calculatorText = new Text("Grade Calculator");
        calculatorText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        calculatorText.setFill(SECONDARY_COLOR);
        
        titleBox.getChildren().addAll(universityText, calculatorText);
        
        headerBox.getChildren().addAll(logoContainer, titleBox);
        
        // Right side - Welcome message and logout button
        HBox userBox = new HBox(15);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        
        // Welcome message
        VBox welcomeBox = new VBox(5);
        welcomeBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label welcomeLabel = new Label("Welcome,");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        welcomeLabel.setTextFill(Color.rgb(100, 100, 100));
        
        Label nameLabel = new Label(currentUser.getFullName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setTextFill(PRIMARY_COLOR);
        
        welcomeBox.getChildren().addAll(welcomeLabel, nameLabel);
        
        // Create logout button
        Button logoutButton = createStyledButton("Logout", LIGHT_GRAY);
        logoutButton.setTextFill(SECONDARY_COLOR);
        logoutButton.setPrefWidth(100);
        logoutButton.setStyle("-fx-background-color: white; -fx-border-color: " + toRgbString(SECONDARY_COLOR) + "; -fx-border-radius: 5; -fx-background-radius: 5;");
        logoutButton.setOnAction(e -> controller.logout());
        
        userBox.getChildren().addAll(welcomeBox, logoutButton);
        
        // Create spacer to push welcome and logout to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Add all elements to top box
        topBox.getChildren().addAll(headerBox, spacer, userBox);
        
        // Add colorful divider line
        Rectangle colorBar = new Rectangle();
        colorBar.setHeight(4);
        colorBar.setArcWidth(4);
        colorBar.setArcHeight(4);
        colorBar.widthProperty().bind(mainLayout.widthProperty().subtract(40)); // Bind to layout width
        
        // Gradient for color bar
        LinearGradient barGradient = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, PRIMARY_COLOR),
            new Stop(0.5, SECONDARY_COLOR),
            new Stop(1, ACCENT_COLOR)
        );
        colorBar.setFill(barGradient);
        
        // Create glow effect
        Glow glow = new Glow();
        glow.setLevel(0.3);
        colorBar.setEffect(glow);
        
        // Container for top elements
        VBox headerContainer = new VBox(15);
        headerContainer.getChildren().addAll(topBox, colorBar);
        headerContainer.setPrefWidth(screenWidth - 40); // Set preferred width to match screen
        
        // Animate the header with fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), headerContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        
        mainLayout.setTop(headerContainer);
    }
    
    /**
     * Creates the content area with GPA summary and semester cards
     */
    private void createStyledContent() {
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30, 20, 20, 20));
        contentBox.setPrefWidth(screenWidth - 40); // Set preferred width to match screen
        
        // Create GPA Summary Panel
        StackPane summaryPanel = createGpaSummaryPanel();
        
        // Create Semesters Panel
        VBox semestersSection = createSemestersSection();
        
        // Add all to content
        contentBox.getChildren().addAll(summaryPanel, semestersSection);
        
        // Animate content with fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), contentBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setDelay(Duration.millis(300));
        fadeIn.play();
        
        mainLayout.setCenter(contentBox);
    }
    
    /**
     * Creates the GPA summary panel with current, goal, and required GPA
     */
    private StackPane createGpaSummaryPanel() {
        StackPane summaryPanel = new StackPane();
        
        // Create background panel
        Rectangle panelBg = new Rectangle();
        panelBg.setWidth(Math.min(900, screenWidth - 60)); // Responsive width
        panelBg.setHeight(180);
        panelBg.setArcWidth(20);
        panelBg.setArcHeight(20);
        
        // Create panel gradient
        LinearGradient panelGradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.WHITE),
            new Stop(1, Color.rgb(250, 250, 255))
        );
        panelBg.setFill(panelGradient);
        
        // Add shadow to panel
        DropShadow panelShadow = new DropShadow();
        panelShadow.setRadius(15);
        panelShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        panelShadow.setOffsetY(5);
        panelBg.setEffect(panelShadow);
        
        // Create GPA Summary content
        HBox summaryContent = new HBox(40);
        summaryContent.setAlignment(Pos.CENTER);
        summaryContent.setPadding(new Insets(25, 30, 25, 30));
        
        // Current GPA Box
        VBox currentGpaBox = createGpaDisplayBox(
            "Current Overall GPA", 
            overallGpaLabel = createGpaValueLabel("0.00"), 
            PRIMARY_COLOR
        );
        styleGpaBox(currentGpaBox);
        
        // Goal GPA Box
        VBox goalGpaBox = new VBox(8);
        goalGpaBox.setAlignment(Pos.CENTER);
        goalGpaBox.setPadding(new Insets(15, 25, 15, 25));
        goalGpaBox.setMinWidth(180);
        
        Label goalHeaderLabel = new Label("GPA Goal");
        goalHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        goalHeaderLabel.setTextFill(Color.rgb(80, 80, 80));
        
        goalGpaField = new TextField("4.0");
        goalGpaField.setPrefWidth(100);
        goalGpaField.setMaxWidth(100);
        goalGpaField.setAlignment(Pos.CENTER);
        goalGpaField.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        styleTextField(goalGpaField);
        
        goalGpaBox.getChildren().addAll(goalHeaderLabel, goalGpaField);
        styleGpaBox(goalGpaBox);
        
        // Calculate Button
        Button calculateButton = createStyledButton("Calculate", ACCENT_COLOR);
        calculateButton.setTextFill(Color.WHITE);
        calculateButton.setPrefWidth(120);
        calculateButton.setPrefHeight(40);
        calculateButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        calculateButton.setOnAction(e -> calculateRequiredGPA());
        
        VBox calculateBox = new VBox();
        calculateBox.setAlignment(Pos.CENTER);
        calculateBox.getChildren().add(calculateButton);
        
        // Required GPA Box
        VBox requiredGpaBox = createGpaDisplayBox(
            "Required GPA for Future Semesters", 
            requiredGpaLabel = createGpaValueLabel("0.00"), 
            SECONDARY_COLOR
        );
        styleGpaBox(requiredGpaBox);
        
        // Add all to summary content
        summaryContent.getChildren().addAll(currentGpaBox, goalGpaBox, calculateBox, requiredGpaBox);
        
        summaryPanel.getChildren().addAll(panelBg, summaryContent);
        
        return summaryPanel;
    }
    
    /**
     * Creates the semesters section with title and flowpane for semester cards
     */
    private VBox createSemestersSection() {
        VBox semestersSection = new VBox(20);
        semestersSection.setAlignment(Pos.CENTER);
        semestersSection.setPrefWidth(screenWidth - 60); // Responsive width
        
        // Create section title
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(10, 0, 5, 10));
        titleBox.setPrefWidth(Math.min(900, screenWidth - 60)); // Responsive width
        
        Text semestersTitle = new Text("Your Semesters");
        semestersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        semestersTitle.setFill(PRIMARY_COLOR);
        
        titleBox.getChildren().add(semestersTitle);
        
        // Create FlowPane for semester cards
        semestersPane = new FlowPane();
        semestersPane.setHgap(30);
        semestersPane.setVgap(30);
        semestersPane.setAlignment(Pos.CENTER);
        semestersPane.setPrefWidth(Math.min(900, screenWidth - 60)); // Responsive width
        semestersPane.setPadding(new Insets(20, 0, 20, 0));
        
        // Create 'Add Semester' button
        Button addSemesterButton = createStyledButton("Add Semester", PRIMARY_COLOR);
        addSemesterButton.setTextFill(Color.WHITE);
        addSemesterButton.setPrefWidth(180);
        addSemesterButton.setPrefHeight(45);
        addSemesterButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Add vertical glow effect to button
        DropShadow buttonShadow = new DropShadow();
        buttonShadow.setRadius(10);
        buttonShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        buttonShadow.setOffsetY(3);
        addSemesterButton.setEffect(buttonShadow);
        
        addSemesterButton.setOnAction(e -> showAddSemesterDialog());
        
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 15, 0));
        buttonBox.getChildren().add(addSemesterButton);
        
        // Add all to section
        semestersSection.getChildren().addAll(titleBox, semestersPane, buttonBox);
        
        return semestersSection;
    }
    
    /**
     * Creates a footer with version information
     */
    private void createFooter() {
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER_RIGHT);
        footerBox.setPadding(new Insets(15, 30, 15, 30));
        footerBox.setPrefWidth(screenWidth - 40); // Set preferred width to match screen
        
        Label versionLabel = new Label("Northeastern Grade Calculator v1.0");
        versionLabel.setFont(Font.font("Arial", 12));
        versionLabel.setTextFill(Color.rgb(150, 150, 150));
        
        footerBox.getChildren().add(versionLabel);
        mainLayout.setBottom(footerBox);
    }
    
    /**
     * Creates a display box for GPAs with header and value
     */
    private VBox createGpaDisplayBox(String header, Label valueLabel, Color accentColor) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15, 25, 15, 25));
        box.setMinWidth(220);
        
        Label headerLabel = new Label(header);
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        headerLabel.setTextFill(Color.rgb(80, 80, 80));
        
        valueLabel.setTextFill(accentColor);
        
        box.getChildren().addAll(headerLabel, valueLabel);
        
        return box;
    }
    
    /**
     * Creates a styled GPA value label
     */
    private Label createGpaValueLabel(String initialValue) {
        Label label = new Label(initialValue);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 34));
        
        // Add reflection effect
        Reflection reflection = new Reflection();
        reflection.setFraction(0.2);
        reflection.setTopOpacity(0.5);
        label.setEffect(reflection);
        
        return label;
    }
    
    /**
     * Applies styling to a GPA display box
     */
    private void styleGpaBox(VBox box) {
        box.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.6); " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: rgba(200, 200, 220, 0.5); " +
            "-fx-border-radius: 10; " +
            "-fx-border-width: 1px;"
        );
        
        // Add subtle shadow
        DropShadow shadow = new DropShadow();
        shadow.setRadius(8);
        shadow.setOffsetY(2);
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        box.setEffect(shadow);
    }
    
    /**
     * Styles a text field with modern appearance
     */
    private void styleTextField(TextField textField) {
        textField.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 5; " +
            "-fx-border-color: #cccccc; " +
            "-fx-border-radius: 5; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 8px 12px; " +
            "-fx-font-size: 16px;"
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
                    "-fx-font-size: 16px;"
                );
            } else {
                textField.setStyle(
                    "-fx-background-color: white; " +
                    "-fx-background-radius: 5; " +
                    "-fx-border-color: #cccccc; " +
                    "-fx-border-radius: 5; " +
                    "-fx-border-width: 1px; " +
                    "-fx-padding: 8px 12px; " +
                    "-fx-font-size: 16px;"
                );
            }
        });
    }
    
    /**
     * Creates a styled button with the specified text and background color
     */
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
    
    /**
     * Converts a JavaFX Color to an RGB string for CSS
     */
    private String toRgbString(Color color) {
        return String.format(
            "rgb(%d, %d, %d)",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255)
        );
    }
    
    /**
     * Load and display all semesters for the current user with fresh data from the database
     */
    private void loadSemesters() {
        try {
            System.out.println("\n===== LOADING SEMESTERS WITH FRESH DATA =====");
            
            // Get fresh data from the database
            List<Semester> semesters = controller.getAllSemesters();
            semestersPane.getChildren().clear();
            
            System.out.println("Found " + semesters.size() + " semesters");
            
            if (semesters.isEmpty()) {
                // Create a styled "no semesters" message
                StackPane emptyPane = new StackPane();
                emptyPane.setPrefWidth(Math.min(800, screenWidth - 100)); // Responsive width
                emptyPane.setPrefHeight(200);
                
                VBox emptyBox = new VBox(15);
                emptyBox.setAlignment(Pos.CENTER);
                
                Text emptyText = new Text("You don't have any semesters yet");
                emptyText.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
                emptyText.setFill(Color.rgb(120, 120, 140));
                
                Text addText = new Text("Click 'Add Semester' to get started");
                addText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                addText.setFill(Color.rgb(150, 150, 170));
                
                emptyBox.getChildren().addAll(emptyText, addText);
                emptyPane.getChildren().add(emptyBox);
                
                semestersPane.getChildren().add(emptyPane);
            } else {
                // Calculate overall GPA here
                double totalGPA = 0.0;
                int validSemesterCount = 0;
                
                // Add each semester card to the UI
                for (Semester semester : semesters) {
                    // Force GPA calculation for each semester
                    double semesterGPA = semester.calculateGPA();
                    
                    if (semesterGPA > 0) {
                        totalGPA += semesterGPA;
                        validSemesterCount++;
                    }
                    
                    // Create card with calculated GPA
                    StackPane semesterCard = createEnhancedSemesterCard(semester, semesterGPA);
                    semestersPane.getChildren().add(semesterCard);
                }
                
                // Calculate and set overall GPA
                double overallGPA = validSemesterCount > 0 ? totalGPA / validSemesterCount : 0.0;
                overallGpaLabel.setText(String.format("%.2f", overallGPA));
                System.out.println("Overall GPA set to: " + overallGPA);
            }
            
            System.out.println("===== SEMESTERS LOADED SUCCESSFULLY =====\n");
        } catch (Exception e) {
            System.err.println("Error loading semesters: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load semesters: " + e.getMessage());
        }
    }
    
    /**
     * Creates an enhanced semester card with visual styling
     */
    private StackPane createEnhancedSemesterCard(Semester semester, double forcedGPA) {
        System.out.println("Creating enhanced semester card with GPA: " + forcedGPA);
        
        StackPane cardContainer = new StackPane();
        
        // Create card background with rounded corners
        Rectangle cardBg = new Rectangle(280, 180);
        cardBg.setArcWidth(15);
        cardBg.setArcHeight(15);
        
        // Create card gradient
        LinearGradient cardGradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, CARD_COLOR),
            new Stop(1, Color.rgb(245, 245, 250))
        );
        cardBg.setFill(cardGradient);
        
        // Add card shadow
        DropShadow cardShadow = new DropShadow();
        cardShadow.setRadius(10);
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        cardShadow.setOffsetY(5);
        cardBg.setEffect(cardShadow);
        
        // Create card content
        VBox cardContent = new VBox(12);
        cardContent.setPadding(new Insets(20, 20, 20, 20));
        cardContent.setPrefWidth(280);
        cardContent.setPrefHeight(180);
        
        // Header with semester name and delete button
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Text nameText = new Text(semester.getName());
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameText.setFill(PRIMARY_COLOR);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button deleteButton = new Button("×");
        deleteButton.setStyle(
            "-fx-background-color: " + toRgbString(SECONDARY_COLOR) + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-min-width: 24px; " +
            "-fx-min-height: 24px; " +
            "-fx-max-width: 24px; " +
            "-fx-max-height: 24px; " +
            "-fx-padding: 0; " +
            "-fx-background-radius: 12;"
        );
        
        // Add hover effect to delete button
        deleteButton.setOnMouseEntered(e -> 
            deleteButton.setStyle(
                "-fx-background-color: " + toRgbString(SECONDARY_COLOR.darker()) + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-min-width: 24px; " +
                "-fx-min-height: 24px; " +
                "-fx-max-width: 24px; " +
                "-fx-max-height: 24px; " +
                "-fx-padding: 0; " +
                "-fx-background-radius: 12;"
            )
        );
        
        deleteButton.setOnMouseExited(e -> 
            deleteButton.setStyle(
                "-fx-background-color: " + toRgbString(SECONDARY_COLOR) + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-min-width: 24px; " +
                "-fx-min-height: 24px; " +
                "-fx-max-width: 24px; " +
                "-fx-max-height: 24px; " +
                "-fx-padding: 0; " +
                "-fx-background-radius: 12;"
            )
        );
        
        headerBox.getChildren().addAll(nameText, spacer, deleteButton);
        
        // GPA display with circle indicator
        HBox gpaBox = new HBox(15);
        gpaBox.setAlignment(Pos.CENTER_LEFT);
        gpaBox.setPadding(new Insets(10, 0, 10, 0));
        
        // Create GPA circle
        StackPane gpaCircle = new StackPane();
        
        Circle circle = new Circle(28);
        
        // Color the circle based on GPA value
        Color circleColor;
        if (forcedGPA >= 3.7) {
            circleColor = ACCENT_COLOR; // Green for A/A+
        } else if (forcedGPA >= 3.0) {
            circleColor = Color.rgb(66, 133, 244); // Blue for B
        } else if (forcedGPA >= 2.0) {
            circleColor = Color.rgb(251, 188, 5); // Yellow for C
        } else {
            circleColor = SECONDARY_COLOR; // Red for D/F
        }
        
        circle.setFill(circleColor);
        
        // Add inner glow effect
        DropShadow innerGlow = new DropShadow();
        innerGlow.setRadius(10);
        innerGlow.setColor(circleColor.deriveColor(0, 1.2, 1, 0.3));
        innerGlow.setOffsetX(0);
        innerGlow.setOffsetY(0);
        circle.setEffect(innerGlow);
        
        // GPA text inside circle
        Text gpaText = new Text(String.format("%.1f", forcedGPA));
        gpaText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gpaText.setFill(Color.WHITE);
        
        gpaCircle.getChildren().addAll(circle, gpaText);
        
        // GPA information
        VBox gpaInfoBox = new VBox(2);
        
        Label gpaLabel = new Label("GPA");
        gpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gpaLabel.setTextFill(Color.rgb(100, 100, 100));
        
        Label gpaValueLabel = new Label(String.format("%.2f", forcedGPA));
        gpaValueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gpaValueLabel.setTextFill(circleColor);
        
        gpaInfoBox.getChildren().addAll(gpaLabel, gpaValueLabel);
        
        // Add divider for subjects
        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: #e0e0e0;");
        
        gpaBox.getChildren().addAll(gpaCircle, gpaInfoBox);
        
        // Subject count
        Label subjectsLabel = new Label(String.format("Subjects: %d", semester.getSubjects().size()));
        subjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subjectsLabel.setTextFill(Color.rgb(100, 100, 100));
        
        // Button to view details
        Button viewButton = createStyledButton("View Details", PRIMARY_COLOR);
        viewButton.setTextFill(Color.WHITE);
        viewButton.setPrefWidth(240);
        viewButton.setOnAction(e -> controller.navigateToSemesterDetails(semester));
        
        // Spacer to push the button to the bottom
        Region verticalSpacer = new Region();
        VBox.setVgrow(verticalSpacer, Priority.ALWAYS);
        
        cardContent.getChildren().addAll(headerBox, gpaBox, divider, subjectsLabel, verticalSpacer, viewButton);
        
        // Set up delete button event handler
        deleteButton.setOnAction(e -> showDeleteSemesterConfirmation(semester));
        
        cardContainer.getChildren().addAll(cardBg, cardContent);
        
        return cardContainer;
    }
    
    /**
     * Show a dialog to add a new semester
     */
    private void showAddSemesterDialog() {
        // Create dialog
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Semester");
        dialog.setMinWidth(400);
        
        VBox dialogVBox = new VBox(20);
        dialogVBox.setPadding(new Insets(30));
        dialogVBox.setStyle("-fx-background-color: #f8f8ff;");
        
        // Title with style
        Text titleText = new Text("Add New Semester");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleText.setFill(PRIMARY_COLOR);
        
        // Add decorative underline
        Rectangle underline = new Rectangle(100, 3);
        underline.setFill(ACCENT_COLOR);
        underline.setArcWidth(3);
        underline.setArcHeight(3);
        
        Label nameLabel = new Label("Semester Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setTextFill(PRIMARY_COLOR);
        
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Fall 2024");
        nameField.setPrefHeight(40);
        styleTextField(nameField);
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button cancelButton = createStyledButton("Cancel", LIGHT_GRAY);
        cancelButton.setTextFill(Color.rgb(80, 80, 80));
        cancelButton.setPrefWidth(100);
        cancelButton.setPrefHeight(40);
        
        Button addButton = createStyledButton("Add", ACCENT_COLOR);
        addButton.setTextFill(Color.WHITE);
        addButton.setPrefWidth(100);
        addButton.setPrefHeight(40);
        addButton.setDefaultButton(true);
        
        buttonBox.getChildren().addAll(cancelButton, addButton);
        
        dialogVBox.getChildren().addAll(titleText, underline, nameLabel, nameField, buttonBox);
        
        dialog.setScene(new javafx.scene.Scene(dialogVBox));
        
        // Set up event handlers
        cancelButton.setOnAction(e -> dialog.close());
        
        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Semester name cannot be empty");
                }
                
                controller.createSemester(name);
                dialog.close();
                loadSemesters();
            } catch (Exception ex) {
                showErrorAlert("Error", ex.getMessage());
            }
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Show a confirmation dialog before deleting a semester
     * 
     * @param semester The semester to delete
     */
    private void showDeleteSemesterConfirmation(Semester semester) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Semester");
        alert.setHeaderText("Are you sure you want to delete " + semester.getName() + "?");
        alert.setContentText("This action cannot be undone. All subjects, grades, and assessments for this semester will be permanently deleted.");
        
        // Style the alert dialog
        alert.getDialogPane().setStyle(
            "-fx-background-color: #f8f8ff; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1px; " +
            "-fx-font-size: 14px;"
        );
        
        // Customize the buttons
        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(deleteButton, cancelButton);
        
        // Make the delete button red
        Button deleteButtonObj = (Button) alert.getDialogPane().lookupButton(deleteButton);
        deleteButtonObj.setStyle("-fx-background-color: " + toRgbString(SECONDARY_COLOR) + "; -fx-text-fill: white;");
        
        // Show the dialog and handle the result
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == deleteButton) {
                try {
                    // Delete the semester
                    controller.deleteSemester(semester.getId());
                    
                    // Refresh the view
                    loadSemesters();
                    
                    // Show success message
                    showInfoAlert("Semester Deleted", "The semester was successfully deleted.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showErrorAlert("Error", "Failed to delete semester: " + ex.getMessage());
                }
            }
        });
    }
    
    /**
     * Calculate and display the required GPA based on the goal
     */
    private void calculateRequiredGPA() {
        try {
            double goalGPA = Double.parseDouble(goalGpaField.getText());
            
            if (goalGPA < 0.0 || goalGPA > 4.0) {
                throw new IllegalArgumentException("GPA must be between 0.0 and 4.0");
            }
            
            double requiredGPA = controller.calculateRequiredGPA(goalGPA);
            requiredGpaLabel.setText(String.format("%.2f", requiredGPA));
            
            // Color code based on difficulty
            if (requiredGPA > 3.7) {
                requiredGpaLabel.setTextFill(SECONDARY_COLOR); // Red for difficult
            } else if (requiredGPA > 3.0) {
                requiredGpaLabel.setTextFill(Color.rgb(251, 188, 5)); // Yellow for moderate
            } else {
                requiredGpaLabel.setTextFill(ACCENT_COLOR); // Green for achievable
            }
            
            // Add animation
            FadeTransition fadeInOut = new FadeTransition(Duration.millis(300), requiredGpaLabel);
            fadeInOut.setFromValue(0.5);
            fadeInOut.setToValue(1.0);
            fadeInOut.setCycleCount(2);
            fadeInOut.setAutoReverse(true);
            fadeInOut.play();
            
        } catch (NumberFormatException e) {
            showErrorAlert("Error", "Please enter a valid number for GPA goal");
        } catch (Exception e) {
            showErrorAlert("Error", e.getMessage());
        }
    }
    
    /**
     * Force update of all UI elements with fresh data
     * This is a more direct approach when event-based updates aren't working
     */
    public void forceUpdateUI() {
        try {
            System.out.println("\n===== FORCE UPDATING HOME VIEW UI =====");
            
            // Clear the semesters pane
            semestersPane.getChildren().clear();
            
            // Get fresh data from the database
            List<Semester> semesters = controller.getAllSemesters();
            
            if (semesters.isEmpty()) {
                // Create a styled "no semesters" message
                StackPane emptyPane = new StackPane();
                emptyPane.setPrefWidth(Math.min(800, screenWidth - 100)); // Responsive width
                emptyPane.setPrefHeight(200);
                
                VBox emptyBox = new VBox(15);
                emptyBox.setAlignment(Pos.CENTER);
                
                Text emptyText = new Text("You don't have any semesters yet");
                emptyText.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
                emptyText.setFill(Color.rgb(120, 120, 140));
                
                Text addText = new Text("Click 'Add Semester' to get started");
                addText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                addText.setFill(Color.rgb(150, 150, 170));
                
                emptyBox.getChildren().addAll(emptyText, addText);
                emptyPane.getChildren().add(emptyBox);
                
                semestersPane.getChildren().add(emptyPane);
            } else {
                // Calculate overall GPA directly here
                double totalGPA = 0.0;
                int validSemesterCount = 0;
                
                for (Semester semester : semesters) {
                    // Force GPA calculation for each semester
                    double semesterGPA = semester.calculateGPA();
                    System.out.println("Semester: " + semester.getName() + ", GPA: " + semesterGPA);
                    
                    // Only count valid GPAs
                    if (semesterGPA > 0) {
                        totalGPA += semesterGPA;
                        validSemesterCount++;
                    }
                    
                    // Add semester card with enhanced visuals
                    StackPane semesterCard = createEnhancedSemesterCard(semester, semesterGPA);
                    semestersPane.getChildren().add(semesterCard);
                }
                
                // Update overall GPA label
                double overallGPA = validSemesterCount > 0 ? totalGPA / validSemesterCount : 0.0;
                System.out.println("Overall GPA (direct calculation): " + overallGPA);
                
                // Update the label directly
                Platform.runLater(() -> {
                    overallGpaLabel.setText(String.format("%.2f", overallGPA));
                    System.out.println("Overall GPA label updated to: " + overallGpaLabel.getText());
                    
                    // Automatically recalculate required GPA if goal is set
                    if (!goalGpaField.getText().isEmpty()) {
                        try {
                            calculateRequiredGPA();
                        } catch (Exception ex) {
                            // Ignore calculation errors during refresh
                        }
                    }
                });
            }
            
            System.out.println("===== HOME VIEW UI UPDATE COMPLETE =====\n");
        } catch (Exception e) {
            System.err.println("Error during force UI update: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * This method aggressively calculates and displays GPAs
     * Call this after any operation that might affect GPAs
     */
    public void forceDisplayGPAs() {
        try {
            System.out.println("\n=== AGGRESSIVE GPA DISPLAY UPDATE ===");
            
            // Get fresh semester data
            List<Semester> semesters = controller.getAllSemesters();
            System.out.println("Found " + semesters.size() + " semesters to process");
            
            // Calculate total semester GPA
            double totalGPA = 0.0;
            int validSemesters = 0;
            
            // Clear the semester pane
            semestersPane.getChildren().clear();
            
            if (semesters.isEmpty()) {
                // Create a styled "no semesters" message
                StackPane emptyPane = new StackPane();
                emptyPane.setPrefWidth(Math.min(800, screenWidth - 100)); // Responsive width
                emptyPane.setPrefHeight(200);
                
                VBox emptyBox = new VBox(15);
                emptyBox.setAlignment(Pos.CENTER);
                
                Text emptyText = new Text("You don't have any semesters yet");
                emptyText.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
                emptyText.setFill(Color.rgb(120, 120, 140));
                
                Text addText = new Text("Click 'Add Semester' to get started");
                addText.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                addText.setFill(Color.rgb(150, 150, 170));
                
                emptyBox.getChildren().addAll(emptyText, addText);
                emptyPane.getChildren().add(emptyBox);
                
                semestersPane.getChildren().add(emptyPane);
                
                // Set overall GPA to 0
                overallGpaLabel.setText("0.00");
                System.out.println("No semesters found, overall GPA set to 0.00");
            } else {
                // Process each semester
                for (Semester semester : semesters) {
                    // Force GPA calculation
                    double semesterGPA = semester.calculateGPA();
                    System.out.println("Semester: " + semester.getName() + " - GPA: " + semesterGPA);
                    
                    // Create an enhanced card
                    StackPane card = createEnhancedSemesterCard(semester, semesterGPA);
                    semestersPane.getChildren().add(card);
                    
                    // Count for overall GPA if valid
                    if (semesterGPA > 0) {
                        totalGPA += semesterGPA;
                        validSemesters++;
                    }
                }
                
                // Calculate and set overall GPA
                if (validSemesters > 0) {
                    double overallGPA = totalGPA / validSemesters;
                    overallGpaLabel.setText(String.format("%.2f", overallGPA));
                    System.out.println("Overall GPA set to: " + overallGPA);
                } else {
                    overallGpaLabel.setText("0.00");
                    System.out.println("No valid semester GPAs, overall set to 0.00");
                }
            }
            
            System.out.println("=== GPA DISPLAY UPDATE COMPLETE ===\n");
        } catch (Exception e) {
            System.err.println("ERROR in forceDisplayGPAs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Show an error alert
     * 
     * @param title The alert title
     * @param message The alert message
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert dialog
        alert.getDialogPane().setStyle(
            "-fx-background-color: #f8f8ff; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1px; " +
            "-fx-font-size: 14px;"
        );
        
        alert.showAndWait();
    }
    
    /**
     * Show an information alert
     * 
     * @param title The alert title
     * @param message The alert message
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert dialog
        alert.getDialogPane().setStyle(
            "-fx-background-color: #f8f8ff; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1px; " +
            "-fx-font-size: 14px;"
        );
        
        alert.showAndWait();
    }
    
    /**
     * Sets the application to full screen mode.
     * This method should be called after the stage is shown.
     * @param stage The primary stage of the application
     */
    public void setFullScreen(javafx.stage.Stage stage) {
        System.out.println("Setting stage to maximized");
        stage.setMaximized(true);
        
        // Additional step to ensure full screen after a short delay
        Platform.runLater(() -> {
            try {
                // Additional size adjustment after the stage is shown
                mainLayout.setPrefSize(stage.getWidth(), stage.getHeight());
                System.out.println("Adjusted size to stage dimensions: " + 
                                 stage.getWidth() + "x" + stage.getHeight());
            } catch (Exception e) {
                System.err.println("Error in setFullScreen: " + e.getMessage());
            }
        });
    }
    
    /**
     * Get the main layout for this view.
     * Additional sizing enforced here to ensure full screen display.
     * @return The main layout
     */
    public BorderPane getView() {
        // Ensure the layout uses maximum available space
        System.out.println("Getting HomeView with dimensions: " + 
                         mainLayout.getPrefWidth() + "x" + mainLayout.getPrefHeight());
        
        // Force preferred width to screen width one more time before returning
        mainLayout.setPrefWidth(screenWidth);
        mainLayout.setPrefHeight(screenHeight);
        
        return mainLayout;
    }
}