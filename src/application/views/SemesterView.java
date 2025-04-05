package application.views;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;

import application.controllers.SemesterController;
import application.models.Assessment;
import application.models.AssessmentType;
import application.models.Semester;
import application.models.Subject;

/**
 * View class for the semester details screen
 */
public class SemesterView {
    private BorderPane mainLayout;
    private SemesterController controller;
    private Semester semester;
    private TabPane tabPane;
    
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
     * Initialize the view
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
            mainLayout.setPadding(new Insets(20));
            
            // Create header
            Label headerLabel = new Label(semester.getName());
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            
            // Create a GPA label
            Label gpaLabel = new Label(String.format("GPA: %.2f", semesterGpa));
            gpaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            
            HBox headerBox = new HBox(20);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            
            VBox titleBox = new VBox(5);
            titleBox.getChildren().addAll(headerLabel, gpaLabel);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button backButton = new Button("Back to Home");
            Button addSubjectButton = new Button("Add Subject");
            Button analyticsButton = new Button("Analytics");
            
            headerBox.getChildren().addAll(titleBox, spacer, analyticsButton, addSubjectButton, backButton);
            mainLayout.setTop(headerBox);
            BorderPane.setMargin(headerBox, new Insets(0, 0, 20, 0));
            
            // Create a new tab pane
            tabPane = new TabPane();
            
            // Dashboard tab
            Tab dashboardTab = new Tab("Dashboard");
            dashboardTab.setClosable(false);
            VBox dashboardContent = createDashboardContent();
            dashboardTab.setContent(dashboardContent);
            
            // Transcript tab
            Tab transcriptTab = new Tab("Transcript");
            transcriptTab.setClosable(false);
            VBox transcriptContent = createTranscriptContent();
            transcriptTab.setContent(transcriptContent);
            
            // Performance tab
            Tab performanceTab = new Tab("Performance");
            performanceTab.setClosable(false);
            VBox performanceContent = createPerformanceContent();
            performanceTab.setContent(performanceContent);
            
            tabPane.getTabs().addAll(dashboardTab, transcriptTab, performanceTab);
            mainLayout.setCenter(tabPane);
            
            // Set up event handlers
            backButton.setOnAction(e -> controller.navigateToHome());
            addSubjectButton.setOnAction(e -> showAddSubjectDialog());
            analyticsButton.setOnAction(e -> controller.navigateToAnalytics());
            
            // Register for data change events
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
                        dashboardTab.setContent(createDashboardContent());
                        transcriptTab.setContent(createTranscriptContent());
                        performanceTab.setContent(createPerformanceContent());
                        
