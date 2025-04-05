package application.views;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.application.Platform;

import application.controllers.HomeController;
import application.models.Semester;
import application.models.Subject;
import application.models.User;
import application.services.UserService;

/**
 * View class for the home screen
 */
public class HomeView {
    private BorderPane mainLayout;
    private HomeController controller;
    private User currentUser;
    private FlowPane semestersPane;
    private Label overallGpaLabel;
    private TextField goalGpaField;
    private Label requiredGpaLabel;
    
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
     * Initialize the view
     */
//    private void initialize() {
//        try {
//            System.out.println("Initializing HomeView");
//            mainLayout = new BorderPane();
//            mainLayout.setPadding(new Insets(20));
//            
//            // Create header
//            Label headerLabel = new Label("Northeastern University - Grade Calculator");
//            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
//            
//            Label welcomeLabel = new Label("Welcome, " + currentUser.getFullName());
//            welcomeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
//            
//            VBox headerBox = new VBox(10);
//            headerBox.setAlignment(Pos.CENTER_LEFT);
//            headerBox.getChildren().addAll(headerLabel, welcomeLabel);
//            
//            Region spacer = new Region();
//            HBox.setHgrow(spacer, Priority.ALWAYS);
//            
//            Button logoutButton = new Button("Logout");
//            logoutButton.setOnAction(e -> controller.logout());
//            
//            HBox topBox = new HBox();
//            topBox.setAlignment(Pos.CENTER);
//            topBox.setPadding(new Insets(10));
//            topBox.setSpacing(10);
//            
//            topBox.getChildren().addAll(headerBox, spacer, logoutButton);
//            
//            mainLayout.setTop(topBox);
//            
//            // Create content
//            VBox contentBox = new VBox(20);
//            contentBox.setAlignment(Pos.CENTER);
//            contentBox.setPadding(new Insets(20));
//            
//            // GPA Summary section
//            HBox gpaSummaryBox = new HBox(20);
//            gpaSummaryBox.setAlignment(Pos.CENTER_LEFT);
//            gpaSummaryBox.setPadding(new Insets(10, 10, 20, 10));
//            
//            VBox currentGpaBox = new VBox(5);
//            Label currentGpaHeaderLabel = new Label("Current Overall GPA");
//            currentGpaHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
//            overallGpaLabel = new Label("0.00");
//            overallGpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//            currentGpaBox.getChildren().addAll(currentGpaHeaderLabel, overallGpaLabel);
//            
//            VBox goalGpaBox = new VBox(5);
//            Label goalGpaHeaderLabel = new Label("GPA Goal");
//            goalGpaHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
//            goalGpaField = new TextField("4.0");
//            goalGpaField.setPrefWidth(80);
//            goalGpaBox.getChildren().addAll(goalGpaHeaderLabel, goalGpaField);
//            
//            VBox requiredGpaBox = new VBox(5);
//            Label requiredGpaHeaderLabel = new Label("Required GPA for Future Semesters");
//            requiredGpaHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
//            requiredGpaLabel = new Label("0.00");
//            requiredGpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//            requiredGpaBox.getChildren().addAll(requiredGpaHeaderLabel, requiredGpaLabel);
//            
//            Button calculateButton = new Button("Calculate");
//            calculateButton.setStyle("-fx-font-size: 14px;");
//            VBox calculateBox = new VBox();
//            calculateBox.setAlignment(Pos.CENTER);
//            calculateBox.getChildren().add(calculateButton);
//            
//            gpaSummaryBox.getChildren().addAll(currentGpaBox, goalGpaBox, calculateBox, requiredGpaBox);
//            
//            // Semesters section
//            Label semestersLabel = new Label("Your Semesters");
//            semestersLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
//            
//            // Create a FlowPane for semester cards
//            semestersPane = new FlowPane();
//            semestersPane.setHgap(20);
//            semestersPane.setVgap(20);
//            semestersPane.setAlignment(Pos.CENTER);
//            semestersPane.setPrefWidth(800);
//            
//            // Add button
//            Button addSemesterButton = new Button("Add Semester");
//            addSemesterButton.setStyle("-fx-font-size: 14px;");
//            addSemesterButton.setPrefWidth(150);
//            HBox addButtonBox = new HBox();
//            addButtonBox.setAlignment(Pos.CENTER);
//            addButtonBox.setPadding(new Insets(20, 0, 0, 0));
//            addButtonBox.getChildren().add(addSemesterButton);
//            
//            contentBox.getChildren().addAll(gpaSummaryBox, semestersLabel, semestersPane, addButtonBox);
//            
//            mainLayout.setCenter(contentBox);
//            
//            // Set up event handlers
//            addSemesterButton.setOnAction(e -> showAddSemesterDialog());
//            calculateButton.setOnAction(e -> calculateRequiredGPA());
//            
//            // Load semesters with fresh data from the database
//            loadSemesters();
//            
//            // Register for data change events to refresh when needed
////            application.utils.EventBus.getInstance().register(
////                application.utils.DataChangedEvent.class,
////                event -> javafx.application.Platform.runLater(this::loadSemesters)
////            );
//             application.utils.EventBus.getInstance().register(
//            	    application.utils.DataChangedEvent.class,
//            	    event -> {
//            	        System.out.println("DataChangedEvent received in HomeView - refreshing UI");
//            	        Platform.runLater(this::forceUpdateUI);
//            	    }
//            	);
//            
//            System.out.println("HomeView initialization complete");
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Error initializing HomeView: " + e.getMessage());
//        }
//    }
    private void initialize() {
        try {
            System.out.println("Initializing HomeView");
            mainLayout = new BorderPane();
            mainLayout.setPadding(new Insets(20));
            
            // Create header
            Label headerLabel = new Label("Northeastern University - Grade Calculator");
            headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            
            Label welcomeLabel = new Label("Welcome, " + currentUser.getFullName());
            welcomeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            
            VBox headerBox = new VBox(10);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            headerBox.getChildren().addAll(headerLabel, welcomeLabel);
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Button logoutButton = new Button("Logout");
            logoutButton.setOnAction(e -> controller.logout());
            
            HBox topBox = new HBox();
            topBox.setAlignment(Pos.CENTER);
            topBox.setPadding(new Insets(10));
            topBox.setSpacing(10);
            
            topBox.getChildren().addAll(headerBox, spacer, logoutButton);
            
            mainLayout.setTop(topBox);
            
            // Create content
            VBox contentBox = new VBox(20);
            contentBox.setAlignment(Pos.CENTER);
            contentBox.setPadding(new Insets(20));
            
            // GPA Summary section
            HBox gpaSummaryBox = new HBox(20);
            gpaSummaryBox.setAlignment(Pos.CENTER_LEFT);
            gpaSummaryBox.setPadding(new Insets(10, 10, 20, 10));
            
            VBox currentGpaBox = new VBox(5);
            Label currentGpaHeaderLabel = new Label("Current Overall GPA");
            currentGpaHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            overallGpaLabel = new Label("0.00");
            overallGpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            currentGpaBox.getChildren().addAll(currentGpaHeaderLabel, overallGpaLabel);
            
            VBox goalGpaBox = new VBox(5);
            Label goalGpaHeaderLabel = new Label("GPA Goal");
            goalGpaHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            goalGpaField = new TextField("4.0");
            goalGpaField.setPrefWidth(80);
            goalGpaBox.getChildren().addAll(goalGpaHeaderLabel, goalGpaField);
            
            VBox requiredGpaBox = new VBox(5);
            Label requiredGpaHeaderLabel = new Label("Required GPA for Future Semesters");
            requiredGpaHeaderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            requiredGpaLabel = new Label("0.00");
            requiredGpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            requiredGpaBox.getChildren().addAll(requiredGpaHeaderLabel, requiredGpaLabel);
            
            Button calculateButton = new Button("Calculate");
            calculateButton.setStyle("-fx-font-size: 14px;");
            VBox calculateBox = new VBox();
            calculateBox.setAlignment(Pos.CENTER);
            calculateBox.getChildren().add(calculateButton);
            
            gpaSummaryBox.getChildren().addAll(currentGpaBox, goalGpaBox, calculateBox, requiredGpaBox);
            
            // Semesters section
            Label semestersLabel = new Label("Your Semesters");
            semestersLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            
            // Create a FlowPane for semester cards
            semestersPane = new FlowPane();
            semestersPane.setHgap(20);
            semestersPane.setVgap(20);
            semestersPane.setAlignment(Pos.CENTER);
            semestersPane.setPrefWidth(800);
            
            // Add button
            Button addSemesterButton = new Button("Add Semester");
            addSemesterButton.setStyle("-fx-font-size: 14px;");
            addSemesterButton.setPrefWidth(150);
            HBox addButtonBox = new HBox();
            addButtonBox.setAlignment(Pos.CENTER);
            addButtonBox.setPadding(new Insets(20, 0, 0, 0));
            addButtonBox.getChildren().add(addSemesterButton);
            
            contentBox.getChildren().addAll(gpaSummaryBox, semestersLabel, semestersPane, addButtonBox);
            
            mainLayout.setCenter(contentBox);
            
            // Set up event handlers
            addSemesterButton.setOnAction(e -> showAddSemesterDialog());
            calculateButton.setOnAction(e -> calculateRequiredGPA());
            
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
     * Load and display all semesters for the current user with fresh data from the database
     */
//    private void loadSemesters() {
//        try {
//            System.out.println("\n===== LOADING SEMESTERS WITH FRESH DATA =====");
//            
//            // Get fresh data from the database
//            List<Semester> semesters = controller.getAllSemesters();
//            semestersPane.getChildren().clear();
//            
//            System.out.println("Found " + semesters.size() + " semesters");
//            
//            if (semesters.isEmpty()) {
//                Label noSemestersLabel = new Label("You don't have any semesters yet");
//                noSemestersLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
//                semestersPane.getChildren().add(noSemestersLabel);
//            } else {
//                // Add each semester card to the UI
//                for (Semester semester : semesters) {
//                    semestersPane.getChildren().add(createSemesterCard(semester));
//                }
//            }
//            
//            // Calculate and update the overall GPA separately
//            updateOverallGPA();
//            
//            System.out.println("===== SEMESTERS LOADED SUCCESSFULLY =====\n");
//        } catch (Exception e) {
//            System.err.println("Error loading semesters: " + e.getMessage());
//            e.printStackTrace();
//            showErrorAlert("Error", "Failed to load semesters: " + e.getMessage());
//        }
//    }
    
 // Replace the existing loadSemesters() method with this version
    private void loadSemesters() {
        try {
            System.out.println("\n===== LOADING SEMESTERS WITH FRESH DATA =====");
            
            // Get fresh data from the database
            List<Semester> semesters = controller.getAllSemesters();
            semestersPane.getChildren().clear();
            
            System.out.println("Found " + semesters.size() + " semesters");
            
            if (semesters.isEmpty()) {
                Label noSemestersLabel = new Label("You don't have any semesters yet");
                noSemestersLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                semestersPane.getChildren().add(noSemestersLabel);
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
                    VBox semesterCard = createSemesterCardWithForcedGPA(semester, semesterGPA);
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
//    
//    /**
//     * Create a visual card for a semester with correct GPA
//     * 
//     * @param semester The semester
//     * @return The VBox representing the semester card
//     */
//    private VBox createSemesterCard(Semester semester) {
//        // Force recalculation of semester GPA
//        double gpa = semester.calculateGPA();
//        
//        System.out.println("Creating card for semester: " + semester.getName() + 
//                          ", ID: " + semester.getId() + 
//                          ", GPA: " + gpa);
//        
//        VBox card = new VBox(10);
//        card.setPadding(new Insets(15));
//        card.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
//        card.setPrefWidth(250);
//        card.setPrefHeight(150);
//        
//        // Header with semester name and delete button
//        HBox headerBox = new HBox(10);
//        headerBox.setAlignment(Pos.CENTER_LEFT);
//        
//        Label nameLabel = new Label(semester.getName());
//        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
//        
//        Region spacer = new Region();
//        HBox.setHgrow(spacer, Priority.ALWAYS);
//        
//        Button deleteButton = new Button("X");
//        deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 25px; -fx-min-height: 25px; -fx-max-width: 25px; -fx-max-height: 25px; -fx-padding: 0;");
//        
//        headerBox.getChildren().addAll(nameLabel, spacer, deleteButton);
//        
//        // Display GPA with the recalculated value
//        Label gpaLabel = new Label(String.format("GPA: %.2f", gpa));
//        gpaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
//        
//        // Subject count
//        Label subjectsLabel = new Label(String.format("Subjects: %d", semester.getSubjects().size()));
//        
//        // Button to view details
//        Button viewButton = new Button("View Details");
//        viewButton.setStyle("-fx-font-size: 12px;");
//        viewButton.setOnAction(e -> controller.navigateToSemesterDetails(semester));
//        
//        // Spacer to push the button to the bottom
//        Region verticalSpacer = new Region();
//        VBox.setVgrow(verticalSpacer, Priority.ALWAYS);
//        
//        card.getChildren().addAll(headerBox, gpaLabel, subjectsLabel, verticalSpacer, viewButton);
//        
//        // Set up delete button event handler
//        deleteButton.setOnAction(e -> showDeleteSemesterConfirmation(semester));
//        
//        return card;
//    }
    
    /**
     * Create a semester card with a directly provided GPA value
     * This bypasses any calculation issues
     * 
     * @param semester The semester
     * @param forcedGPA The pre-calculated GPA to display
     * @return The semester card VBox
     */
    private VBox createSemesterCardWithForcedGPA(Semester semester, double forcedGPA) {
        System.out.println("Creating semester card with forced GPA: " + forcedGPA);
        
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(250);
        card.setPrefHeight(150);
        
        // Header with semester name and delete button
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(semester.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button deleteButton = new Button("X");
        deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 25px; -fx-min-height: 25px; -fx-max-width: 25px; -fx-max-height: 25px; -fx-padding: 0;");
        
        headerBox.getChildren().addAll(nameLabel, spacer, deleteButton);
        
        // Use the forced GPA value directly
        Label gpaLabel = new Label(String.format("GPA: %.2f", forcedGPA));
        gpaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        // Subject count
        Label subjectsLabel = new Label(String.format("Subjects: %d", semester.getSubjects().size()));
        
        // Button to view details
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-font-size: 12px;");
        viewButton.setOnAction(e -> controller.navigateToSemesterDetails(semester));
        
        // Spacer to push the button to the bottom
        Region verticalSpacer = new Region();
        VBox.setVgrow(verticalSpacer, Priority.ALWAYS);
        
        card.getChildren().addAll(headerBox, gpaLabel, subjectsLabel, verticalSpacer, viewButton);
        
        // Set up delete button event handler
        deleteButton.setOnAction(e -> showDeleteSemesterConfirmation(semester));
        
        return card;
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
        dialogVBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Add New Semester");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Label nameLabel = new Label("Semester Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Fall 2024");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelButton = new Button("Cancel");
        Button addButton = new Button("Add");
        addButton.setDefaultButton(true);
        
        buttonBox.getChildren().addAll(cancelButton, addButton);
        
        dialogVBox.getChildren().addAll(titleLabel, nameLabel, nameField, buttonBox);
        
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
                requiredGpaLabel.setTextFill(Color.RED);
            } else if (requiredGPA > 3.0) {
                requiredGpaLabel.setTextFill(Color.ORANGE);
            } else {
                requiredGpaLabel.setTextFill(Color.GREEN);
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Error", "Please enter a valid number for GPA goal");
        } catch (Exception e) {
            showErrorAlert("Error", e.getMessage());
        }
    }
    
    /**
     * Calculate and display the overall GPA across all semesters
     */
    private void updateOverallGPA() {
        try {
            System.out.println("\n===== CALCULATING OVERALL GPA FOR HOME VIEW =====");
            
            // Get all semesters with fresh data
            List<Semester> semesters = controller.getAllSemesters();
            
            double totalGPA = 0.0;
            int validSemesterCount = 0;
            
            System.out.println("Processing " + semesters.size() + " semesters for overall GPA");
            
            for (Semester semester : semesters) {
                // Calculate semester GPA directly to ensure it's fresh
                double semesterGPA = 0.0;
                int validSubjectCount = 0;
                
                System.out.println("  Semester: " + semester.getName() + ", ID: " + semester.getId());
                
                for (Subject subject : semester.getSubjects()) {
                    // Calculate subject GPA
                    double subjectGPA = subject.calculateGPA();
                    System.out.println("    Subject: " + subject.getName() + ", GPA: " + subjectGPA);
                    
                    if (subjectGPA > 0) {
                        semesterGPA += subjectGPA;
                        validSubjectCount++;
                    }
                }
                
                // Calculate average for this semester
                double averageSemesterGPA = validSubjectCount > 0 ? semesterGPA / validSubjectCount : 0.0;
                System.out.println("  Semester average GPA: " + averageSemesterGPA);
                
                if (averageSemesterGPA > 0) {
                    totalGPA += averageSemesterGPA;
                    validSemesterCount++;
                }
            }
            
            // Calculate overall GPA
            double overallGPA = validSemesterCount > 0 ? totalGPA / validSemesterCount : 0.0;
            System.out.println("Overall GPA across all semesters: " + overallGPA);
            
            // Update the UI
            overallGpaLabel.setText(String.format("%.2f", overallGPA));
            
            System.out.println("===== OVERALL GPA CALCULATION COMPLETE =====\n");
        } catch (Exception e) {
            System.err.println("Error calculating overall GPA: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Force update of all UI elements with fresh data
     * This is a more direct approach when event-based updates aren't working
     */
//    public void forceUpdateUI() {
//        try {
//            System.out.println("\n===== FORCE UPDATING HOME VIEW UI =====");
//            
//            // Clear the semesters pane
//            semestersPane.getChildren().clear();
//            
//            // Get fresh data from the database
//            List<Semester> semesters = controller.getAllSemesters();
//            
//            if (semesters.isEmpty()) {
//                Label noSemestersLabel = new Label("You don't have any semesters yet");
//                noSemestersLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
//                semestersPane.getChildren().add(noSemestersLabel);
//            } else {
//                // Calculate overall GPA directly here
//                double totalGPA = 0.0;
//                int validSemesterCount = 0;
//                
//                for (Semester semester : semesters) {
//                    // Force GPA calculation for each semester
//                    double semesterGPA = semester.calculateGPA();
//                    System.out.println("Semester: " + semester.getName() + ", GPA: " + semesterGPA);
//                    
//                    // Only count valid GPAs
//                    if (semesterGPA > 0) {
//                        totalGPA += semesterGPA;
//                        validSemesterCount++;
//                    }
//                    
//                    // Add semester card with forced GPA calculation
//                    VBox semesterCard = createSemesterCardWithForcedGPA(semester, semesterGPA);
//                    semestersPane.getChildren().add(semesterCard);
//                }
//                
//                // Update overall GPA label
//                double overallGPA = validSemesterCount > 0 ? totalGPA / validSemesterCount : 0.0;
//                System.out.println("Overall GPA (direct calculation): " + overallGPA);
//                
//                // Update the label directly
//                Platform.runLater(() -> {
//                    overallGpaLabel.setText(String.format("%.2f", overallGPA));
//                    System.out.println("Overall GPA label updated to: " + overallGpaLabel.getText());
//                });
//            }
//            
//            System.out.println("===== HOME VIEW UI UPDATE COMPLETE =====\n");
//        } catch (Exception e) {
//            System.err.println("Error during force UI update: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
    
 // Replace the existing forceUpdateUI() method with this implementation
    public void forceUpdateUI() {
        try {
            System.out.println("\n===== FORCE UPDATING HOME VIEW UI =====");
            
            // Clear the semesters pane
            semestersPane.getChildren().clear();
            
            // Get fresh data from the database
            List<Semester> semesters = controller.getAllSemesters();
            
            if (semesters.isEmpty()) {
                Label noSemestersLabel = new Label("You don't have any semesters yet");
                noSemestersLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                semestersPane.getChildren().add(noSemestersLabel);
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
                    
                    // Add semester card with forced GPA calculation
                    VBox semesterCard = createSemesterCardWithForcedGPA(semester, semesterGPA);
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
                Label noSemestersLabel = new Label("You don't have any semesters yet");
                noSemestersLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
                semestersPane.getChildren().add(noSemestersLabel);
                
                // Set overall GPA to 0
                overallGpaLabel.setText("0.00");
                System.out.println("No semesters found, overall GPA set to 0.00");
            } else {
                // Process each semester
                for (Semester semester : semesters) {
                    // Force GPA calculation
                    double semesterGPA = semester.calculateGPA();
                    System.out.println("Semester: " + semester.getName() + " - GPA: " + semesterGPA);
                    
                    // Create a card with the forced GPA
                    VBox card = new VBox(10);
                    card.setPadding(new Insets(15));
                    card.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
                    card.setPrefWidth(250);
                    card.setPrefHeight(150);
                    
                    // Header with semester name and delete button
                    HBox headerBox = new HBox(10);
                    headerBox.setAlignment(Pos.CENTER_LEFT);
                    
                    Label nameLabel = new Label(semester.getName());
                    nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Button deleteButton = new Button("X");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 25px; -fx-min-height: 25px; -fx-max-width: 25px; -fx-max-height: 25px; -fx-padding: 0;");
                    
                    headerBox.getChildren().addAll(nameLabel, spacer, deleteButton);
                    
                    // Use the forced GPA value directly
                    Label gpaLabel = new Label(String.format("GPA: %.2f", semesterGPA));
                    gpaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
                    
                    // Subject count
                    Label subjectsLabel = new Label(String.format("Subjects: %d", semester.getSubjects().size()));
                    
                    // Button to view details
                    Button viewButton = new Button("View Details");
                    viewButton.setStyle("-fx-font-size: 12px;");
                    final Semester finalSemester = semester; // Need final for lambda
                    viewButton.setOnAction(e -> controller.navigateToSemesterDetails(finalSemester));
                    
                    // Spacer to push the button to the bottom
                    Region verticalSpacer = new Region();
                    VBox.setVgrow(verticalSpacer, Priority.ALWAYS);
                    
                    card.getChildren().addAll(headerBox, gpaLabel, subjectsLabel, verticalSpacer, viewButton);
                    
                    // Set up delete button event handler
                    final Semester deleteSemester = semester; // Need final for lambda
                    deleteButton.setOnAction(e -> showDeleteSemesterConfirmation(deleteSemester));
                    
                    // Add to semester pane
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
    
    public BorderPane getView() {
        return mainLayout;
    }
    
}