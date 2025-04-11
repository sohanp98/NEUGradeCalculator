package application.views;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.stage.Screen;

import application.controllers.SemesterController;
import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Semester;
import application.models.Subject;

/**
 * View class for the semester details screen with enhanced styling
 */
public class SemesterView {
    // Original fields - unchanged
    private BorderPane mainLayout;
    private SemesterController controller;
    private Semester semester;
    private TabPane tabPane;
    
    // Define color constants for consistent styling across the application
    private static final Color PRIMARY_COLOR = Color.rgb(0, 59, 111); // Northeastern Blue
    private static final Color SECONDARY_COLOR = Color.rgb(200, 16, 46); // Northeastern Red
    private static final Color ACCENT_COLOR = Color.rgb(0, 173, 86); // Green for grades
    private static final Color LIGHT_GRAY = Color.rgb(240, 240, 240);
    
    // Screen dimensions for responsive design
    private final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    private final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
    
    /**
     * Constructor
     * 
     * @param semester The semester to display
     */
    public SemesterView(Semester semester) {
        try {
        	
            this.semester = semester;
            this.controller = new SemesterController(semester);
           
            // Force refresh the semester data from the database
            controller.refreshSemester();
            
            // Get the refreshed semester object
            this.semester = controller.getSemester();
            
            System.out.println("SemesterView: Initialized with semester ID " + semester.getId());
            System.out.println("  Semester name: " + semester.getName());
            System.out.println("  Number of subjects: " + semester.getSubjects().size());
            System.out.println("  GPA: " + semester.calculateGPA());
            
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
            // We can't show an alert here because we don't have a UI yet
            System.err.println("Error initializing SemesterView: " + e.getMessage());
        }
    }
    
    /**
     * Initialize the view with enhanced styling
     */

    private void initialize() {
        try {
            System.out.println("\n===== INITIALIZING SEMESTER VIEW =====");
            
            // First, refresh semester data to ensure we have the latest
            controller.refreshSemester();
            semester = controller.getSemester();
            
            System.out.println("Working with semester: " + semester.getName() + ", ID: " + semester.getId());
            System.out.println("Subject count: " + semester.getSubjects().size());
            
            // Print current GPA
            double semesterGpa = semester.calculateGPA();
            System.out.println("Current semester GPA: " + semesterGpa);
            
            // Create a completely new layout (discard the old one)
            mainLayout = new BorderPane();
            mainLayout.setStyle("-fx-background-color: #f5f5ff;");
            mainLayout.setPadding(new Insets(20));
            
            // Set preferred size to maximize screen
            mainLayout.setPrefSize(screenWidth, screenHeight);
            mainLayout.setMinSize(screenWidth, screenHeight);
            mainLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            
            // Create header with enhanced styling
            Label headerLabel = new Label(semester.getName());
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            headerLabel.setTextFill(PRIMARY_COLOR);
            
            // Create a GPA label with color based on value
            Label gpaLabel = new Label(String.format("GPA: %.2f", semesterGpa));
            gpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            
            if (semesterGpa >= 3.7) {
                gpaLabel.setTextFill(ACCENT_COLOR); // Green for A/A+
            } else if (semesterGpa >= 3.0) {
                gpaLabel.setTextFill(Color.rgb(66, 133, 244)); // Blue for B
            } else if (semesterGpa >= 2.0) {
                gpaLabel.setTextFill(Color.rgb(251, 188, 5)); // Yellow for C
            } else {
                gpaLabel.setTextFill(SECONDARY_COLOR); // Red for D/F
            }
            
            HBox headerBox = new HBox(20);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            
            VBox titleBox = new VBox(5);
            titleBox.getChildren().addAll(headerLabel, gpaLabel);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            // Style buttons
            Button backButton = createStyledButton("Back to Home", LIGHT_GRAY);
            backButton.setTextFill(PRIMARY_COLOR);
            backButton.setPrefWidth(130);
            backButton.setStyle("-fx-background-color: white; -fx-border-color: " + toRgbString(PRIMARY_COLOR) + "; -fx-border-radius: 5; -fx-background-radius: 5;");
            
            Button addSubjectButton = createStyledButton("Add Subject", PRIMARY_COLOR);
            addSubjectButton.setTextFill(Color.WHITE);
            addSubjectButton.setPrefWidth(130);
            
            Button analyticsButton = createStyledButton("Analytics", ACCENT_COLOR);
            analyticsButton.setTextFill(Color.WHITE);
            analyticsButton.setPrefWidth(130);
            
            // Add shadow to buttons
            addButtonShadow(backButton);
            addButtonShadow(addSubjectButton);
            addButtonShadow(analyticsButton);
            
            headerBox.getChildren().addAll(titleBox, spacer, analyticsButton, addSubjectButton, backButton);
            
            // Add a colored divider below header
            VBox headerWithDivider = new VBox(10);
            
            Rectangle colorBar = new Rectangle();
            colorBar.setHeight(3);
            colorBar.widthProperty().bind(mainLayout.widthProperty().subtract(40));
            
            // Create gradient for color bar
            LinearGradient barGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, PRIMARY_COLOR),
                new Stop(0.5, SECONDARY_COLOR),
                new Stop(1, ACCENT_COLOR)
            );
            colorBar.setFill(barGradient);
            
            headerWithDivider.getChildren().addAll(headerBox, colorBar);
            mainLayout.setTop(headerWithDivider);
            BorderPane.setMargin(headerWithDivider, new Insets(0, 0, 20, 0));
            