                        // Update GPA in header
                        gpaLabel.setText(String.format("GPA: %.2f", semester.calculateGPA()));
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
     * 
     * @return The dashboard content
     */
    private VBox createDashboardContent() {
        System.out.println("\n===== CREATING DASHBOARD TAB WITH FRESH DATA =====");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        try {
            // Ensure we have fresh data
            controller.refreshSemester();
            semester = controller.getSemester();
            
            List<Subject> subjects = semester.getSubjects();
            System.out.println("Dashboard: Found " + subjects.size() + " subjects for semester ID " + semester.getId());
            
            if (subjects.isEmpty()) {
                Label noSubjectsLabel = new Label("No subjects added yet. Click 'Add Subject' to get started.");
                noSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                content.getChildren().add(noSubjectsLabel);
            } else {
                // For each subject, get fresh data and recalculate values
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
                    
                    // Create card with the fresh data
                    VBox subjectCard = createSubjectCard(subject);
                    content.getChildren().add(subjectCard);
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating dashboard content: " + e.getMessage());
            e.printStackTrace();
            
            Label errorLabel = new Label("Error loading subjects: " + e.getMessage());
            errorLabel.setTextFill(javafx.scene.paint.Color.RED);
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
            for (javafx.scene.Node node : ((HBox)mainLayout.getTop()).getChildren()) {
                if (node instanceof VBox) {
                    VBox titleBox = (VBox)node;
                    for (javafx.scene.Node titleNode : titleBox.getChildren()) {
                        if (titleNode instanceof Label && ((Label)titleNode).getText().startsWith("GPA:")) {
                            ((Label)titleNode).setText(String.format("GPA: %.2f", semesterGPA));
                            System.out.println("Updated header GPA label to: " + semesterGPA);
                            break;
                        }
                    }
                    break;
                }
            }
            
            // Refresh the dashboard tab content
            Tab dashboardTab = tabPane.getTabs().get(0);
            VBox newContent = createDashboardContent();
            dashboardTab.setContent(newContent);
            
            System.out.println("===== DASHBOARD TAB UPDATE COMPLETE =====\n");
        } catch (Exception e) {
            System.err.println("Error updating dashboard tab: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create a visual card for a subject with fresh calculations
     * 
     * @param subject The subject
     * @return The VBox representing the subject card
     */
    private VBox createSubjectCard(Subject subject) {
        System.out.println("Creating card for subject: " + subject.getName() + ", ID: " + subject.getId());
        
        // Force recalculation of values
        double percentage = subject.calculateOverallPercentage();
        String letterGrade = subject.calculateLetterGrade();
        double gpa = subject.calculateGPA();
        
        System.out.println("  Current values - Percentage: " + percentage + 
                         ", Grade: " + letterGrade + 
                         ", GPA: " + gpa);
        
        VBox card = new VBox(15);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        // Header with subject name and delete button
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(subject.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        
        headerBox.getChildren().addAll(nameLabel, spacer, deleteButton);
        
        // Create grade label with fresh calculations
        Label gradeLabel = new Label(String.format("%.1f%% | %s | GPA: %.1f", percentage, letterGrade, gpa));
        gradeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        // Progress section
        VBox progressBox = new VBox(10);
        
        // Goal section
        HBox goalBox = new HBox(10);
        goalBox.setAlignment(Pos.CENTER_LEFT);
        
        Label goalLabel = new Label("Goal:");
        TextField goalField = new TextField(String.valueOf(subject.getGoalPercentage()));
        goalField.setPrefWidth(80);
        
        Button calculateButton = new Button("Calculate Required Scores");
        
        goalBox.getChildren().addAll(goalLabel, goalField, calculateButton);
        
        // Required scores section - use VBox instead of single label
        VBox requiredScoresBox = new VBox(5);
        Label requiredScoresHeaderLabel = new Label("Required Scores for Remaining Assessments:");
        requiredScoresHeaderLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        requiredScoresBox.getChildren().add(requiredScoresHeaderLabel);
        
        // Add this initially - will be updated when Calculate button is clicked
        Label initialMessageLabel = new Label("Enter a goal percentage and click Calculate.");
        initialMessageLabel.setStyle("-fx-font-style: italic;");
        requiredScoresBox.getChildren().add(initialMessageLabel);
        
        progressBox.getChildren().addAll(goalBox, requiredScoresBox);
        
        // Button actions
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button editButton = new Button("Edit Weightages");
        Button inputGradesButton = new Button("Input Grades");
        
        buttonBox.getChildren().addAll(editButton, inputGradesButton);
        
        card.getChildren().addAll(headerBox, gradeLabel, new Separator(), progressBox, buttonBox);
        
        // Set up event handlers
        editButton.setOnAction(e -> showEditWeightagesDialog(subject));
        inputGradesButton.setOnAction(e -> showInputGradesDialog(subject));
        
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
                    unachievableLabel.setStyle("-fx-text-fill: red;");
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
                        
                        // Color-code scores based on difficulty
                        if (requiredScore > 95) {
                            scoreLabel.setStyle("-fx-text-fill: #cc0000; -fx-font-weight: bold;"); // Red - very challenging
                        } else if (requiredScore > 90) {
                            scoreLabel.setStyle("-fx-text-fill: #ff8c00; -fx-font-weight: bold;"); // Dark orange - difficult
                        } else if (requiredScore > 80) {
                            scoreLabel.setStyle("-fx-text-fill: #ff8c00;"); // Orange - moderately difficult
                        } else if (requiredScore == 100.0) {
                            // Special case for "max out this area"
                            scoreLabel.setStyle("-fx-text-fill: #006400; -fx-font-weight: bold;"); // Dark green
                        }
                        
                        requiredScoresBox.getChildren().add(scoreLabel);
                    }
                }
            } catch (NumberFormatException ex) {
                // Invalid goal input
                requiredScoresBox.getChildren().clear();
                requiredScoresBox.getChildren().add(requiredScoresHeaderLabel);
                
                Label errorLabel = new Label("Please enter a valid goal percentage.");
                errorLabel.setStyle("-fx-text-fill: red;");
                requiredScoresBox.getChildren().add(errorLabel);
            }
        });
        
        // Set up delete button event handler
        deleteButton.setOnAction(e -> showDeleteSubjectConfirmation(subject));
        
        return card;
    }
    
    /**
     * Show a confirmation dialog before deleting a subject
     * 
     * @param subject The subject to delete
     */
    private void showDeleteSubjectConfirmation(Subject subject) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Subject");
        alert.setHeaderText("Are you sure you want to delete " + subject.getName() + "?");
        alert.setContentText("This action cannot be undone. All grades and assessments for this subject will be permanently deleted.");
        
        // Customize the buttons
        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(deleteButton, cancelButton);
        
        // Make the delete button red
        Button deleteButtonObj = (Button) alert.getDialogPane().lookupButton(deleteButton);
        deleteButtonObj.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
        
        // Show the dialog and handle the result
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == deleteButton) {
                try {
                    // Delete the subject
                    controller.deleteSubject(subject.getId());
                    
                    // Refresh the view
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
     * 
     * @return The transcript content
     */
    private VBox createTranscriptContent() {
        System.out.println("\n===== CREATING TRANSCRIPT TAB =====");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Semester Transcript");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Headers
        Label subjectHeaderLabel = new Label("Subject");
        subjectHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label percentageHeaderLabel = new Label("Percentage");
        percentageHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label letterGradeHeaderLabel = new Label("Letter Grade");
        letterGradeHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label gpaHeaderLabel = new Label("GPA");
        gpaHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        grid.add(subjectHeaderLabel, 0, 0);
        grid.add(percentageHeaderLabel, 1, 0);
        grid.add(letterGradeHeaderLabel, 2, 0);
        grid.add(gpaHeaderLabel, 3, 0);
        
        // Make sure we have the freshest data
        try {
            controller.refreshSemester();
            List<Subject> subjects = semester.getSubjects();
            
            System.out.println("Transcript: Refreshed semester data");
            System.out.println("Transcript: Number of subjects: " + subjects.size());
            
            if (subjects.isEmpty()) {
                Label noSubjectsLabel = new Label("No subjects added yet.");
                noSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                grid.add(noSubjectsLabel, 0, 1, 4, 1);
            } else {
                double semesterTotal = 0.0;
                int validSubjectCount = 0;
                
                for (int i = 0; i < subjects.size(); i++) {
                    Subject subject = subjects.get(i);
                    
                    // Make sure we have fresh data
                    Subject refreshedSubject = controller.refreshSubjectData(subject.getId());
                    if (refreshedSubject != null) {
                        subject = refreshedSubject;
                    }
                    
                    // Force recalculation of values
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
                    
                    Label subjectLabel = new Label(subject.getName());
                    Label percentageLabel = new Label(String.format("%.1f%%", percentage));
                    Label letterGradeLabel = new Label(letterGrade);
                    Label gpaLabel = new Label(String.format("%.1f", gpa));
                    
                    grid.add(subjectLabel, 0, i + 1);
                    grid.add(percentageLabel, 1, i + 1);
                    grid.add(letterGradeLabel, 2, i + 1);
                    grid.add(gpaLabel, 3, i + 1);
                }
                
                // Add semester GPA
                grid.add(new Separator(), 0, subjects.size() + 1, 4, 1);
                
                Label overallLabel = new Label("Overall GPA");
                overallLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                
                // Calculate overall GPA
                double overallGPA = validSubjectCount > 0 ? semesterTotal / validSubjectCount : 0.0;
                System.out.println("Transcript: Calculated overall GPA: " + overallGPA);
                
                Label overallGpaLabel = new Label(String.format("%.2f", overallGPA));
                overallGpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                
                grid.add(overallLabel, 0, subjects.size() + 2);
                grid.add(overallGpaLabel, 3, subjects.size() + 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error refreshing transcript data: " + e.getMessage());
            
            Label errorLabel = new Label("Error loading data: " + e.getMessage());
            errorLabel.setTextFill(javafx.scene.paint.Color.RED);
            grid.add(errorLabel, 0, 1, 4, 1);
        }
        
        content.getChildren().addAll(titleLabel, grid);
        
        System.out.println("===== TRANSCRIPT TAB CREATION COMPLETE =====\n");
        return content;
    }
    
    /**
     * Create content for the performance tab with fresh data
     * 
     * @return The performance content
     */
    private VBox createPerformanceContent() {
        System.out.println("\n===== CREATING PERFORMANCE TAB WITH FRESH DATA =====");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Subject Performance Comparison");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        try {
            // Ensure we have fresh data
            controller.refreshSemester();
            semester = controller.getSemester();
            
            List<Subject> subjects = semester.getSubjects();
            System.out.println("Performance: Found " + subjects.size() + " subjects");
            
            // For each subject, refresh data and recalculate
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
                Label notEnoughSubjectsLabel = new Label("Need at least 2 subjects to compare performance.");
                notEnoughSubjectsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                content.getChildren().addAll(titleLabel, notEnoughSubjectsLabel);
            } else {
                // Create a TabPane for different chart types
                TabPane chartTabPane = new TabPane();
                chartTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                
                // Percentage comparison tab
                Tab percentageTab = new Tab("Percentage Comparison");
                VBox percentageContent = new VBox(15);
                percentageContent.setPadding(new Insets(15));
                
                // Create bar chart for percentages
                javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
                javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis(0, 100, 10);
                xAxis.setLabel("Subject");
                yAxis.setLabel("Percentage");
                
                javafx.scene.chart.BarChart<String, Number> percentageChart = 
                    new javafx.scene.chart.BarChart<>(xAxis, yAxis);
                percentageChart.setTitle("Overall Percentage Comparison");
                
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
                
                // GPA comparison tab
                Tab gpaTab = new Tab("GPA Comparison");
                VBox gpaContent = new VBox(15);
                gpaContent.setPadding(new Insets(15));
                
                // Create bar chart for GPAs
                javafx.scene.chart.CategoryAxis gpaXAxis = new javafx.scene.chart.CategoryAxis();
                javafx.scene.chart.NumberAxis gpaYAxis = new javafx.scene.chart.NumberAxis(0, 4, 0.5);
                gpaXAxis.setLabel("Subject");
                gpaYAxis.setLabel("GPA");
                
                javafx.scene.chart.BarChart<String, Number> gpaChart = 
                    new javafx.scene.chart.BarChart<>(gpaXAxis, gpaYAxis);
                gpaChart.setTitle("GPA Comparison");
                
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
                
                // Add comparison table tab
                Tab tableTab = new Tab("Detailed Comparison");
                VBox tableContent = new VBox(15);
                tableContent.setPadding(new Insets(15));
                
                // Create grid for the table
                GridPane comparisonGrid = new GridPane();
                comparisonGrid.setHgap(20);
                comparisonGrid.setVgap(10);
                comparisonGrid.setPadding(new Insets(10));
                
                // Headers
                Label metricHeaderLabel = new Label("Metric");
                metricHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                comparisonGrid.add(metricHeaderLabel, 0, 0);
                
                for (int i = 0; i < subjects.size(); i++) {
                    Label subjectHeaderLabel = new Label(subjects.get(i).getName());
                    subjectHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    comparisonGrid.add(subjectHeaderLabel, i + 1, 0);
                }
                
                // Metrics rows
                String[] metrics = {"Overall Percentage", "Letter Grade", "GPA"};
                
                for (int i = 0; i < metrics.length; i++) {
                    Label metricLabel = new Label(metrics[i]);
                    comparisonGrid.add(metricLabel, 0, i + 1);
                    
                    for (int j = 0; j < subjects.size(); j++) {
                        Subject subject = subjects.get(j);
                        String value = "";
                        
                        switch (i) {
                            case 0: // Overall Percentage
                                value = String.format("%.1f%%", subject.calculateOverallPercentage());
                                break;
                            case 1: // Letter Grade
                                value = subject.calculateLetterGrade();
                                break;
                            case 2: // GPA
                                value = String.format("%.1f", subject.calculateGPA());
                                break;
                        }
                        
                        Label valueLabel = new Label(value);
                        comparisonGrid.add(valueLabel, j + 1, i + 1);
                    }
                }
                
                tableContent.getChildren().add(comparisonGrid);
                tableTab.setContent(tableContent);
                
                // Add all tabs
                chartTabPane.getTabs().addAll(percentageTab, gpaTab, tableTab);
                
                content.getChildren().addAll(titleLabel, chartTabPane);
            }
        } catch (Exception e) {
            System.err.println("Error creating performance content: " + e.getMessage());
            e.printStackTrace();
            
            Label errorLabel = new Label("Error loading performance data: " + e.getMessage());
            errorLabel.setTextFill(javafx.scene.paint.Color.RED);
            content.getChildren().addAll(titleLabel, errorLabel);
        }
        
        System.out.println("===== PERFORMANCE TAB CREATION COMPLETE =====\n");
        return content;
    }
    
    /**
     * Show dialog to add a new subject
     */
    private void showAddSubjectDialog() {
        if (semester.getSubjects().size() >= 2) {
            showErrorAlert("Maximum Subjects", "You can only add up to 2 subjects per semester.");
            return;
        }
        
        // Create dialog
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Subject");
        dialog.setMinWidth(500);
        dialog.setMinHeight(600);
        
        VBox dialogVBox = new VBox(20);
        dialogVBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Add New Subject");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Subject name
        Label nameLabel = new Label("Subject Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., CS5010 - Programming Design Paradigm");
        
        // Assessment types
        Label assessmentLabel = new Label("Assessment Types:");
        assessmentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Assignments
        HBox assignmentBox = new HBox(10);
        assignmentBox.setAlignment(Pos.CENTER_LEFT);
        
        Label assignmentLabel = new Label("Assignments:");
        ComboBox<Integer> assignmentCountCombo = new ComboBox<>();
        for (int i = 0; i <= 10; i++) {
            assignmentCountCombo.getItems().add(i);
        }
        assignmentCountCombo.setValue(0);
        
        Label assignmentWeightLabel = new Label("Weight (%):");
        TextField assignmentWeightField = new TextField("0");
        assignmentWeightField.setPrefWidth(80);
        
        assignmentBox.getChildren().addAll(assignmentLabel, assignmentCountCombo, assignmentWeightLabel, assignmentWeightField);
        
        // Quizzes
        HBox quizBox = new HBox(10);
        quizBox.setAlignment(Pos.CENTER_LEFT);
        
        Label quizLabel = new Label("Quizzes:");
        ComboBox<Integer> quizCountCombo = new ComboBox<>();
        for (int i = 0; i <= 10; i++) {
            quizCountCombo.getItems().add(i);
        }
        quizCountCombo.setValue(0);
        
        Label quizWeightLabel = new Label("Weight (%):");
        TextField quizWeightField = new TextField("0");
        quizWeightField.setPrefWidth(80);
        
        quizBox.getChildren().addAll(quizLabel, quizCountCombo, quizWeightLabel, quizWeightField);
        
        // Midterm
        HBox midtermBox = new HBox(10);
        midtermBox.setAlignment(Pos.CENTER_LEFT);
        
        Label midtermLabel = new Label("Midterm:");
        midtermLabel.setPrefWidth(80);
        
        Label midtermWeightLabel = new Label("Weight (%):");
        TextField midtermWeightField = new TextField("0");
        midtermWeightField.setPrefWidth(80);
        
        midtermBox.getChildren().addAll(midtermLabel, midtermWeightLabel, midtermWeightField);
        
        // Final Exam
        HBox finalExamBox = new HBox(10);
        finalExamBox.setAlignment(Pos.CENTER_LEFT);
        
        Label finalExamLabel = new Label("Final Exam:");
        finalExamLabel.setPrefWidth(80);
        
        Label finalExamWeightLabel = new Label("Weight (%):");
        TextField finalExamWeightField = new TextField("0");
        finalExamWeightField.setPrefWidth(80);
        
        finalExamBox.getChildren().addAll(finalExamLabel, finalExamWeightLabel, finalExamWeightField);
        
        // Final Project
        HBox finalProjectBox = new HBox(10);
        finalProjectBox.setAlignment(Pos.CENTER_LEFT);
        
        Label finalProjectLabel = new Label("Final Project:");
        finalProjectLabel.setPrefWidth(80);
        
        Label finalProjectWeightLabel = new Label("Weight (%):");
        TextField finalProjectWeightField = new TextField("0");
        finalProjectWeightField.setPrefWidth(80);
        
        finalProjectBox.getChildren().addAll(finalProjectLabel, finalProjectWeightLabel, finalProjectWeightField);
        
        // Weight total
        Label weightTotalLabel = new Label("Total Weight: 0%");
        weightTotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelButton = new Button("Cancel");
        Button addButton = new Button("Add Subject");
        addButton.setDefaultButton(true);
        
        buttonBox.getChildren().addAll(cancelButton, addButton);
        
        dialogVBox.getChildren().addAll(
            titleLabel, nameLabel, nameField, 
            assessmentLabel, assignmentBox, quizBox, midtermBox, finalExamBox, finalProjectBox,
            weightTotalLabel, buttonBox
        );
        
        dialog.setScene(new javafx.scene.Scene(dialogVBox));
        
        // Update weight total when any weight field changes
        javafx.beans.value.ChangeListener<String> weightChangeListener = (observable, oldValue, newValue) -> {
            try {
                double assignmentWeight = Double.parseDouble(assignmentWeightField.getText().trim());
                double quizWeight = Double.parseDouble(quizWeightField.getText().trim());
                double midtermWeight = Double.parseDouble(midtermWeightField.getText().trim());
                double finalExamWeight = Double.parseDouble(finalExamWeightField.getText().trim());
                double finalProjectWeight = Double.parseDouble(finalProjectWeightField.getText().trim());
                
                double totalWeight = assignmentWeight + quizWeight + midtermWeight + finalExamWeight + finalProjectWeight;
                weightTotalLabel.setText(String.format("Total Weight: %.1f%%", totalWeight));
                
                // Highlight if not 100%
                if (Math.abs(totalWeight - 100.0) < 0.1) {
                    weightTotalLabel.setStyle("-fx-text-fill: green;");
                } else {
                    weightTotalLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (NumberFormatException e) {
                weightTotalLabel.setText("Total Weight: Invalid input");
                weightTotalLabel.setStyle("-fx-text-fill: red;");
            }
        };
        
        assignmentWeightField.textProperty().addListener(weightChangeListener);
        quizWeightField.textProperty().addListener(weightChangeListener);
        midtermWeightField.textProperty().addListener(weightChangeListener);
        finalExamWeightField.textProperty().addListener(weightChangeListener);
        finalProjectWeightField.textProperty().addListener(weightChangeListener);
        
        // Set up event handlers
        cancelButton.setOnAction(e -> dialog.close());
        
     // Inside the showAddSubjectDialog method, update the addButton.setOnAction:

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
     * 
     * @param subject The subject to edit
     */
    private void showEditWeightagesDialog(Subject subject) {
        // Create dialog
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Weightages - " + subject.getName());
        dialog.setMinWidth(500);
        
        VBox dialogVBox = new VBox(20);
        dialogVBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Edit Weightages for " + subject.getName());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Create grid for weightage inputs
        GridPane weightGrid = new GridPane();
        weightGrid.setHgap(15);
        weightGrid.setVgap(15);
        weightGrid.setPadding(new Insets(10));
        
        // Headers
        Label typeHeaderLabel = new Label("Assessment Type");
        typeHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label countHeaderLabel = new Label("Count");
        countHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label weightHeaderLabel = new Label("Weight (%)");
        weightHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        weightGrid.add(typeHeaderLabel, 0, 0);
        weightGrid.add(countHeaderLabel, 1, 0);
        weightGrid.add(weightHeaderLabel, 2, 0);
        
        // Map to store references to UI controls
        Map<String, TextField> weightFields = new HashMap<>();
        
        // Add rows for each assessment type
        int rowIndex = 1;
        String[] assessmentTypes = {"assignment", "quiz", "midterm", "final_exam", "final_project"};
        
        for (String type : assessmentTypes) {
            // Type name
            Label typeLabel = new Label(capitalizeAndFormat(type));
            
            // Count display (cannot be edited)
            Label countLabel;
            
            // Weight input
            TextField weightField = new TextField("0");
            weightField.setPrefWidth(80);
            
            // If the assessment type exists in the subject, pre-populate its values
            application.models.AssessmentType assessmentType = subject.getAssessmentType(type);
            if (assessmentType != null) {
                countLabel = new Label(String.valueOf(assessmentType.getCount()));
                weightField.setText(String.valueOf(assessmentType.getWeight()));
            } else {
                countLabel = new Label("0");
            }
            
            // Store reference to the weight field
            weightFields.put(type, weightField);
            
            weightGrid.add(typeLabel, 0, rowIndex);
            weightGrid.add(countLabel, 1, rowIndex);
            weightGrid.add(weightField, 2, rowIndex);
            
            rowIndex++;
        }
        
        // Weight total
        Label weightTotalLabel = new Label("Total Weight: 0%");
        weightTotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelButton = new Button("Cancel");
        Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        
        buttonBox.getChildren().addAll(cancelButton, saveButton);
        
        dialogVBox.getChildren().addAll(titleLabel, weightGrid, weightTotalLabel, buttonBox);
        
        dialog.setScene(new javafx.scene.Scene(dialogVBox));
        
        // Update weight total when any weight field changes
        javafx.beans.value.ChangeListener<String> weightChangeListener = (observable, oldValue, newValue) -> {
            try {
                double totalWeight = 0.0;
                
                for (TextField field : weightFields.values()) {
                    double weight = Double.parseDouble(field.getText().trim());
                    totalWeight += weight;
                }
                
                weightTotalLabel.setText(String.format("Total Weight: %.1f%%", totalWeight));
                
                // Highlight if not 100%
                if (Math.abs(totalWeight - 100.0) < 0.1) {
                    weightTotalLabel.setStyle("-fx-text-fill: green;");
                } else {
                    weightTotalLabel.setStyle("-fx-text-fill: red;");
                }
            } catch (NumberFormatException e) {
                weightTotalLabel.setText("Total Weight: Invalid input");
                weightTotalLabel.setStyle("-fx-text-fill: red;");
            }
        };
        
        // Add the listener to all weight fields
        for (TextField field : weightFields.values()) {
            field.textProperty().addListener(weightChangeListener);
        }
        
        // Trigger the listener once to initialize the total
        weightChangeListener.changed(null, null, null);
        
        // Set up event handlers
        cancelButton.setOnAction(e -> dialog.close());
        
        saveButton.setOnAction(e -> {
            try {
                double totalWeight = 0.0;
                
                // Validate weights
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
                
                // Update assessment type weights
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
     * Helper method to capitalize and format assessment type names
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
     * Show dialog to input grades for a subject
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
            
            // Create dialog
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Input Grades - " + subject.getName());
            dialog.setMinWidth(600);
            
            VBox dialogVBox = new VBox(20);
            dialogVBox.setPadding(new Insets(20));
            
            Label titleLabel = new Label("Input Grades for " + subject.getName());
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            
            // Create a scroll pane for the assessment grid
            javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(400);
            
            GridPane assessmentGrid = new GridPane();
            assessmentGrid.setHgap(15);
            assessmentGrid.setVgap(15);
            assessmentGrid.setPadding(new Insets(10));
            
            // Headers
            Label assessmentHeaderLabel = new Label("Assessment");
            assessmentHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            Label scoreHeaderLabel = new Label("Score (%)");
            scoreHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            Label finalizedHeaderLabel = new Label("Mark as Final");
            finalizedHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            assessmentGrid.add(assessmentHeaderLabel, 0, 0);
            assessmentGrid.add(scoreHeaderLabel, 1, 0);
            assessmentGrid.add(finalizedHeaderLabel, 2, 0);
            
            // Map to store references to UI controls
            Map<Integer, TextField> scoreFields = new HashMap<>();
            Map<Integer, javafx.scene.control.CheckBox> finalCheckboxes = new HashMap<>();
            
            int rowIndex = 1;
            boolean hasAssessments = false;
            
            // Add rows for each assessment
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
                
                hasAssessments = true;
                
                // Add a header for the assessment type
                Label typeLabel = new Label(assessmentType.getDisplayName() + " (" + assessmentType.getWeight() + "%)");
                typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                assessmentGrid.add(typeLabel, 0, rowIndex, 3, 1);
                rowIndex++;
                
                // Add rows for each individual assessment
                for (Assessment assessment : assessments) {
                    // Assessment name
                    String displayName = assessment.getDisplayName(assessmentType.getType());
                    Label nameLabel = new Label(displayName);
                    
                    // Score input - show current score if available
                    TextField scoreField = new TextField(String.format("%.1f", assessment.getScore()));
                    scoreField.setPrefWidth(80);
                    
                    // Store reference to the field
                    scoreFields.put(assessment.getId(), scoreField);
                    
                    // Finalized checkbox - set to current value
                    javafx.scene.control.CheckBox finalizedCheckbox = new javafx.scene.control.CheckBox();
                    finalizedCheckbox.setSelected(assessment.isFinal());
                    
                    // Store reference to the checkbox
                    finalCheckboxes.put(assessment.getId(), finalizedCheckbox);
                    
                    assessmentGrid.add(nameLabel, 0, rowIndex);
                    assessmentGrid.add(scoreField, 1, rowIndex);
                    assessmentGrid.add(finalizedCheckbox, 2, rowIndex);
                    
                    rowIndex++;
                }
            }
            
            if (!hasAssessments) {
                Label noAssessmentsLabel = new Label("No assessments found with weight > 0. Please check subject configuration.");
                assessmentGrid.add(noAssessmentsLabel, 0, rowIndex, 3, 1);
            }
            
            scrollPane.setContent(assessmentGrid);
            
            // Calculate current values
            double percentage = subject.calculateOverallPercentage();
            String letterGrade = subject.calculateLetterGrade();
            double gpa = subject.calculateGPA();
            
            // Current overall percentage
            Label currentOverallLabel = new Label(String.format(
                "Current Overall: %.1f%% | %s | GPA: %.2f",
                percentage,
                letterGrade,
                gpa
            ));
            currentOverallLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            
            // Buttons
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            
            Button cancelButton = new Button("Cancel");
            Button saveButton = new Button("Save");
            saveButton.setDefaultButton(true);
            
            buttonBox.getChildren().addAll(cancelButton, saveButton);
            
            dialogVBox.getChildren().addAll(titleLabel, scrollPane, currentOverallLabel, buttonBox);
            
            dialog.setScene(new javafx.scene.Scene(dialogVBox));
            
            // Create grade input handler
            application.utils.GradeInputHandler gradeHandler = 
                new application.utils.GradeInputHandler(subject, controller, scoreFields, finalCheckboxes, dialog);
            
            // Set up event handlers
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
     * Get the main layout of this view
     * 
     * @return The BorderPane containing the view
     */
    public BorderPane getView() {
        return mainLayout;
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
        alert.showAndWait();
    }
    
    /**
     * Refresh the view with updated data
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
}