            // Create a styled tab pane
            tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            tabPane.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-tab-min-width: 120px; " +
                "-fx-tab-max-width: 120px; " +
                "-fx-tab-min-height: 35px;"
            );
            
            // Dashboard tab with scrolling support
            Tab dashboardTab = new Tab("Dashboard");
            dashboardTab.setClosable(false);
            
            // Add ScrollPane for dashboardContent to enable scrolling
            ScrollPane dashboardScrollPane = new ScrollPane();
            dashboardScrollPane.setFitToWidth(true);
            dashboardScrollPane.setPrefWidth(screenWidth - 60);
            dashboardScrollPane.setStyle("-fx-background-color: transparent;");
            
            // Create dashboard content
            VBox dashboardContent = createDashboardContent();
            
            // Set the content to the scroll pane
            dashboardScrollPane.setContent(dashboardContent);
            dashboardTab.setContent(dashboardScrollPane);
            
            // Transcript tab with scrolling support
            Tab transcriptTab = new Tab("Transcript");
            transcriptTab.setClosable(false);
            
            // Add ScrollPane for transcriptContent to enable scrolling
            ScrollPane transcriptScrollPane = new ScrollPane();
            transcriptScrollPane.setFitToWidth(true);
            transcriptScrollPane.setPrefWidth(screenWidth - 60);
            transcriptScrollPane.setStyle("-fx-background-color: transparent;");
            
            // Create transcript content
            VBox transcriptContent = createTranscriptContent();
            
            // Set the content to the scroll pane
            transcriptScrollPane.setContent(transcriptContent);
            transcriptTab.setContent(transcriptScrollPane);
            
            // Performance tab with scrolling support
            Tab performanceTab = new Tab("Performance");
            performanceTab.setClosable(false);
            
            // Add ScrollPane for performanceContent to enable scrolling
            ScrollPane performanceScrollPane = new ScrollPane();
            performanceScrollPane.setFitToWidth(true);
            performanceScrollPane.setPrefWidth(screenWidth - 60);
            performanceScrollPane.setStyle("-fx-background-color: transparent;");
            
            // Create performance content
            VBox performanceContent = createPerformanceContent();
            
            // Set the content to the scroll pane
            performanceScrollPane.setContent(performanceContent);
            performanceTab.setContent(performanceScrollPane);
            
            tabPane.getTabs().addAll(dashboardTab, transcriptTab, performanceTab);
            
            // Create a container for the tab pane with shadow
            StackPane tabContainer = new StackPane();
            tabContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
            tabContainer.setPadding(new Insets(5));
            
            // Add shadow to tab container
            DropShadow shadow = new DropShadow();
            shadow.setRadius(8);
            shadow.setColor(Color.rgb(0, 0, 0, 0.15));
            shadow.setOffsetY(3);
            tabContainer.setEffect(shadow);
            
            tabContainer.getChildren().add(tabPane);
            mainLayout.setCenter(tabContainer);
            
            // Set up event handlers - unchanged
            backButton.setOnAction(e -> controller.navigateToHome());
            addSubjectButton.setOnAction(e -> showAddSubjectDialog());
            analyticsButton.setOnAction(e -> controller.navigateToAnalytics());
            
            // Register for data change events - unchanged
            application.utils.EventBus.getInstance().register(
                application.utils.DataChangedEvent.class,
                event -> javafx.application.Platform.runLater(() -> {
                    System.out.println("DataChangedEvent received in SemesterView - refreshing view");
                    // Full refresh of the view
                    try {
                        // Refresh semester data
                        controller.refreshSemester();
                        semester = controller.getSemester();
                        
                        // Completely rebuild tabs with fresh data
                        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
                        int selectedIndex = tabPane.getSelectionModel().getSelectedIndex();
                        
                        // Update dashboard tab with scrolling
                        VBox newDashboardContent = createDashboardContent();
                        ScrollPane newDashboardScrollPane = (ScrollPane) tabPane.getTabs().get(0).getContent();
                        newDashboardScrollPane.setContent(newDashboardContent);
                        
                        // Update transcript tab with scrolling
                        VBox newTranscriptContent = createTranscriptContent();
                        ScrollPane newTranscriptScrollPane = (ScrollPane) tabPane.getTabs().get(1).getContent();
                        newTranscriptScrollPane.setContent(newTranscriptContent);
                        
                        // Update performance tab with scrolling
                        VBox newPerformanceContent = createPerformanceContent();
                        ScrollPane newPerformanceScrollPane = (ScrollPane) tabPane.getTabs().get(2).getContent();
                        newPerformanceScrollPane.setContent(newPerformanceContent);
                        
                        // Re-select the previously selected tab
                        tabPane.getSelectionModel().select(selectedIndex);
                        
                        // Update GPA in header
                        gpaLabel.setText(String.format("GPA: %.2f", semester.calculateGPA()));
                        
                        // Update color based on new GPA
                        double updatedGpa = semester.calculateGPA();
                        if (updatedGpa >= 3.7) {
                            gpaLabel.setTextFill(ACCENT_COLOR);
                        } else if (updatedGpa >= 3.0) {
                            gpaLabel.setTextFill(Color.rgb(66, 133, 244));
                        } else if (updatedGpa >= 2.0) {
                            gpaLabel.setTextFill(Color.rgb(251, 188, 5));
                        } else {
                            gpaLabel.setTextFill(SECONDARY_COLOR);
                        }
                    } catch (Exception e) {
                        System.err.println("Error refreshing tabs: " + e.getMessage());
                        e.printStackTrace();
                    }
                })
            );
            
            System.out.println("===== SEMESTER VIEW INITIALIZATION COMPLETE =====\n");
        } catch (Exception e) {
            System.err.println("Error initializing view: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Error", "Failed to initialize view: " + e.getMessage());
        }
    }
    /**
     * Create content for the dashboard tab with fresh data
     * Enhanced styling only, functionality unchanged
     * 
     * @return The dashboard content
     */
    private VBox createDashboardContent() {
        System.out.println("\n===== CREATING DASHBOARD TAB WITH FRESH DATA =====");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(screenWidth - 60); // Maximize width
        
        try {
            // Ensure we have fresh data - unchanged
            controller.refreshSemester();
            semester = controller.getSemester();
            
            List<Subject> subjects = semester.getSubjects();
            System.out.println("Dashboard: Found " + subjects.size() + " subjects for semester ID " + semester.getId());
            
            if (subjects.isEmpty()) {
                // Style the empty state message
                StackPane emptyPane = new StackPane();
                emptyPane.setPadding(new Insets(40));
                emptyPane.setStyle(
                    "-fx-background-color: #f8f8ff; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10;"
                );
                
                Label noSubjectsLabel = new Label("No subjects added yet. Click 'Add Subject' to get started.");
                noSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                noSubjectsLabel.setTextFill(Color.rgb(100, 100, 120));
                
                emptyPane.getChildren().add(noSubjectsLabel);
                content.getChildren().add(emptyPane);
            } else {
                // For each subject, get fresh data and recalculate values - unchanged
                for (Subject subject : subjects) {
                    System.out.println("Dashboard: Processing subject ID " + subject.getId() + ": " + subject.getName());
                    
                    // Get fresh data for this subject
                    Subject refreshedSubject = controller.refreshSubjectData(subject.getId());
                    if (refreshedSubject != null) {
                        // Replace with the fresh data
                        subject = refreshedSubject;
                    } else {
                        System.out.println("Warning: Could not refresh subject data for ID " + subject.getId());
                    }
                    
                    // Create card with the fresh data and enhanced styling
                    VBox subjectCard = createEnhancedSubjectCard(subject);
                    content.getChildren().add(subjectCard);
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating dashboard content: " + e.getMessage());
            e.printStackTrace();
            
            Label errorLabel = new Label("Error loading subjects: " + e.getMessage());
            errorLabel.setTextFill(SECONDARY_COLOR);
            content.getChildren().add(errorLabel);
        }
        
        System.out.println("===== DASHBOARD TAB CREATION COMPLETE =====\n");
        return content;
    }
      
    /**
     * Update the dashboard tab with fresh data
     * Called after adding a new subject
     */
    private void updateDashboardTab() {
        try {
            System.out.println("\n===== UPDATING DASHBOARD TAB DIRECTLY =====");
            
            // Refresh semester data
            controller.refreshSemester();
            semester = controller.getSemester();
            
            System.out.println("Semester: " + semester.getName() + ", ID: " + semester.getId());
            System.out.println("Subject count: " + semester.getSubjects().size());
            
            // Calculate semester GPA
            double semesterGPA = semester.calculateGPA();
            System.out.println("Semester GPA: " + semesterGPA);
            
            // Update the GPA label in the header
            VBox headerContainer = (VBox) mainLayout.getTop();
            HBox headerBox = (HBox) headerContainer.getChildren().get(0);
            VBox titleBox = (VBox) headerBox.getChildren().get(0);
            Label gpaLabel = (Label) titleBox.getChildren().get(1);
            
            gpaLabel.setText(String.format("GPA: %.2f", semesterGPA));
            
            // Update color based on GPA
            if (semesterGPA >= 3.7) {
                gpaLabel.setTextFill(ACCENT_COLOR);
            } else if (semesterGPA >= 3.0) {
                gpaLabel.setTextFill(Color.rgb(66, 133, 244));
            } else if (semesterGPA >= 2.0) {
                gpaLabel.setTextFill(Color.rgb(251, 188, 5));
            } else {
                gpaLabel.setTextFill(SECONDARY_COLOR);
            }
            
            System.out.println("Updated header GPA label to: " + semesterGPA);
            
            // Refresh the dashboard tab content with scrolling
            Tab dashboardTab = tabPane.getTabs().get(0);
            
            // Get the current scroll pane
            ScrollPane scrollPane = (ScrollPane) dashboardTab.getContent();
            
            // Create new dashboard content
            VBox newContent = createDashboardContent();
            
            // Update the scroll pane content
            scrollPane.setContent(newContent);
            
            System.out.println("===== DASHBOARD TAB UPDATE COMPLETE =====\n");
        } catch (Exception e) {
            System.err.println("Error updating dashboard tab: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create a visual card for a subject with fresh calculations and enhanced styling
     * 
     * @param subject The subject
     * @return The VBox representing the subject card
     */
    private VBox createEnhancedSubjectCard(Subject subject) {
        System.out.println("Creating card for subject: " + subject.getName() + ", ID: " + subject.getId());
        
        // Force recalculation of values - unchanged
        double percentage = subject.calculateOverallPercentage();
        String letterGrade = subject.calculateLetterGrade();
        double gpa = subject.calculateGPA();
        
        System.out.println("  Current values - Percentage: " + percentage + 
                         ", Grade: " + letterGrade + 
                         ", GPA: " + gpa);
        
        // Create enhanced card
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(screenWidth - 100); // Set preferred width to maximize
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        
        // Add shadow to card
        DropShadow cardShadow = new DropShadow();
        cardShadow.setRadius(8);
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        cardShadow.setOffsetY(3);
        card.setEffect(cardShadow);
        
        // Header with subject name and delete button
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(subject.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameLabel.setTextFill(PRIMARY_COLOR);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Enhanced delete button
        Button deleteButton = new Button("×");
        deleteButton.setStyle(
            "-fx-background-color: " + toRgbString(SECONDARY_COLOR) + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-min-width: 28px; " +
            "-fx-min-height: 28px; " +
            "-fx-max-width: 28px; " +
            "-fx-max-height: 28px; " +
            "-fx-padding: 0; " +
            "-fx-background-radius: 14;"
        );
        
        headerBox.getChildren().addAll(nameLabel, spacer, deleteButton);
        
        // Grade info with enhanced styling
        HBox gradeBox = new HBox(15);
        gradeBox.setAlignment(Pos.CENTER_LEFT);
        
        // Create grade circle indicator
        StackPane gradeCircle = new StackPane();
        Circle circle = new Circle(24);
        
        // Color the circle based on grade
        Color circleColor;
        if (gpa >= 3.7) {
            circleColor = ACCENT_COLOR;
        } else if (gpa >= 3.0) {
            circleColor = Color.rgb(66, 133, 244);
        } else if (gpa >= 2.0) {
            circleColor = Color.rgb(251, 188, 5);
        } else {
            circleColor = SECONDARY_COLOR;
        }
        
        circle.setFill(circleColor);
        
        // Grade letter inside circle
        Label letterLabel = new Label(letterGrade);
        letterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        letterLabel.setTextFill(Color.WHITE);
        
        gradeCircle.getChildren().addAll(circle, letterLabel);
        
        // Styled grade label
        Label gradeLabel = new Label(String.format("%.1f%% | GPA: %.1f", percentage, gpa));
        gradeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        gradeLabel.setTextFill(PRIMARY_COLOR);
        
        gradeBox.getChildren().addAll(gradeCircle, gradeLabel);
        
        // Styled separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e0e0e0;");
        
        // Progress section - keep original implementation with enhanced styling
        VBox progressBox = new VBox(12);
        
        // Goal section
        HBox goalBox = new HBox(10);
        goalBox.setAlignment(Pos.CENTER_LEFT);
        
        Label goalLabel = new Label("Goal:");
        goalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        goalLabel.setTextFill(PRIMARY_COLOR);
        
        TextField goalField = new TextField(String.valueOf(subject.getGoalPercentage()));
        goalField.setPrefWidth(80);
        styleTextField(goalField);
        
        Button calculateButton = createStyledButton("Calculate Required Scores", ACCENT_COLOR);
        calculateButton.setTextFill(Color.WHITE);
        calculateButton.setPrefWidth(200);
        addButtonShadow(calculateButton);
        
        goalBox.getChildren().addAll(goalLabel, goalField, calculateButton);
        
        // Required scores section - use VBox instead of single label
        VBox requiredScoresBox = new VBox(8);
        requiredScoresBox.setPadding(new Insets(12));
        requiredScoresBox.setStyle(
            "-fx-background-color: #f8f8ff; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );
        
        Label requiredScoresHeaderLabel = new Label("Required Scores for Remaining Assessments:");
        requiredScoresHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        requiredScoresHeaderLabel.setTextFill(PRIMARY_COLOR);
        requiredScoresBox.getChildren().add(requiredScoresHeaderLabel);
        
        // Add this initially - will be updated when Calculate button is clicked
        Label initialMessageLabel = new Label("Enter a goal percentage and click Calculate.");
        initialMessageLabel.setStyle("-fx-font-style: italic;");
        initialMessageLabel.setTextFill(Color.rgb(120, 120, 120));
        requiredScoresBox.getChildren().add(initialMessageLabel);
        
        progressBox.getChildren().addAll(goalBox, requiredScoresBox);
        
        // Button actions with enhanced styling
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));
        
        Button editButton = createStyledButton("Edit Weightages", PRIMARY_COLOR);
        editButton.setTextFill(Color.WHITE);
        editButton.setPrefWidth(180);
        addButtonShadow(editButton);
        
        Button inputGradesButton = createStyledButton("Input Grades", PRIMARY_COLOR);
        inputGradesButton.setTextFill(Color.WHITE);
        inputGradesButton.setPrefWidth(180);
        addButtonShadow(inputGradesButton);
        
        buttonBox.getChildren().addAll(editButton, inputGradesButton);
        
        card.getChildren().addAll(headerBox, gradeBox, separator, progressBox, buttonBox);
        
        // Set up event handlers - unchanged
        editButton.setOnAction(e -> showEditWeightagesDialog(subject));
        inputGradesButton.setOnAction(e -> showInputGradesDialog(subject));
        
        // Original calculateButton implementation - unchanged
        calculateButton.setOnAction(e -> {
            try {
                // First, clear existing results
                requiredScoresBox.getChildren().clear();
                requiredScoresBox.getChildren().add(requiredScoresHeaderLabel);
                
                // Parse and set the goal percentage
                double goalPercentage = Double.parseDouble(goalField.getText());
                subject.setGoalPercentage(goalPercentage);
                
                // Calculate required scores using our new method
                Map<String, Double> requiredScores = subject.calculateRequiredScores();
                
                if (requiredScores.isEmpty()) {
                    // Goal is not achievable
                    Label unachievableLabel = new Label("Goal is not achievable with remaining assessments.");
                    unachievableLabel.setStyle("-fx-text-fill: " + toRgbString(SECONDARY_COLOR) + ";");
                    requiredScoresBox.getChildren().add(unachievableLabel);
                    
                    // Add suggestion for a possible achievable goal if applicable
                    if (percentage > 0) {
                        double achievableGoal = subject.calculateMaxPossibleScore();
                        Label suggestionLabel = new Label(String.format(
                            "Maximum possible score with remaining assessments: %.1f%%", achievableGoal));
                        suggestionLabel.setStyle("-fx-font-style: italic;");
                        requiredScoresBox.getChildren().add(suggestionLabel);
                    }
                } else {
                    // Goal is achievable, show required scores for each assessment type
                    
                    // First add an explanation of the strategy
                    Label strategyLabel = new Label("Focus Strategy: Higher scores on higher-weight assessments");
                    strategyLabel.setStyle("-fx-font-style: italic; -fx-font-size: 11px;");
                    requiredScoresBox.getChildren().add(strategyLabel);
                    
                    // Sort assessment types by weight (highest first) to show priorities
                    List<String> assessmentTypes = new ArrayList<>(requiredScores.keySet());
                    
                    // Try to get the assessment types to sort by their weights
                    Map<String, Double> displayWeights = new HashMap<>();
                    for (AssessmentType assessmentType : subject.getAssessmentTypes().values()) {
                        displayWeights.put(assessmentType.getDisplayName(), assessmentType.getWeight());
                    }
                    
                    // Sort by weight, highest first
                    assessmentTypes.sort((t1, t2) -> Double.compare(
                        displayWeights.getOrDefault(t1, 0.0), 
                        displayWeights.getOrDefault(t2, 0.0)
                    ));
                    Collections.reverse(assessmentTypes); // Reverse to get highest first
                    
                    for (String assessmentType : assessmentTypes) {
                        double requiredScore = requiredScores.get(assessmentType);
                        double typeWeight = displayWeights.getOrDefault(assessmentType, 0.0);
                        
                        Label scoreLabel = new Label(String.format(
                            "%s (%.0f%%): %.1f%%", assessmentType, typeWeight, requiredScore));
                        
                        // Color-code scores based on difficulty - same logic, enhanced colors
                        if (requiredScore > 95) {
                            scoreLabel.setStyle("-fx-text-fill: " + toRgbString(SECONDARY_COLOR) + "; -fx-font-weight: bold;"); // Red - very challenging
                        } else if (requiredScore > 90) {
                            scoreLabel.setStyle("-fx-text-fill: #ff8c00; -fx-font-weight: bold;"); // Dark orange - difficult
                        } else if (requiredScore > 80) {
                            scoreLabel.setStyle("-fx-text-fill: #ff8c00;"); // Orange - moderately difficult
                        } else if (requiredScore == 100.0) {
                            // Special case for "max out this area"
                            scoreLabel.setStyle("-fx-text-fill: " + toRgbString(ACCENT_COLOR) + "; -fx-font-weight: bold;"); // Green
                        }
                        
                        requiredScoresBox.getChildren().add(scoreLabel);
                    }
                }
            } catch (NumberFormatException ex) {
                // Invalid goal input
                requiredScoresBox.getChildren().clear();
                requiredScoresBox.getChildren().add(requiredScoresHeaderLabel);
                
                Label errorLabel = new Label("Please enter a valid goal percentage.");
                errorLabel.setStyle("-fx-text-fill: " + toRgbString(SECONDARY_COLOR) + ";");
                requiredScoresBox.getChildren().add(errorLabel);
            }
        });
        
        // Set up delete button event handler - unchanged
        deleteButton.setOnAction(e -> showDeleteSubjectConfirmation(subject));
        
        return card;
    }
    
    /**
     * Show a confirmation dialog before deleting a subject
     * Enhanced styling only, functionality unchanged
     * 
     * @param subject The subject to delete
     */
    private void showDeleteSubjectConfirmation(Subject subject) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Subject");
        alert.setHeaderText("Are you sure you want to delete " + subject.getName() + "?");
        alert.setContentText("This action cannot be undone. All grades and assessments for this subject will be permanently deleted.");
        
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
        
        // Show the dialog and handle the result - unchanged
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == deleteButton) {
                try {
                    // Delete the subject
                    controller.deleteSubject(subject.getId());
                    
                    // Refresh the view - unchanged
                    initialize();
                    
                    // Show success message
                    showInfoAlert("Subject Deleted", "The subject was successfully deleted.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showErrorAlert("Error", "Failed to delete subject: " + ex.getMessage());
                }
            }
        });
    }
    
    /**
     * Create content for the transcript tab with fresh data
     * Enhanced styling only, functionality unchanged
     * 
     * @return The transcript content
     */
    private VBox createTranscriptContent() {
        System.out.println("\n===== CREATING TRANSCRIPT TAB =====");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(screenWidth - 60); // Maximize width
        
        // Styled title
        Label titleLabel = new Label("Semester Transcript");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(PRIMARY_COLOR);
        
        // Create styled container for grid
        StackPane gridContainer = new StackPane();
        gridContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        gridContainer.setPadding(new Insets(5));
        gridContainer.setPrefWidth(screenWidth - 100); // Maximize width
        
        // Add shadow to container
        DropShadow shadow = new DropShadow();
        shadow.setRadius(8);
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setOffsetY(3);
        gridContainer.setEffect(shadow);
        
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setPrefWidth(screenWidth - 150); // Maximize width
        
        // Styled headers
        Label subjectHeaderLabel = new Label("Subject");
        subjectHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        subjectHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label percentageHeaderLabel = new Label("Percentage");
        percentageHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        percentageHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label letterGradeHeaderLabel = new Label("Letter Grade");
        letterGradeHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        letterGradeHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label gpaHeaderLabel = new Label("GPA");
        gpaHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gpaHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        grid.add(subjectHeaderLabel, 0, 0);
        grid.add(percentageHeaderLabel, 1, 0);
        grid.add(letterGradeHeaderLabel, 2, 0);
        grid.add(gpaHeaderLabel, 3, 0);
        
        // Set column widths
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(300)); // Subject column
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(150)); // Percentage column
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(150)); // Letter Grade column
        grid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(100)); // GPA column
        
        // Add header separator
        Rectangle headerLine = new Rectangle(screenWidth - 200, 2);
        headerLine.setFill(Color.rgb(230, 230, 240));
        grid.add(headerLine, 0, 1, 4, 1);
        
        // Make sure we have the freshest data - unchanged
        try {
            controller.refreshSemester();
            List<Subject> subjects = semester.getSubjects();
            
            System.out.println("Transcript: Refreshed semester data");
            System.out.println("Transcript: Number of subjects: " + subjects.size());
            
            if (subjects.isEmpty()) {
                // Styled empty message
                Label noSubjectsLabel = new Label("No subjects added yet.");
                noSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                noSubjectsLabel.setTextFill(Color.rgb(100, 100, 120));
                grid.add(noSubjectsLabel, 0, 2, 4, 1);
            } else {
                double semesterTotal = 0.0;
                int validSubjectCount = 0;
                
                for (int i = 0; i < subjects.size(); i++) {
                    Subject subject = subjects.get(i);
                    
                    // Make sure we have fresh data - unchanged
                    Subject refreshedSubject = controller.refreshSubjectData(subject.getId());
                    if (refreshedSubject != null) {
                        subject = refreshedSubject;
                    }
                    
                    // Force recalculation of values - unchanged
                    double percentage = subject.calculateOverallPercentage();
                    String letterGrade = subject.calculateLetterGrade();
                    double gpa = subject.calculateGPA();
                    
                    System.out.println("Transcript: Subject " + subject.getName() + 
                                      " - Percentage: " + percentage + 
                                      ", Grade: " + letterGrade + 
                                      ", GPA: " + gpa);
                    
                    if (gpa > 0) {
                        semesterTotal += gpa;
                        validSubjectCount++;
                    }
                    
                    // Style the rows with alternating colors
                    Rectangle rowBg = null;
                    if (i % 2 == 1) {
                        rowBg = new Rectangle(screenWidth - 200, 40);
                        rowBg.setFill(Color.rgb(248, 248, 255, 0.5));
                        rowBg.setArcWidth(5);
                        rowBg.setArcHeight(5);
                        grid.add(rowBg, 0, i + 2, 4, 1);
                    }
                    
                    // Styled labels
                    Label subjectLabel = new Label(subject.getName());
                    subjectLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 15));
                    subjectLabel.setTextFill(PRIMARY_COLOR);
                    
                    Label percentageLabel = new Label(String.format("%.1f%%", percentage));
                    percentageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
                    
                    Label letterGradeLabel = new Label(letterGrade);
                    letterGradeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                    
                    // Color based on grade
                    if (gpa >= 3.7) {
                        letterGradeLabel.setTextFill(ACCENT_COLOR);
                    } else if (gpa >= 3.0) {
                        letterGradeLabel.setTextFill(Color.rgb(66, 133, 244));
                    } else if (gpa >= 2.0) {
                        letterGradeLabel.setTextFill(Color.rgb(251, 188, 5));
                    } else {
                        letterGradeLabel.setTextFill(SECONDARY_COLOR);
                    }
                    
                    Label gpaLabel = new Label(String.format("%.1f", gpa));
                    gpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                    gpaLabel.setTextFill(letterGradeLabel.getTextFill());
                    
                    grid.add(subjectLabel, 0, i + 2);
                    grid.add(percentageLabel, 1, i + 2);
                    grid.add(letterGradeLabel, 2, i + 2);
                    grid.add(gpaLabel, 3, i + 2);
                }
                
                // Add semester GPA with styling
                Separator separator = new Separator();
                grid.add(separator, 0, subjects.size() + 2, 4, 1);
                
                Label overallLabel = new Label("Overall GPA");
                overallLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                overallLabel.setTextFill(PRIMARY_COLOR);
                
                // Calculate overall GPA - unchanged
                double overallGPA = validSubjectCount > 0 ? semesterTotal / validSubjectCount : 0.0;
                System.out.println("Transcript: Calculated overall GPA: " + overallGPA);
                
                Label overallGpaLabel = new Label(String.format("%.2f", overallGPA));
                overallGpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                
                // Color based on GPA value
                if (overallGPA >= 3.7) {
                    overallGpaLabel.setTextFill(ACCENT_COLOR);
                } else if (overallGPA >= 3.0) {
                    overallGpaLabel.setTextFill(Color.rgb(66, 133, 244));
                } else if (overallGPA >= 2.0) {
                    overallGpaLabel.setTextFill(Color.rgb(251, 188, 5));
                } else {
                    overallGpaLabel.setTextFill(SECONDARY_COLOR);
                }
                
                grid.add(overallLabel, 0, subjects.size() + 3);
                grid.add(overallGpaLabel, 3, subjects.size() + 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error refreshing transcript data: " + e.getMessage());
            
            Label errorLabel = new Label("Error loading data: " + e.getMessage());
            errorLabel.setTextFill(SECONDARY_COLOR);
            grid.add(errorLabel, 0, 2, 4, 1);
        }
        
        gridContainer.getChildren().add(grid);
        content.getChildren().addAll(titleLabel, gridContainer);
        
        System.out.println("===== TRANSCRIPT TAB CREATION COMPLETE =====\n");
        return content;
    }
    
    /**
     * Create content for the performance tab with fresh data
     * Enhanced styling only, functionality unchanged
     * 
     * @return The performance content
     */
    private VBox createPerformanceContent() {
        System.out.println("\n===== CREATING PERFORMANCE TAB WITH FRESH DATA =====");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(screenWidth - 60); // Maximize width
        
        // Styled title
        Label titleLabel = new Label("Subject Performance Comparison");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(PRIMARY_COLOR);
        
        try {
            // Ensure we have fresh data - unchanged
            controller.refreshSemester();
            semester = controller.getSemester();
            
            List<Subject> subjects = semester.getSubjects();
            System.out.println("Performance: Found " + subjects.size() + " subjects");
            
            // For each subject, refresh data and recalculate - unchanged
            for (int i = 0; i < subjects.size(); i++) {
                Subject refreshedSubject = controller.refreshSubjectData(subjects.get(i).getId());
                if (refreshedSubject != null) {
                    subjects.set(i, refreshedSubject);
                    
                    // Force recalculation
                    double percentage = refreshedSubject.calculateOverallPercentage();
                    String letterGrade = refreshedSubject.calculateLetterGrade();
                    double gpa = refreshedSubject.calculateGPA();
                    
                    System.out.println("Performance: Subject " + refreshedSubject.getName() + 
                                      " - Percentage: " + percentage + 
                                      ", Grade: " + letterGrade + 
                                      ", GPA: " + gpa);
                }
            }
            
            if (subjects.size() < 2) {
                // Styled empty state
                StackPane emptyPane = new StackPane();
                emptyPane.setPadding(new Insets(40));
                emptyPane.setPrefWidth(screenWidth - 100); // Maximize width
                emptyPane.setStyle(
                    "-fx-background-color: #f8f8ff; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 10; " +
                    "-fx-background-radius: 10;"
                );
                
                Label notEnoughSubjectsLabel = new Label("Need at least 2 subjects to compare performance.");
                notEnoughSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                notEnoughSubjectsLabel.setTextFill(Color.rgb(100, 100, 120));
                
                emptyPane.getChildren().add(notEnoughSubjectsLabel);
                content.getChildren().addAll(titleLabel, emptyPane);
            } else {
                // Create a styled container for the charts
                StackPane chartContainer = new StackPane();
                chartContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
                chartContainer.setPadding(new Insets(5));
                chartContainer.setPrefWidth(screenWidth - 100); // Maximize width
                chartContainer.setPrefHeight(screenHeight - 250); // Maximize height
                
                // Add shadow
                DropShadow chartShadow = new DropShadow();
                chartShadow.setRadius(8);
                chartShadow.setColor(Color.rgb(0, 0, 0, 0.15));
                chartShadow.setOffsetY(3);
                chartContainer.setEffect(chartShadow);
                
                // Create a TabPane for different chart types - unchanged
                TabPane chartTabPane = new TabPane();
                chartTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                chartTabPane.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-tab-min-width: 150px; "
                );
                
                // Percentage comparison tab - unchanged
                Tab percentageTab = new Tab("Percentage Comparison");
                VBox percentageContent = new VBox(15);
                percentageContent.setPadding(new Insets(15));
                percentageContent.setPrefSize(screenWidth - 150, screenHeight - 300); // Maximize size
                
                // Create bar chart for percentages
                javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
                javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis(0, 100, 10);
                xAxis.setLabel("Subject");
                yAxis.setLabel("Percentage");
                
                javafx.scene.chart.BarChart<String, Number> percentageChart = 
                    new javafx.scene.chart.BarChart<>(xAxis, yAxis);
                percentageChart.setTitle("Overall Percentage Comparison");
                percentageChart.setPrefSize(screenWidth - 200, screenHeight - 350); // Maximize chart size
                
                // Create a series for the data
                javafx.scene.chart.XYChart.Series<String, Number> percentageSeries = 
                    new javafx.scene.chart.XYChart.Series<>();
                percentageSeries.setName("Overall Percentage");
                
                for (Subject subject : subjects) {
                    percentageSeries.getData().add(
                        new javafx.scene.chart.XYChart.Data<>(
                            subject.getName(), 
                            subject.calculateOverallPercentage()
                        )
                    );
                }
                
                percentageChart.getData().add(percentageSeries);
                percentageChart.setAnimated(false);
                percentageChart.setLegendVisible(false);
                
                percentageContent.getChildren().add(percentageChart);
                percentageTab.setContent(percentageContent);
                
                // GPA comparison tab - unchanged
                Tab gpaTab = new Tab("GPA Comparison");
                VBox gpaContent = new VBox(15);
                gpaContent.setPadding(new Insets(15));
                gpaContent.setPrefSize(screenWidth - 150, screenHeight - 300); // Maximize size
                
                // Create bar chart for GPAs
                javafx.scene.chart.CategoryAxis gpaXAxis = new javafx.scene.chart.CategoryAxis();
                javafx.scene.chart.NumberAxis gpaYAxis = new javafx.scene.chart.NumberAxis(0, 4, 0.5);
                gpaXAxis.setLabel("Subject");
                gpaYAxis.setLabel("GPA");
                
                javafx.scene.chart.BarChart<String, Number> gpaChart = 
                    new javafx.scene.chart.BarChart<>(gpaXAxis, gpaYAxis);
                gpaChart.setTitle("GPA Comparison");
                gpaChart.setPrefSize(screenWidth - 200, screenHeight - 350); // Maximize chart size
                
                // Create a series for the data
                javafx.scene.chart.XYChart.Series<String, Number> gpaSeries = 
                    new javafx.scene.chart.XYChart.Series<>();
                gpaSeries.setName("GPA");
                
                for (Subject subject : subjects) {
                    gpaSeries.getData().add(
                        new javafx.scene.chart.XYChart.Data<>(
                            subject.getName(), 
                            subject.calculateGPA()
                        )
                    );
                }
                
                gpaChart.getData().add(gpaSeries);
                gpaChart.setAnimated(false);
                gpaChart.setLegendVisible(false);
                
                gpaContent.getChildren().add(gpaChart);
                gpaTab.setContent(gpaContent);
                
                // Add comparison table tab - unchanged
                Tab tableTab = new Tab("Detailed Comparison");
                VBox tableContent = new VBox(15);
                tableContent.setPadding(new Insets(15));
                tableContent.setPrefSize(screenWidth - 150, screenHeight - 300); // Maximize size
                
                // Create grid for the table
                GridPane comparisonGrid = new GridPane();
                comparisonGrid.setHgap(25);
                comparisonGrid.setVgap(15);
                comparisonGrid.setPadding(new Insets(20));
                comparisonGrid.setPrefWidth(screenWidth - 200); // Maximize width
                
                // Styled headers
                Label metricHeaderLabel = new Label("Metric");
                metricHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                metricHeaderLabel.setTextFill(PRIMARY_COLOR);
                comparisonGrid.add(metricHeaderLabel, 0, 0);
                
                for (int i = 0; i < subjects.size(); i++) {
                    Label subjectHeaderLabel = new Label(subjects.get(i).getName());
                    subjectHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    subjectHeaderLabel.setTextFill(PRIMARY_COLOR);
                    comparisonGrid.add(subjectHeaderLabel, i + 1, 0);
                }
                
                // Add header separator
                Rectangle headerLine = new Rectangle(screenWidth - 250, 2);
                headerLine.setFill(Color.rgb(230, 230, 240));
                comparisonGrid.add(headerLine, 0, 1, subjects.size() + 1, 1);
                
                // Metrics rows - unchanged
                String[] metrics = {"Overall Percentage", "Letter Grade", "GPA"};
                
                for (int i = 0; i < metrics.length; i++) {
                    Label metricLabel = new Label(metrics[i]);
                    metricLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
                    comparisonGrid.add(metricLabel, 0, i + 2);
                    
                    for (int j = 0; j < subjects.size(); j++) {
                        Subject subject = subjects.get(j);
                        Label valueLabel = new Label();
                        
                        switch (i) {
                            case 0: // Overall Percentage
                                valueLabel.setText(String.format("%.1f%%", subject.calculateOverallPercentage()));
                                valueLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
                                break;
                            case 1: // Letter Grade
                                valueLabel.setText(subject.calculateLetterGrade());
                                valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                                
                                // Color based on grade
                                double gpa = subject.calculateGPA();
                                if (gpa >= 3.7) {
                                    valueLabel.setTextFill(ACCENT_COLOR);
                                } else if (gpa >= 3.0) {
                                    valueLabel.setTextFill(Color.rgb(66, 133, 244));
                                } else if (gpa >= 2.0) {
                                    valueLabel.setTextFill(Color.rgb(251, 188, 5));
                                } else {
                                    valueLabel.setTextFill(SECONDARY_COLOR);
                                }
                                break;
                            case 2: // GPA
                                valueLabel.setText(String.format("%.1f", subject.calculateGPA()));
                                valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                                
                                // Color based on GPA value
                                gpa = subject.calculateGPA();
                                if (gpa >= 3.7) {
                                    valueLabel.setTextFill(ACCENT_COLOR);
                                } else if (gpa >= 3.0) {
                                    valueLabel.setTextFill(Color.rgb(66, 133, 244));
                                } else if (gpa >= 2.0) {
                                    valueLabel.setTextFill(Color.rgb(251, 188, 5));
                                } else {
                                    valueLabel.setTextFill(SECONDARY_COLOR);
                                }
                                break;
                        }
                        
                        comparisonGrid.add(valueLabel, j + 1, i + 2);
                    }
                }
                
                tableContent.getChildren().add(comparisonGrid);
                tableTab.setContent(tableContent);
                
                // Add all tabs
                chartTabPane.getTabs().addAll(percentageTab, gpaTab, tableTab);
                
                chartContainer.getChildren().add(chartTabPane);
                content.getChildren().addAll(titleLabel, chartContainer);
            }
        } catch (Exception e) {
            System.err.println("Error creating performance content: " + e.getMessage());
            e.printStackTrace();
            
            Label errorLabel = new Label("Error loading performance data: " + e.getMessage());
            errorLabel.setTextFill(SECONDARY_COLOR);
            content.getChildren().addAll(titleLabel, errorLabel);
        }
        
        System.out.println("===== PERFORMANCE TAB CREATION COMPLETE =====\n");
        return content;
    }
    
    /**
     * Show dialog to add a new subject
     * Enhanced styling only, functionality unchanged
     */
    private void showAddSubjectDialog() {
        if (semester.getSubjects().size() >= 2) {
            showErrorAlert("Maximum Subjects", "You can only add up to 2 subjects per semester.");
            return;
        }
        
        // Create dialog with maximized size
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Subject");
        dialog.setMinWidth(800);
        dialog.setMinHeight(700);
        
        VBox dialogVBox = new VBox(20);
        dialogVBox.setPadding(new Insets(30));
        dialogVBox.setStyle("-fx-background-color: #f8f8ff;");
        
        // Styled title
        Label titleLabel = new Label("Add New Subject");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(PRIMARY_COLOR);
        
        // Subject name with styling
        Label nameLabel = new Label("Subject Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setTextFill(PRIMARY_COLOR);
        
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., CS5010 - Programming Design Paradigm");
        nameField.setPrefHeight(40);
        styleTextField(nameField);
        
        // Assessment types
        Label assessmentLabel = new Label("Assessment Types:");
        assessmentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        assessmentLabel.setTextFill(PRIMARY_COLOR);
        
        // Create styled container for assessment types
        VBox assessmentBox = new VBox(18);
        assessmentBox.setPadding(new Insets(20));
        assessmentBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8;"
        );
        
        // Add shadow to assessment container
        DropShadow boxShadow = new DropShadow();
        boxShadow.setRadius(5);
        boxShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        boxShadow.setOffsetY(2);
        assessmentBox.setEffect(boxShadow);
        
        // Assignments - unchanged fields with enhanced styling
        HBox assignmentBox = new HBox(15);
        assignmentBox.setAlignment(Pos.CENTER_LEFT);
        
        Label assignmentLabel = new Label("Assignments:");
        assignmentLabel.setMinWidth(150);
        assignmentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        assignmentLabel.setTextFill(PRIMARY_COLOR);
        
        ComboBox<Integer> assignmentCountCombo = new ComboBox<>();
        for (int i = 0; i <= 10; i++) {
            assignmentCountCombo.getItems().add(i);
        }
        assignmentCountCombo.setValue(0);
        assignmentCountCombo.setPrefWidth(100);
        assignmentCountCombo.setPrefHeight(40);
        
        Label assignmentWeightLabel = new Label("Weight (%):");
        assignmentWeightLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        
        TextField assignmentWeightField = new TextField("0");
        assignmentWeightField.setPrefWidth(100);
        assignmentWeightField.setPrefHeight(40);
        styleTextField(assignmentWeightField);
        
        assignmentBox.getChildren().addAll(assignmentLabel, assignmentCountCombo, assignmentWeightLabel, assignmentWeightField);
        
        // Quizzes - unchanged fields with enhanced styling
        HBox quizBox = new HBox(15);
        quizBox.setAlignment(Pos.CENTER_LEFT);
        
        Label quizLabel = new Label("Quizzes:");
        quizLabel.setMinWidth(150);
        quizLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        quizLabel.setTextFill(PRIMARY_COLOR);
        
        ComboBox<Integer> quizCountCombo = new ComboBox<>();
        for (int i = 0; i <= 10; i++) {
            quizCountCombo.getItems().add(i);
        }
        quizCountCombo.setValue(0);
        quizCountCombo.setPrefWidth(100);
        quizCountCombo.setPrefHeight(40);
        
        Label quizWeightLabel = new Label("Weight (%):");
        quizWeightLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        
        TextField quizWeightField = new TextField("0");
        quizWeightField.setPrefWidth(100);
        quizWeightField.setPrefHeight(40);
        styleTextField(quizWeightField);
        
        quizBox.getChildren().addAll(quizLabel, quizCountCombo, quizWeightLabel, quizWeightField);
        
        // Midterm - unchanged fields with enhanced styling
        HBox midtermBox = new HBox(15);
        midtermBox.setAlignment(Pos.CENTER_LEFT);
        
        Label midtermLabel = new Label("Midterm:");
        midtermLabel.setMinWidth(150);
        midtermLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        midtermLabel.setTextFill(PRIMARY_COLOR);
        
        Label midtermWeightLabel = new Label("Weight (%):");
        midtermWeightLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        
        TextField midtermWeightField = new TextField("0");
        midtermWeightField.setPrefWidth(100);
        midtermWeightField.setPrefHeight(40);
        styleTextField(midtermWeightField);
        
        midtermBox.getChildren().addAll(midtermLabel, midtermWeightLabel, midtermWeightField);
        
        // Final Exam - unchanged fields with enhanced styling
        HBox finalExamBox = new HBox(15);
        finalExamBox.setAlignment(Pos.CENTER_LEFT);
        
        Label finalExamLabel = new Label("Final Exam:");
        finalExamLabel.setMinWidth(150);
        finalExamLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        finalExamLabel.setTextFill(PRIMARY_COLOR);
        
        Label finalExamWeightLabel = new Label("Weight (%):");
        finalExamWeightLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        
        TextField finalExamWeightField = new TextField("0");
        finalExamWeightField.setPrefWidth(100);
        finalExamWeightField.setPrefHeight(40);
        styleTextField(finalExamWeightField);
        
        finalExamBox.getChildren().addAll(finalExamLabel, finalExamWeightLabel, finalExamWeightField);
        
        // Final Project - unchanged fields with enhanced styling
        HBox finalProjectBox = new HBox(15);
        finalProjectBox.setAlignment(Pos.CENTER_LEFT);
        
        Label finalProjectLabel = new Label("Final Project:");
        finalProjectLabel.setMinWidth(150);
        finalProjectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        finalProjectLabel.setTextFill(PRIMARY_COLOR);
        
        Label finalProjectWeightLabel = new Label("Weight (%):");
        finalProjectWeightLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        
        TextField finalProjectWeightField = new TextField("0");
        finalProjectWeightField.setPrefWidth(100);
        finalProjectWeightField.setPrefHeight(40);
        styleTextField(finalProjectWeightField);
        
        finalProjectBox.getChildren().addAll(finalProjectLabel, finalProjectWeightLabel, finalProjectWeightField);
        
        // Add all to assessment box
        assessmentBox.getChildren().addAll(
            assignmentBox, quizBox, midtermBox, finalExamBox, finalProjectBox
        );
        
        // Weight total with styling
        Label weightTotalLabel = new Label("Total Weight: 0%");
        weightTotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        weightTotalLabel.setTextFill(SECONDARY_COLOR);
        
        // Styled buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));
        
        Button cancelButton = createStyledButton("Cancel", LIGHT_GRAY);
        cancelButton.setTextFill(PRIMARY_COLOR);
        cancelButton.setPrefWidth(150);
        cancelButton.setPrefHeight(50);
        cancelButton.setStyle("-fx-background-color: white; -fx-border-color: " + toRgbString(PRIMARY_COLOR) + "; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Button addButton = createStyledButton("Add Subject", ACCENT_COLOR);
        addButton.setTextFill(Color.WHITE);
        addButton.setPrefWidth(150);
        addButton.setPrefHeight(50);
        addButton.setDefaultButton(true);
        
        // Add shadow to buttons
        addButtonShadow(cancelButton);
        addButtonShadow(addButton);
        
        buttonBox.getChildren().addAll(cancelButton, addButton);
        
        dialogVBox.getChildren().addAll(
            titleLabel, nameLabel, nameField, 
            assessmentLabel, assessmentBox,
            weightTotalLabel, buttonBox
        );
        
        dialog.setScene(new javafx.scene.Scene(dialogVBox));
        
        // Update weight total when any weight field changes - unchanged
        javafx.beans.value.ChangeListener<String> weightChangeListener = (observable, oldValue, newValue) -> {
            try {
                double assignmentWeight = Double.parseDouble(assignmentWeightField.getText().trim());
                double quizWeight = Double.parseDouble(quizWeightField.getText().trim());
                double midtermWeight = Double.parseDouble(midtermWeightField.getText().trim());
                double finalExamWeight = Double.parseDouble(finalExamWeightField.getText().trim());
                double finalProjectWeight = Double.parseDouble(finalProjectWeightField.getText().trim());
                
                double totalWeight = assignmentWeight + quizWeight + midtermWeight + finalExamWeight + finalProjectWeight;
                weightTotalLabel.setText(String.format("Total Weight: %.1f%%", totalWeight));
                
                // Highlight if not 100% - unchanged logic with enhanced colors
                if (Math.abs(totalWeight - 100.0) < 0.1) {
                    weightTotalLabel.setTextFill(ACCENT_COLOR);
                } else {
                    weightTotalLabel.setTextFill(SECONDARY_COLOR);
                }
            } catch (NumberFormatException e) {
                weightTotalLabel.setText("Total Weight: Invalid input");
                weightTotalLabel.setTextFill(SECONDARY_COLOR);
            }
        };
        
        // Add the listener to all weight fields - unchanged
        assignmentWeightField.textProperty().addListener(weightChangeListener);
        quizWeightField.textProperty().addListener(weightChangeListener);
        midtermWeightField.textProperty().addListener(weightChangeListener);
        finalExamWeightField.textProperty().addListener(weightChangeListener);
        finalProjectWeightField.textProperty().addListener(weightChangeListener);
        
        // Set up event handlers - unchanged
        cancelButton.setOnAction(e -> dialog.close());
        
        // Keep original addButton action without any changes
        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Subject name cannot be empty");
                }
                
                int assignmentCount = assignmentCountCombo.getValue();
                double assignmentWeight = Double.parseDouble(assignmentWeightField.getText().trim());
                
                int quizCount = quizCountCombo.getValue();
                double quizWeight = Double.parseDouble(quizWeightField.getText().trim());
                
                double midtermWeight = Double.parseDouble(midtermWeightField.getText().trim());
                double finalExamWeight = Double.parseDouble(finalExamWeightField.getText().trim());
                double finalProjectWeight = Double.parseDouble(finalProjectWeightField.getText().trim());
                
                double totalWeight = assignmentWeight + quizWeight + midtermWeight + finalExamWeight + finalProjectWeight;
                if (Math.abs(totalWeight - 100.0) > 0.1) {
                    throw new IllegalArgumentException("Total weight must be 100%");
                }
                
                // Create assessment config
                Map<String, Object[]> assessmentConfig = new HashMap<>();
                assessmentConfig.put("assignment", new Object[] {assignmentCount, assignmentWeight});
                assessmentConfig.put("quiz", new Object[] {quizCount, quizWeight});
                assessmentConfig.put("midterm", new Object[] {midtermWeight});
                assessmentConfig.put("final_exam", new Object[] {finalExamWeight});
                assessmentConfig.put("final_project", new Object[] {finalProjectWeight});
                
                // Create subject
                Subject newSubject = controller.createSubject(name, assessmentConfig);
                
                if (newSubject != null) {
                    System.out.println("Subject created successfully: " + newSubject.getName() + ", ID: " + newSubject.getId());
                    
                    // Close the dialog
                    dialog.close();
                    
                    // Show a success message
                    showInfoAlert("Subject Added", "Subject \"" + name + "\" added successfully!");
                    
                    // IMPORTANT: Update the dashboard immediately with the new subject
                    Platform.runLater(() -> {
                        try {
                            // Direct update of the dashboard tab
                            updateDashboardTab();
                            
                            // Also update the transcript tab since it should show the new subject
                            Tab transcriptTab = tabPane.getTabs().get(1);
                            transcriptTab.setContent(createTranscriptContent());
                            
                            // Update performance tab if there are now at least 2 subjects
                            if (semester.getSubjects().size() >= 2) {
                                Tab performanceTab = tabPane.getTabs().get(2);
                                performanceTab.setContent(createPerformanceContent());
                            }
                        } catch (Exception ex) {
                            System.err.println("Error updating UI after adding subject: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    });
                } else {
                    showErrorAlert("Error", "Failed to create subject. Please try again.");
                }
            } catch (NumberFormatException ex) {
                showErrorAlert("Invalid Input", "Please enter valid numbers for weights");
            } catch (Exception ex) {
                showErrorAlert("Error", ex.getMessage());
            }
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Show dialog to edit subject weightages
     * Enhanced styling only, functionality unchanged
     * 
     * @param subject The subject to edit
     */
    private void showEditWeightagesDialog(Subject subject) {
        // Create dialog with maximized size
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Weightages - " + subject.getName());
        dialog.setMinWidth(700);
        
        VBox dialogVBox = new VBox(20);
        dialogVBox.setPadding(new Insets(30));
        dialogVBox.setStyle("-fx-background-color: #f8f8ff;");
        
        // Styled title
        Label titleLabel = new Label("Edit Weightages for " + subject.getName());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(PRIMARY_COLOR);
        
        // Create container with shadow
        StackPane gridContainer = new StackPane();
        gridContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        gridContainer.setPadding(new Insets(5));
        
        // Add shadow
        DropShadow shadow = new DropShadow();
        shadow.setRadius(8);
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setOffsetY(3);
        gridContainer.setEffect(shadow);
        
        // Create grid for weightage inputs
        GridPane weightGrid = new GridPane();
        weightGrid.setHgap(25);
        weightGrid.setVgap(20);
        weightGrid.setPadding(new Insets(25));
        
        // Headers with styling
        Label typeHeaderLabel = new Label("Assessment Type");
        typeHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        typeHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label countHeaderLabel = new Label("Count");
        countHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        countHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        Label weightHeaderLabel = new Label("Weight (%)");
        weightHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        weightHeaderLabel.setTextFill(PRIMARY_COLOR);
        
        weightGrid.add(typeHeaderLabel, 0, 0);
        weightGrid.add(countHeaderLabel, 1, 0);
        weightGrid.add(weightHeaderLabel, 2, 0);
        
        // Set column constraints for better spacing
        weightGrid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(250)); // Type column
        weightGrid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(120)); // Count column
        weightGrid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(120)); // Weight column
        
        // Add separator line
        Rectangle headerLine = new Rectangle(600, 2);
        headerLine.setFill(Color.rgb(230, 230, 240));
        weightGrid.add(headerLine, 0, 1, 3, 1);
        
        // Map to store references to UI controls - unchanged
        Map<String, TextField> weightFields = new HashMap<>();
        
        // Add rows for each assessment type
        int rowIndex = 2;
        String[] assessmentTypes = {"assignment", "quiz", "midterm", "final_exam", "final_project"};
        
        for (String type : assessmentTypes) {
            // Type name
            Label typeLabel = new Label(capitalizeAndFormat(type));
            typeLabel.setFont(Font.font("Arial", FontWeight.MEDIUM, 16));
            typeLabel.setTextFill(PRIMARY_COLOR);
            
            // Count display (cannot be edited)
            Label countLabel;
            
            // Weight input
            TextField weightField = new TextField("0");
            weightField.setPrefWidth(100);
            weightField.setPrefHeight(40);
            styleTextField(weightField);
            
            // If the assessment type exists in the subject, pre-populate its values
            application.models.AssessmentType assessmentType = subject.getAssessmentType(type);
            if (assessmentType != null) {
                countLabel = new Label(String.valueOf(assessmentType.getCount()));
                weightField.setText(String.valueOf(assessmentType.getWeight()));
            } else {
                countLabel = new Label("0");
            }
            
            countLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            
            // Store reference to the weight field
            weightFields.put(type, weightField);
            
            weightGrid.add(typeLabel, 0, rowIndex);
            weightGrid.add(countLabel, 1, rowIndex);
            weightGrid.add(weightField, 2, rowIndex);
            
            rowIndex++;
        }
        
        gridContainer.getChildren().add(weightGrid);
        
        // Weight total with styling
        Label weightTotalLabel = new Label("Total Weight: 0%");
        weightTotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        weightTotalLabel.setTextFill(SECONDARY_COLOR);
        
        // Styled buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button cancelButton = createStyledButton("Cancel", LIGHT_GRAY);
        cancelButton.setTextFill(PRIMARY_COLOR);
        cancelButton.setPrefWidth(150);
        cancelButton.setPrefHeight(50);
        cancelButton.setStyle("-fx-background-color: white; -fx-border-color: " + toRgbString(PRIMARY_COLOR) + "; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        Button saveButton = createStyledButton("Save", ACCENT_COLOR);
        saveButton.setTextFill(Color.WHITE);
        saveButton.setPrefWidth(150);
        saveButton.setPrefHeight(50);
        saveButton.setDefaultButton(true);
        
        // Add shadow to buttons
        addButtonShadow(cancelButton);
        addButtonShadow(saveButton);
        
        buttonBox.getChildren().addAll(cancelButton, saveButton);
        
        dialogVBox.getChildren().addAll(titleLabel, gridContainer, weightTotalLabel, buttonBox);
        
        dialog.setScene(new javafx.scene.Scene(dialogVBox));
        
        // Update weight total when any weight field changes - unchanged
        javafx.beans.value.ChangeListener<String> weightChangeListener = (observable, oldValue, newValue) -> {
            try {
                double totalWeight = 0.0;
                
                for (TextField field : weightFields.values()) {
                    double weight = Double.parseDouble(field.getText().trim());
                    totalWeight += weight;
                }
                
                weightTotalLabel.setText(String.format("Total Weight: %.1f%%", totalWeight));
                
                // Highlight if not 100% - enhanced colors only
                if (Math.abs(totalWeight - 100.0) < 0.1) {
                    weightTotalLabel.setTextFill(ACCENT_COLOR);
                } else {
                    weightTotalLabel.setTextFill(SECONDARY_COLOR);
                }
            } catch (NumberFormatException e) {
                weightTotalLabel.setText("Total Weight: Invalid input");
                weightTotalLabel.setTextFill(SECONDARY_COLOR);
            }
        };
        
        // Add the listener to all weight fields - unchanged
        for (TextField field : weightFields.values()) {
            field.textProperty().addListener(weightChangeListener);
        }
        
        // Trigger the listener once to initialize the total - unchanged
        weightChangeListener.changed(null, null, null);
        
        // Set up event handlers - unchanged
        cancelButton.setOnAction(e -> dialog.close());
        
        saveButton.setOnAction(e -> {
            try {
                double totalWeight = 0.0;
                
                // Validate weights - unchanged
                for (String type : assessmentTypes) {
                    TextField field = weightFields.get(type);
                    double weight = Double.parseDouble(field.getText().trim());
                    
                    if (weight < 0) {
                        throw new IllegalArgumentException("Weight cannot be negative");
                    }
                    
                    totalWeight += weight;
                }
                
                if (Math.abs(totalWeight - 100.0) > 0.1) {
                    throw new IllegalArgumentException("Total weight must be 100%");
                }
                
                // Update assessment type weights - unchanged
                boolean anyChange = false;
                
                for (String type : assessmentTypes) {
                    TextField field = weightFields.get(type);
                    double weight = Double.parseDouble(field.getText().trim());
                    
                    application.models.AssessmentType assessmentType = subject.getAssessmentType(type);
                    
                    if (assessmentType != null) {
                        if (assessmentType.getWeight() != weight) {
                            assessmentType.setWeight(weight);
                            
                            // Update in database
                            controller.updateAssessmentType(assessmentType);
                            anyChange = true;
                        }
                    } else if (weight > 0) {
                        // We can't create new assessment types here because we need their count
                        throw new IllegalArgumentException("Cannot add new assessment type. Please create a new subject.");
                    }
                }
                
                if (anyChange) {
                    // Refresh semester data
                    controller.refreshSemester();
                    
                    // Refresh view
                    initialize();
                }
                
                dialog.close();
            } catch (NumberFormatException ex) {
                showErrorAlert("Invalid Input", "Please enter valid numbers for weights");
            } catch (Exception ex) {
                showErrorAlert("Error", ex.getMessage());
            }
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Show dialog to input grades for a subject
     * Fixed to clearly show different assessment types with mark as completed checkboxes
     * 
     * @param subject The subject to input grades for
     */
    private void showInputGradesDialog(Subject subject) {
        try {
            // First, refresh the subject data to ensure we have all assessment types and assessments
            subject = controller.refreshSubjectData(subject.getId());
            
            if (subject == null) {
                showErrorAlert("Error", "Failed to load subject data.");
                return;
            }
            
            // Debugging: Print assessment types and assessments
            System.out.println("OPENING GRADE INPUT DIALOG FOR SUBJECT: " + subject.getName() + ", ID: " + subject.getId());
            System.out.println("Assessment Types: " + subject.getAssessmentTypes().size());
            
            // Manually debug all assessment types to check if they have proper weights and scores
            for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                String type = entry.getKey();
                AssessmentType assessmentType = entry.getValue();
                
                System.out.println("Type: " + type + 
                                  ", ID: " + assessmentType.getId() + 
                                  ", Weight: " + assessmentType.getWeight() + 
                                  ", Count: " + assessmentType.getCount());
                
                List<Assessment> assessments = assessmentType.getAssessments();
                System.out.println("  Assessments: " + (assessments != null ? assessments.size() : "NULL!"));
                
                if (assessments != null) {
                    for (Assessment assessment : assessments) {
                        System.out.println("    Assessment ID: " + assessment.getId() + 
                                         ", Number: " + assessment.getNumber() + 
                                         ", Score: " + assessment.getScore() + 
                                         ", Final: " + assessment.isFinal());
                    }
                }
            }
            
            // Create dialog with larger maximized size
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Input Grades - " + subject.getName());
            dialog.setMinWidth(900);
            dialog.setMinHeight(700);
            
            VBox dialogVBox = new VBox(20);
            dialogVBox.setPadding(new Insets(30));
            dialogVBox.setStyle("-fx-background-color: #f8f8ff;");
            
            // Styled title
            Label titleLabel = new Label("Input Grades for " + subject.getName());
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            titleLabel.setTextFill(PRIMARY_COLOR);
            
            // Create a tabbed pane to separate different assessment types
            TabPane assessmentTypeTabs = new TabPane();
            assessmentTypeTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            assessmentTypeTabs.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-tab-min-width: 150px; " +
                "-fx-tab-min-height: 40px;"
            );
            
            // Map to store references to UI controls - unchanged
            Map<Integer, TextField> scoreFields = new HashMap<>();
            Map<Integer, CheckBox> finalCheckboxes = new HashMap<>();
            
            boolean hasAnyAssessments = false;
            
            // Create tabs for each assessment type
            for (Map.Entry<String, AssessmentType> entry : subject.getAssessmentTypes().entrySet()) {
                AssessmentType assessmentType = entry.getValue();
                String typeName = entry.getKey();
                
                // Skip if weight is 0
                if (assessmentType.getWeight() <= 0) {
                    System.out.println("Skipping type with zero weight: " + typeName);
                    continue;
                }
                
                List<Assessment> assessments = assessmentType.getAssessments();
                
                if (assessments == null || assessments.isEmpty()) {
                    System.out.println("No assessments found for type: " + typeName);
                    continue;
                }
                
                hasAnyAssessments = true;
                
                // Create tab for this assessment type
                Tab typeTab = new Tab(assessmentType.getDisplayName() + " (" + assessmentType.getWeight() + "%)");
                typeTab.setStyle("-fx-font-size: 16px;");
                
                // Create styled container for assessment grid
                StackPane typeContainer = new StackPane();
                typeContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
                typeContainer.setPadding(new Insets(5));
                
                // Add shadow
                DropShadow containerShadow = new DropShadow();
                containerShadow.setRadius(8);
                containerShadow.setColor(Color.rgb(0, 0, 0, 0.15));
                containerShadow.setOffsetY(3);
                typeContainer.setEffect(containerShadow);
                
                // Create grid for assessments of this type
                GridPane assessmentGrid = new GridPane();
                assessmentGrid.setHgap(25);
                assessmentGrid.setVgap(20);
                assessmentGrid.setPadding(new Insets(25));
                
                // Headers with styling
                Label assessmentNumberLabel = new Label("Assessment");
                assessmentNumberLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                assessmentNumberLabel.setTextFill(PRIMARY_COLOR);
                
                Label scoreLabel = new Label("Score (%)");
                scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                scoreLabel.setTextFill(PRIMARY_COLOR);
                
                Label finalizedLabel = new Label("Mark as Final");
                finalizedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                finalizedLabel.setTextFill(PRIMARY_COLOR);
                
                assessmentGrid.add(assessmentNumberLabel, 0, 0);
                assessmentGrid.add(scoreLabel, 1, 0);
                assessmentGrid.add(finalizedLabel, 2, 0);
                
                // Set column constraints for better layout
                assessmentGrid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(250)); // Assessment column
                assessmentGrid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(150)); // Score column
                assessmentGrid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(150)); // Mark as Final column
                
                // Add header separator
                Rectangle headerLine = new Rectangle(700, 2);
                headerLine.setFill(Color.rgb(230, 230, 240));
                assessmentGrid.add(headerLine, 0, 1, 3, 1);
                
                // Sort assessments by number
                List<Assessment> sortedAssessments = new ArrayList<>(assessments);
                sortedAssessments.sort((a1, a2) -> Integer.compare(a1.getNumber(), a2.getNumber()));
                
                int rowIndex = 2;
                for (Assessment assessment : sortedAssessments) {
                    // Assessment name
                    String displayName = assessment.getDisplayName(assessmentType.getType());
                    Label nameLabel = new Label(displayName);
                    nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                    
                    // Score input - show current score if available
                    TextField scoreField = new TextField(String.format("%.1f", assessment.getScore()));
                    scoreField.setPrefWidth(120);
                    scoreField.setPrefHeight(40);
                    styleTextField(scoreField);
                    
                    // Store reference to the field
                    scoreFields.put(assessment.getId(), scoreField);
                    
                    // Finalized checkbox - set to current value
                    CheckBox finalizedCheckbox = new CheckBox("Completed");
                    finalizedCheckbox.setSelected(assessment.isFinal());
                    finalizedCheckbox.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                    
                    // Store reference to the checkbox
                    finalCheckboxes.put(assessment.getId(), finalizedCheckbox);
                    
                    // Add to grid with alternating row colors
                    if (rowIndex % 2 == 0) {
                        Rectangle rowBg = new Rectangle(700, 50);
                        rowBg.setFill(Color.rgb(248, 248, 255, 0.5));
                        rowBg.setArcWidth(5);
                        rowBg.setArcHeight(5);
                        assessmentGrid.add(rowBg, 0, rowIndex, 3, 1);
                    }
                    
                    assessmentGrid.add(nameLabel, 0, rowIndex);
                    assessmentGrid.add(scoreField, 1, rowIndex);
                    assessmentGrid.add(finalizedCheckbox, 2, rowIndex);
                    
                    rowIndex++;
                }
                
                typeContainer.getChildren().add(assessmentGrid);
                typeTab.setContent(typeContainer);
                assessmentTypeTabs.getTabs().add(typeTab);
            }
            
            // Create styled container for the tabs
            StackPane tabsContainer = new StackPane();
            tabsContainer.setStyle("-fx-background-color: transparent;");
            tabsContainer.setPrefHeight(400);
            
            // Check if we have any assessments
            if (!hasAnyAssessments) {
                // Show message when no assessments
                StackPane emptyContainer = new StackPane();
                emptyContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
                emptyContainer.setPadding(new Insets(40));
                
                Label noAssessmentsLabel = new Label("No assessments found with weight > 0. Please check subject configuration.");
                noAssessmentsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                noAssessmentsLabel.setTextFill(Color.rgb(100, 100, 120));
                
                emptyContainer.getChildren().add(noAssessmentsLabel);
                
                // Add shadow to empty container
                DropShadow emptyContainerShadow = new DropShadow();
                emptyContainerShadow.setRadius(8);
                emptyContainerShadow.setColor(Color.rgb(0, 0, 0, 0.15));
                emptyContainerShadow.setOffsetY(3);
                emptyContainer.setEffect(emptyContainerShadow);
                
                tabsContainer.getChildren().add(emptyContainer);
            } else {
                tabsContainer.getChildren().add(assessmentTypeTabs);
            }
            
            // Calculate current values - unchanged
            double percentage = subject.calculateOverallPercentage();
            String letterGrade = subject.calculateLetterGrade();
            double gpa = subject.calculateGPA();
            
            // Create styled summary panel
            StackPane summaryPane = new StackPane();
            summaryPane.setPadding(new Insets(20));
            summaryPane.setStyle(
                "-fx-background-color: #f8f8ff; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8;"
            );
            
            // Create grade circle
            StackPane gradeCircle = new StackPane();
            Circle circle = new Circle(30);
            
            // Color based on grade
            Color circleColor;
            if (gpa >= 3.7) {
                circleColor = ACCENT_COLOR;
            } else if (gpa >= 3.0) {
                circleColor = Color.rgb(66, 133, 244);
            } else if (gpa >= 2.0) {
                circleColor = Color.rgb(251, 188, 5);
            } else {
                circleColor = SECONDARY_COLOR;
            }
            
            circle.setFill(circleColor);
            
            // Grade letter inside circle
            Label letterLabel = new Label(letterGrade);
            letterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            letterLabel.setTextFill(Color.WHITE);
            
            gradeCircle.getChildren().addAll(circle, letterLabel);
            
            // Current overall label
            Label currentOverallLabel = new Label(String.format(
                "Current Overall: %.1f%% | GPA: %.2f",
                percentage,
                gpa
            ));
            currentOverallLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            currentOverallLabel.setTextFill(PRIMARY_COLOR);
            
            HBox summaryBox = new HBox(20);
            summaryBox.setAlignment(Pos.CENTER);
            summaryBox.getChildren().addAll(gradeCircle, currentOverallLabel);
            
            summaryPane.getChildren().add(summaryBox);
            
            // Create button bar with styling
            HBox buttonBox = new HBox(20);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setPadding(new Insets(20, 0, 0, 0));
            
            Button cancelButton = createStyledButton("Cancel", LIGHT_GRAY);
            cancelButton.setTextFill(PRIMARY_COLOR);
            cancelButton.setPrefWidth(150);
            cancelButton.setPrefHeight(50);
            cancelButton.setStyle("-fx-background-color: white; -fx-border-color: " + toRgbString(PRIMARY_COLOR) + "; -fx-border-radius: 5; -fx-background-radius: 5;");
            
            Button saveButton = createStyledButton("Save", ACCENT_COLOR);
            saveButton.setTextFill(Color.WHITE);
            saveButton.setPrefWidth(150);
            saveButton.setPrefHeight(50);
            saveButton.setDefaultButton(true);
            
            // Add shadow to buttons
            addButtonShadow(cancelButton);
            addButtonShadow(saveButton);
            
            buttonBox.getChildren().addAll(cancelButton, saveButton);
            
            // Add all components to dialog
            dialogVBox.getChildren().addAll(titleLabel, tabsContainer, summaryPane, buttonBox);
            
            dialog.setScene(new javafx.scene.Scene(dialogVBox));
            
            // Create grade input handler - unchanged
            application.utils.GradeInputHandler gradeHandler = 
                new application.utils.GradeInputHandler(subject, controller, scoreFields, finalCheckboxes, dialog);
            
            // Set up event handlers - unchanged
            cancelButton.setOnAction(e -> dialog.close());
            
            saveButton.setOnAction(e -> {
                if (gradeHandler.saveGrades()) {
                    // Successfully saved, refresh view
                    try {
                        initialize();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorAlert("Error", "Failed to refresh view: " + ex.getMessage());
                    }
                }
            });
            
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to display input grades dialog: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to capitalize and format assessment type names
     * Unchanged
     * 
     * @param type The assessment type
     * @return The formatted name
     */
    private String capitalizeAndFormat(String type) {
        if (type == null || type.isEmpty()) {
            return type;
        }
        
        String formatted = type.replace("_", " ");
        return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
    }
    
    /**
     * Style a text field with modern appearance
     * New helper method for styling
     * 
     * @param textField The text field to style
     */
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
        
        // Add subtle inner shadow
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(2);
        innerShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        innerShadow.setOffsetX(1);
        innerShadow.setOffsetY(1);
        textField.setEffect(innerShadow);
        
        // Add focus style
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
    
    /**
     * Creates a styled button with the specified text and background color
     * New helper method for styling
     * 
     * @param text The button text
     * @param bgColor The background color
     * @return The styled button
     */
    private Button createStyledButton(String text, Color bgColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Convert color to CSS format
        String colorString = toRgbString(bgColor);
        
        // Style the button
        button.setStyle(
            "-fx-background-color: " + colorString + "; " +
            "-fx-background-radius: 5; "
        );
        
        // Add hover effect
        Color hoverColor = bgColor.darker();
        String hoverColorString = toRgbString(hoverColor);
        
        button.setOnMouseEntered(e -> 
            button.setStyle(
                button.getStyle().replace(colorString, hoverColorString)
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                button.getStyle().replace(hoverColorString, colorString)
            )
        );
        
        return button;
    }
    
    /**
     * Add shadow to a button
     * New helper method for styling
     * 
     * @param button The button to add shadow to
     */
    private void addButtonShadow(Button button) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        button.setEffect(shadow);
    }
    
    /**
     * Convert a JavaFX Color to an RGB string for CSS
     * New helper method for styling
     * 
     * @param color The color
     * @return The RGB string
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
     * Show an error alert with enhanced styling
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
     * Show an information alert with enhanced styling
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
     * Get the main layout of this view
     * Unchanged
     * 
     * @return The BorderPane containing the view
     */
    public BorderPane getView() {
        return mainLayout;
    }

    /**
     * Refresh the view with updated data
     * Unchanged
     */
    private void refreshView() {
        try {
            System.out.println("Refreshing SemesterView");
            
            // Re-initialize the entire view
            initialize();
            
            // Update any specific elements if needed
            // This will depend on your specific UI implementation
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error refreshing view: " + e.getMessage());
        }
    }

    /**
     * Sets the application to full screen mode
     * This method should be called after the stage is shown.
     * @param stage The primary stage of the application
     */
    public void setFullScreen(javafx.stage.Stage stage) {
        stage.setMaximized(true);
    }
}